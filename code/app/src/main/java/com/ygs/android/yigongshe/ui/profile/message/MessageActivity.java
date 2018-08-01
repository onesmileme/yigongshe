package com.ygs.android.yigongshe.ui.profile.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import retrofit2.Response;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.MsgItemBean;
import com.ygs.android.yigongshe.bean.MsgListBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import com.ygs.android.yigongshe.view.SegmentControlView;

import java.util.List;

/**
 * 我的消息
 */
public class MessageActivity extends BaseActivity {

  @BindView(R.id.titlebar) CommonTitleBar titleBar;

  @BindView(R.id.message_segment) SegmentControlView segmentControlView;

  @BindView(R.id.message_list) RecyclerView recyclerView;

  @BindView(R.id.message_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

  private MessageAdapter messageAdapter;

  private List<MsgItemBean> privateMsgList;
  private List<MsgItemBean> noticeMsgList;

  private LinkCall<BaseResultDataInfo<MsgListBean>> mCall;

  @Override
  protected void initIntent(Bundle bundle) {


  }

  @Override
  protected void initView() {

    titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override
      public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
          finish();
        }else if(action == CommonTitleBar.ACTION_RIGHT_TEXT || action == CommonTitleBar.ACTION_RIGHT_BUTTON){
          doOperate();
        }
      }
    });

    segmentControlView.setOnSegmentChangedListener(
        new SegmentControlView.OnSegmentChangedListener() {
          @Override public void onSegmentChanged(int newSelectedIndex) {
            changeSegment(newSelectedIndex);
          }
        });
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        loadMessage(false , segmentControlView.getSelectedIndex() == 0);
      }
    });
    messageAdapter = new MessageAdapter(this);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(messageAdapter);
    messageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        showTalk(position);
      }
    });

    loadMessage(true , segmentControlView.getSelectedIndex() == 0);
  }

  @Override
  protected int getLayoutResId() {
    return R.layout.activity_message;
  }

  private void changeSegment(int position) {
    switch (position){
      case 0:{
        if (privateMsgList == null || privateMsgList.size() == 0){
          loadMessage(true , true);
        }
        messageAdapter.setNewData(privateMsgList);

        break;
      }
      case 1:{
        if (noticeMsgList == null || noticeMsgList.size() == 0){
          loadMessage(true,false);
        }
        messageAdapter.setNewData(noticeMsgList);
        break;
      }
    }

  }

  private void doOperate(){

  }

  private void loadMessage(boolean showRefresh, final boolean isPrivateMsg){

    if (showRefresh){
      swipeRefreshLayout.setRefreshing(true);
    }


    //final boolean isPrivateMsg = segmentControlView.getSelectedIndex() == 0;
    String type = isPrivateMsg ? "message" : "notice";

    String token = YGApplication.accountManager.getToken();

    mCall =  LinkCallHelper.getApiService().getMessageList(token,type);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MsgListBean>>(){
      @Override
      public void onResponse(BaseResultDataInfo<MsgListBean> entity, Response<?> response, Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == ApiStatusInterface.OK){

          if (isPrivateMsg){
            privateMsgList = entity.data.list;
          }else{
            noticeMsgList = entity.data.list;
          }
          messageAdapter.setNewData(entity.data.list);

        }else{
          String msg = "请求消息失败";
          if (entity != null){
            msg += "("+entity.msg+")";
          }
          Toast.makeText(MessageActivity.this,msg,Toast.LENGTH_SHORT).show();
        }
        swipeRefreshLayout.setRefreshing(false);
      }
    });
  }

  private void showTalk(int index){
    MsgItemBean itemBean = null;
    String type ;
    if (segmentControlView.getSelectedIndex() == 0){
      itemBean = privateMsgList.get(index);
      type = "message";
    }else{
      itemBean = noticeMsgList.get(index);
      type = "notice";
    }

    Intent intent = new Intent(this,MsgTalkActivity.class);
    if (itemBean.other_id != null){
      intent.putExtra("otherUid",itemBean.other_id);
    }
    if (itemBean.message_key != null){
      intent.putExtra("messageKey",itemBean.message_key);
    }

    if (itemBean.username != null){
      intent.putExtra("name",itemBean.username);
    }

    intent.putExtra("type",type);

    startActivity(intent);

  }

}
