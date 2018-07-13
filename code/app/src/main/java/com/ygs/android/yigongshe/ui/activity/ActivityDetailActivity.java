package com.ygs.android.yigongshe.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.ActivityDetailResponse;
import com.ygs.android.yigongshe.bean.response.HelpVideoListResponse;
import com.ygs.android.yigongshe.bean.response.SigninResponse;
import com.ygs.android.yigongshe.bean.response.SignupResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseDetailActivity;
import com.ygs.android.yigongshe.view.DaCallView;
import com.ygs.android.yigongshe.view.HelpVideoView;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/27.
 */

public class ActivityDetailActivity extends BaseDetailActivity {
  private LinkCall<BaseResultDataInfo<ActivityDetailResponse>> mCall;
  private DaCallView mDaCallView;
  private HelpVideoView mHelpVideoView;
  private WebView mWebView;
  private WebView mWebview2;
  private RelativeLayout mRlWebview;
  private TextView mSeeFull;
  private LinkCall<BaseResultDataInfo<HelpVideoListResponse>> mHelpVideoCall;

  @BindView(R.id.signup) TextView mSignup; //报名
  @BindView(R.id.signin) TextView mSignin; //签到

  @Override protected void initIntent(Bundle bundle) {
    mId = bundle.getInt("activity_id");
    mTitle = bundle.getString("activity_title");
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_activity_detail;
  }

  @Override protected void initView() {
    super.initView();
    mTitleBar.getCenterTextView().setText(mTitle);
    requestDetailData();
    requestHelpVideoData();
    requestCommentData(TYPE_ACTIVITY, true);
  }

  private void requestDetailData() {
    mCall = LinkCallHelper.getApiService().getActivityDetail(mId);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ActivityDetailResponse>>() {
      @Override public void onResponse(BaseResultDataInfo<ActivityDetailResponse> entity,
          Response<?> response, Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          ActivityDetailResponse data = entity.data;
          if (data != null) {
            mWebView.loadDataWithBaseURL(null, data.content, "text/html", "utf-8", null);
            mWebview2.loadDataWithBaseURL(null, data.content, "text/html", "utf-8", null);
            mDaCallView.setDacallViewData(data);
          }
        }
      }
    });
  }

  private void requestHelpVideoData() {
    mHelpVideoCall = LinkCallHelper.getApiService().getHelpVideoList(0, 20, mId);
    mHelpVideoCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<HelpVideoListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<HelpVideoListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null
            && entity.error == 2000
            && entity.data != null
            && entity.data.video_list != null
            && entity.data.video_list.size() > 0) {
          HelpVideoListResponse data = entity.data;
          mHelpVideoView.setVisibility(View.VISIBLE);
          mHelpVideoView.setHelpVideoData(ActivityDetailActivity.this, data.video_list.get(0));
        } else {
          mHelpVideoView.setVisibility(View.GONE);
        }
      }
    });
  }

  protected void addHeaderView() {
    mWebView = new WebView(this);
    mAdapter.addHeaderView(mWebView);
    mWebView.setVisibility(View.GONE);
    mRlWebview = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.view_webview, null);
    mAdapter.addHeaderView(mRlWebview);
    mRlWebview.setVisibility(View.VISIBLE);
    mSeeFull = mRlWebview.findViewById(R.id.seefull);
    mWebview2 = mRlWebview.findViewById(R.id.webview);
    mSeeFull.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mWebView.setVisibility(View.VISIBLE);
        mRlWebview.setVisibility(View.GONE);
      }
    });
    mDaCallView = new DaCallView(this, mRecyclerView);
    mAdapter.addHeaderView(mDaCallView.getView());
    mHelpVideoView = new HelpVideoView(this, mRecyclerView, mId);
    mAdapter.addHeaderView(mHelpVideoView.getView());
  }

  @Override protected void onStop() {
    if (mCall != null) {
      mCall.cancel();
    }
    super.onStop();
  }

  @OnClick({ R.id.signup, R.id.signin }) public void onBtnClicked(View view) {
    AccountManager accountManager = YGApplication.accountManager;
    if (TextUtils.isEmpty(accountManager.getToken())) {
      Toast.makeText(this, "没有登录", Toast.LENGTH_SHORT).show();
      return;
    }

    switch (view.getId()) {
      //报名
      case R.id.signup:
        LinkCall<BaseResultDataInfo<SignupResponse>> signup =
            LinkCallHelper.getApiService().signupActivity(mId, accountManager.getToken());
        signup.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<SignupResponse>>() {
          @Override
          public void onResponse(BaseResultDataInfo<SignupResponse> entity, Response<?> response,
              Throwable throwable) {
            super.onResponse(entity, response, throwable);
            if (entity != null) {
              if (entity.error == 2000) {
                Toast.makeText(ActivityDetailActivity.this, "报名成功", Toast.LENGTH_SHORT).show();
              } else {
                Toast.makeText(ActivityDetailActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
              }
            }
          }
        });
        break;
      //签到
      case R.id.signin:
        LinkCall<BaseResultDataInfo<SigninResponse>> signin =
            LinkCallHelper.getApiService().signinActivity(mId, accountManager.getToken());
        signin.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<SigninResponse>>() {
          @Override
          public void onResponse(BaseResultDataInfo<SigninResponse> entity, Response<?> response,
              Throwable throwable) {
            super.onResponse(entity, response, throwable);
            if (entity != null) {
              if (entity.error == 2000) {
                Toast.makeText(ActivityDetailActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
              } else {
                Toast.makeText(ActivityDetailActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
              }
            }
          }
        });
        break;
    }
  }
}
