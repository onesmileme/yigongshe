package com.ygs.android.yigongshe.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
import com.ygs.android.yigongshe.bean.response.ShoucangResponse;
import com.ygs.android.yigongshe.bean.response.SigninResponse;
import com.ygs.android.yigongshe.bean.response.SignupResponse;
import com.ygs.android.yigongshe.bean.response.UnShoucangResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseDetailActivity;
import com.ygs.android.yigongshe.utils.DensityUtil;
import com.ygs.android.yigongshe.utils.NetworkUtils;
import com.ygs.android.yigongshe.view.DaCallView;
import com.ygs.android.yigongshe.view.HelpVideoView;
import com.ygs.android.yigongshe.view.MyWebView;
import retrofit2.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by ruichao on 2018/6/27.
 */

public class ActivityDetailActivity extends BaseDetailActivity {
  private LinkCall<BaseResultDataInfo<ActivityDetailResponse>> mCall;
  private DaCallView mDaCallView;
  private HelpVideoView mHelpVideoView;
  private MyWebView mWebView;
  private MyWebView mWebview2;
  private RelativeLayout mRlWebview1;
  private RelativeLayout mRlWebview2;
  private TextView mSeeFull;
  private LinkCall<BaseResultDataInfo<HelpVideoListResponse>> mHelpVideoCall;
  private boolean isStore;//0没收藏1收藏
  @BindView(R.id.signup) TextView mSignup; //报名
  @BindView(R.id.signin) TextView mSignin; //签到
  @BindView(R.id.shoucang) ImageView mShoucang;

  @Override protected void initIntent(Bundle bundle) {
    mId = bundle.getInt("activity_id");
    mTitle = bundle.getString("activity_title");
    mType = TYPE_ACTIVITY;
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_activity_detail;
  }

  @Override protected void initView() {
    super.initView();
    mTitleBar.getCenterTextView().setText(mTitle);
    mErrorView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        requestDetailData();
      }
    });

    requestDetailData();
    requestHelpVideoData();
  }

  private void requestDetailData() {
    if (!NetworkUtils.isConnected(this)) {
      showError(true);
      return;
    }
    showLoading(true);
    mCall = LinkCallHelper.getApiService().getActivityDetail(mId);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ActivityDetailResponse>>() {
      @Override public void onResponse(BaseResultDataInfo<ActivityDetailResponse> entity,
          Response<?> response, Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          showLoading(false);
          ActivityDetailResponse data = entity.data;
          if (data != null) {
            requestCommentData(TYPE_ACTIVITY, true);
            mWebView.loadDataWithBaseURL(null, data.content, "text/html", "utf-8", null);
            mWebview2.loadDataWithBaseURL(null, data.content, "text/html", "utf-8", null);
            mDaCallView.setDacallViewData(data);
            if (data.is_store == 1) {
              isStore = true;
              mShoucang.setImageResource(R.drawable.yishoucang);
            } else {
              isStore = false;
              mShoucang.setImageResource(R.drawable.shoucang);
            }
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
    //bg
    mRlWebview1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.view_webview, null);
    mAdapter.addHeaderView(mRlWebview1);
    mWebView = mRlWebview1.findViewById(R.id.webview);

    mWebView.setMaxHeight(MATCH_PARENT);
    mRlWebview1.setVisibility(View.GONE);
    //fg
    mRlWebview2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.view_webview, null);
    mAdapter.addHeaderView(mRlWebview2);
    mWebview2 = mRlWebview2.findViewById(R.id.webview);
    mWebview2.setMaxHeight(DensityUtil.dp2px(this, 360));
    mRlWebview2.setVisibility(View.VISIBLE);
    mSeeFull = mRlWebview2.findViewById(R.id.seefull);
    //if (mWebview2.getMeasuredHeight() < mWebview2.getMaxHeight()) {
    //  mSeeFull.setVisibility(View.GONE);
    //} else {
    mSeeFull.setVisibility(View.VISIBLE);
    mSeeFull.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mRlWebview1.setVisibility(View.VISIBLE);
        mRlWebview2.setVisibility(View.GONE);
        mSeeFull.setVisibility(View.GONE);
      }
    });
    //}

    mDaCallView = new DaCallView(this, mRecyclerView, mId);
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

  @OnClick({ R.id.signup, R.id.signin, R.id.shoucang, R.id.share })
  public void onBtnClicked(View view) {
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
      case R.id.shoucang:
        if (isStore) {
          LinkCall<BaseResultDataInfo<UnShoucangResponse>> unshoucang =
              LinkCallHelper.getApiService().unrestoreActivity(mId, accountManager.getToken());
          unshoucang.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UnShoucangResponse>>() {
            @Override public void onResponse(BaseResultDataInfo<UnShoucangResponse> entity,
                Response<?> response, Throwable throwable) {
              super.onResponse(entity, response, throwable);
              if (entity != null) {
                if (entity.error == 2000) {
                  Toast.makeText(ActivityDetailActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                  mShoucang.setImageResource(R.drawable.shoucang);
                } else {
                  Toast.makeText(ActivityDetailActivity.this, entity.msg, Toast.LENGTH_SHORT)
                      .show();
                }
              }
            }
          });
        } else {
          LinkCall<BaseResultDataInfo<ShoucangResponse>> shoucang =
              LinkCallHelper.getApiService().restoreActivity(mId, accountManager.getToken());
          shoucang.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ShoucangResponse>>() {
            @Override public void onResponse(BaseResultDataInfo<ShoucangResponse> entity,
                Response<?> response, Throwable throwable) {
              super.onResponse(entity, response, throwable);
              if (entity != null) {
                if (entity.error == 2000) {
                  mShoucang.setImageResource(R.drawable.yishoucang);
                  Toast.makeText(ActivityDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                  Toast.makeText(ActivityDetailActivity.this, entity.msg, Toast.LENGTH_SHORT)
                      .show();
                }
              }
            }
          });
        }

        break;
    }
  }
}
