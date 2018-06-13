package com.weikan.app.personalcenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.bean.UserInfoObject;
import com.weikan.app.face.FaceRelativeLayout;
import com.weikan.app.personalcenter.adapter.TalkAdapter;
import com.weikan.app.personalcenter.bean.SendTalkObject;
import com.weikan.app.personalcenter.bean.TalkObject;
import com.weikan.app.util.URLDefine;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.responsehandler.ResponseHandler;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liujian on 16/11/13.
 */
public class TalkActivity extends BaseActivity implements View.OnClickListener {

    public static final String TYPE_NEW = "new";
    public static final String TYPE_NEXT = "next";

    private PullToRefreshListView mPullRefreshListView;
    private TalkAdapter mAdapter;
    private EditText editText;

    private List<TalkObject.TalkContent> contentList = new LinkedList<>();

    private String hisUid = "";

    private String curContent;

    private String last_read_mid;
    private boolean isShowTimeLine = false;

    private ListView actualListView;
    private String uname = null;
    private FaceRelativeLayout mFaceRelativeLayout;
    private View detailview;
    private ImageView mUserbut;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);


        updateData();
        contentList.clear();
        findViewById(R.id.iv_titlebar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                finish();
            }
        });

        titleText = (TextView) findViewById(R.id.tv_titlebar_title);
        if (uname != null) {
            titleText.setText(uname);
        } else {
            titleText.setText("");
        }

        mUserbut = (ImageView) findViewById(R.id.uv_titlebar_right);
        mUserbut.setVisibility(View.VISIBLE);
        mUserbut.setOnClickListener(this);


        detailview = (View) findViewById(R.id.detailview);
        detailview.setOnClickListener(this);

        mFaceRelativeLayout = (FaceRelativeLayout) findViewById(R.id.rl_face);
        mFaceRelativeLayout.setOnhideSoftKeyboardListener(new FaceRelativeLayout.OnhideSoftKeyboardListener() {
            public void onHideSoftKeyboard() {
                hideSoftKeyboard();
            }

            @Override
            public void onSpannableString(SpannableString spannableString) {

            }
        });

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.topics_pull_list_view);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
                String label = DateUtils.formatDateTime(TalkActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                listViewPullToRefreshBase.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (contentList.size() > 0) {
                    sendRequestWithLastMid(contentList.get(0).mid);
                } else {
                    mPullRefreshListView.onRefreshComplete();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
                String label = DateUtils.formatDateTime(TalkActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                listViewPullToRefreshBase.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                //不需要
//                sendRequestWithFirstMid(hisUid);
            }
        });

        mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
//                Toast.makeText(DiscussFragment.this.getActivity(), "End of List!", Toast.LENGTH_SHORT).show();
            }
        });
        actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.setDivider(null);
        actualListView.setDividerHeight(0);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        registerForContextMenu(actualListView);
        mAdapter = new TalkAdapter(this, contentList);

        actualListView.setAdapter(mAdapter);

        editText = (EditText) findViewById(R.id.ed_dis_detail);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        editText.setOnClickListener(this);
        editText.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showshowSoftKeyboard();
                    // 此处为得到焦点时的处理内容
                } else {
                    hideSoftKeyboard();
                    // 此处为失去焦点时的处理内容
                }
            }
        });
        Button button = (Button) findViewById(R.id.bt_dis_detail_pub);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curContent = editText.getText().toString();
                if (!TextUtils.isEmpty(curContent)) {
                    sendTalkRequest(curContent);
                    if (mAdapter.getCount() > 0) {
                        actualListView.setSelection(mAdapter.getCount() - 1);
                    }
                    editText.setText("");
//                    hideSoftKeyboard();
                }
            }
        });

        sendRequestWithLastMid("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSoftKeyboard();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        EventBus.getDefault().register(this);
    }

    private void updateData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            hisUid = bundle.getString(URLDefine.UID);
            uname = bundle.getString(URLDefine.UNAME);
        }
    }

    private void sendRequestWithLastMid(@NonNull String lastMid) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.TALK_DETAIL);


        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.SEARCH_UID, hisUid);
        //不传就是取最新的
        if (!TextUtils.isEmpty(lastMid)) {
            params.put(URLDefine.TYPE, TYPE_NEXT);
            params.put(URLDefine.LAST_MID, lastMid);
        }
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());

        HttpUtils.get(builder.build().toString(), params, httpHandler);
    }

    private void sendTalkRequest(String content) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.SEND_TALK);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TO_UID, hisUid);
        params.put(URLDefine.CONTENT, content);

        HttpUtils.get(builder.build().toString(), params, sendMsgHttpHandler);
    }


    private ResponseHandler httpHandler = new JsonResponseHandler<TalkObject>() {

        @Override
        public void success(@NonNull TalkObject data) {
            if (data.content != null) {
                //加载更多，从最前面加
                last_read_mid = data.last_read_mid;
                if (contentList.size() > 0 && TextUtils.isEmpty(contentList.get(0).mid)) {
                    contentList.clear();
                }

                contentList.addAll(0, data.content);
                mAdapter.notifyDataSetChanged();

                actualListView.setSelection(contentList.size() - 1);
            } else {
                Toast.makeText(TalkActivity.this.getApplicationContext(), "获取数据失败。", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void end() {
            super.end();
            mPullRefreshListView.onRefreshComplete();
        }
    };

    private ResponseHandler sendMsgHttpHandler = new JsonResponseHandler<SendTalkObject>() {

        @Override
        public void success(@NonNull SendTalkObject data) {
            long mid = data.getMid();
            if(mid > 0){
                TalkObject.TalkContent talkContent = new TalkObject.TalkContent();
                UserInfoObject.UserInfoContent userInfo = AccountManager.getInstance().getUserData();
                talkContent.headimgurl =  userInfo != null ? userInfo.headimgurl : "";
                talkContent.mid = mid+"";
                talkContent.content = data.getContent();
                talkContent.from_uid = AccountManager.getInstance().getUserId();
                talkContent.to_uid = hisUid;
                talkContent.ctime = System.currentTimeMillis()/1000;
                contentList.add(talkContent);
                mAdapter.notifyDataSetChanged();
                if (actualListView != null)
                    actualListView.setSelection(contentList.size() - 1);
            }else {
                Toast.makeText(TalkActivity.this.getApplicationContext(), "发送失败。", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        switch (view.getId()) {
            default:
                break;
            case R.id.detailview:
                hideSoftKeyboard();
                break;
            case R.id.ed_dis_detail:
                showshowSoftKeyboard();
                break;
            case R.id.uv_titlebar_right:
                Intent intent = UserHomeActivity.makeIntent(this,hisUid);
                startActivity(intent);
                break;
        }

    }

    public void showshowSoftKeyboard() {
        detailview.setVisibility(View.VISIBLE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null && editText != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        }
        mFaceRelativeLayout.hideFaceView();
    }


    public void hideSoftKeyboard() {
        // 隐藏软键盘
        detailview.setVisibility(View.GONE);
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
