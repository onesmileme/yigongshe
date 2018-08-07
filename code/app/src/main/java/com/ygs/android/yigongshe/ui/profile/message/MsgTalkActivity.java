package com.ygs.android.yigongshe.ui.profile.message;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.TalkItemBean;
import com.ygs.android.yigongshe.bean.TalkListItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Response;

public class MsgTalkActivity extends BaseActivity {

    private String otherUid;
    private String type;
    private String messageKey;
    private String name;

    @BindView(R.id.titlebar) CommonTitleBar titleBar;

    @BindView(R.id.input_text) EditText editText;

    @BindView(R.id.send) Button sendButton;

    @BindView(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.talk_recyclerview) RecyclerView recyclerView;

    private MsgTalkAdapter talkAdapter;

    private List<TalkItemBean> talkItemBeans;

    private LinkCall<BaseResultDataInfo<TalkListItemBean>> talksCall;
    private LinkCall<BaseResultDataInfo<TalkItemBean>> sendCall;

    @Override
    protected  void initIntent(Bundle bundle){

        otherUid = bundle.getString("otherUid");
        type = bundle.getString("type");
        messageKey = bundle.getString("messageKey");
        if (type == null){
            type = "message";
        }
        name = bundle.getString("name");
    }

    @Override
    protected void initView(){

        if (name != null){
            TextView titleView = titleBar.getCenterTextView();
            titleView.setText(name);
        }

        talkAdapter = new MsgTalkAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(talkAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTalks(true);
            }
        });
        talkAdapter.enableLoadMoreEndClick(false);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTalk(editText.getText().toString());
            }
        });

        swipeRefreshLayout.setRefreshing(true);
        loadTalks(false);
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_msg_talk;
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideSoftKeyboard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (talksCall != null && !talksCall.isCanceled()){
            talksCall.cancel();
        }
    }

    private void tryGetOtherId(){
        if (otherUid != null){
            return;
        }
        String otherName = null;
        String myuid = YGApplication.accountManager.getUserid()+"";
        for (TalkItemBean item:talkItemBeans) {
            if (myuid.equals(item.sender_id)){
                otherUid = item.receiver_id;
                otherName = item.receiver_name;
                break;
            }else if(myuid.equals(item.receiver_id)){
                otherUid = item.sender_id;
                otherName = item.sender_name;
                break;
            }
        }

        if (name == null){
            name = otherName;
            TextView titleView = titleBar.getCenterTextView();
            titleView.setText(name);
        }
    }

    private void loadTalks(final boolean loadHistory ){

        String token = YGApplication.accountManager.getToken();
        String lastId = null;
        if (talkItemBeans != null && talkItemBeans.size() > 0){
            TalkItemBean itemBean = talkItemBeans.get(0);
            lastId = itemBean.messageid;
        }
        talksCall = LinkCallHelper.getApiService().getTalkList(messageKey,otherUid,token,type,lastId);
        talksCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<TalkListItemBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<TalkListItemBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK){

                    if (entity.getData().list != null){
                        talkItemBeans = new LinkedList<>();
                        talkItemBeans.addAll(entity.getData().list);
                        talkItemBeans.addAll(talkAdapter.getData());
                        talkAdapter.setNewData(talkItemBeans);
                        tryGetOtherId();
                        if (loadHistory){
                            recyclerView.scrollToPosition(0);
                        }
                    }

                }else{
                    String msg = "请求消息失败";
                    if (entity != null && entity.msg != null){
                        msg += "("+entity.msg+")";
                    }
                    Toast.makeText(MsgTalkActivity.this,msg,Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    private void sendTalk(String content){


        if (content == null || content.length() == 0){
            return;
        }

        String token = YGApplication.accountManager.getToken();
        sendCall = LinkCallHelper.getApiService().sendTalkItem(token,content,otherUid);
        sendCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<TalkItemBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<TalkItemBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK){
                    //append to last
                    if (entity.data != null) {
                        talkItemBeans.add(entity.getData());
                        talkAdapter.setNewData(talkItemBeans);
                        recyclerView.scrollToPosition(talkItemBeans.size() - 1);
                        editText.setText(null);
                    }
                }else{
                    String msg = "发送失败";
                    if (entity != null && entity.msg != null){
                        msg += "("+entity.msg+")";
                    }
                    Toast.makeText(MsgTalkActivity.this,msg,Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void hideSoftKeyboard() {
        // 隐藏软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null && editText != null && imm.isActive(editText)) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    private String getCurrTime() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd  HH:mm");
        Date now = new Date();
        return dateFormat.format(now);
    }
}
