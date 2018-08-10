package com.ygs.android.yigongshe.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.ShareBean;
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
import com.ygs.android.yigongshe.ui.share.ShareUtils;
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
  private LinearLayout mRlWebview1;
  //private RelativeLayout mRlWebview2;
  private TextView mSeeFull;
  private LinkCall<BaseResultDataInfo<HelpVideoListResponse>> mHelpVideoCall;
  private boolean isStore;//0没收藏1收藏
  //@BindView(R.id.signup) TextView mSignup; //报名
  //@BindView(R.id.signin) TextView mSignin; //签到
  @BindView(R.id.shoucang) ImageView mShoucang;
  private ShareBean mShareBean;
  //@BindView(R.id.status_on) LinearLayout mStatusOn;
  //@BindView(R.id.status_finish) RelativeLayout mStatusFinish;
  //@BindView(R.id.people_num) TextView mPeopleNum;

  LinearLayout mStatusOn;
  RelativeLayout mStatusFinish;
  TextView mPeopleNum;
  TextView createtitle, createName, createDate;
  AccountManager accountManager = YGApplication.accountManager;
  private TextView signup;
  private TextView signin;

  @Override protected void initIntent(Bundle bundle) {
    mId = bundle.getInt("activity_id");
    mTitle = bundle.getString("activity_title");
    mType = TYPE_ACTIVITY;
    mShareBean = (ShareBean) bundle.getSerializable("shareBean");
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
    mCall = LinkCallHelper.getApiService().getActivityDetail(mId, accountManager.getToken());
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ActivityDetailResponse>>() {
      @Override public void onResponse(BaseResultDataInfo<ActivityDetailResponse> entity,
          Response<?> response, Throwable throwable) {
        super.onResponse(entity, response, throwable);
        showLoading(false);
        if (entity != null && entity.error == 2000) {
          showLoading(false);
          ActivityDetailResponse data = entity.data;
          if (data != null) {
            mShareBean.url = data.share_url;
            requestCommentData(TYPE_ACTIVITY, true);
            createtitle.setText(data.title);
            createName.setText("发起方:" + data.create_name);
            createDate.setText(data.create_at + " 发布");
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
            if (data.is_end == 1) {
              mStatusOn.setVisibility(View.GONE);
              mStatusFinish.setVisibility(View.VISIBLE);
              mPeopleNum.setText(data.participate_count + "");
            } else {
              mStatusOn.setVisibility(View.VISIBLE);
              mStatusFinish.setVisibility(View.GONE);
              if (data.is_register == 1) {//报名
                signup.setTextColor(getResources().getColor(R.color.black1));
                signup.setBackgroundResource(R.drawable.bg_hassignup);
              } else {
                signup.setTextColor(getResources().getColor(R.color.white));
                signup.setBackgroundResource(R.drawable.bg_signup);
              }
              if (data.is_signin == 1) {
                signin.setTextColor(getResources().getColor(R.color.black1));
                signin.setBackgroundResource(R.drawable.bg_hassignin);
              } else {
                signin.setTextColor(getResources().getColor(R.color.white));
                signin.setBackgroundResource(R.drawable.bg_signup);
              }
            }
          }
        }
      }
    });
  }

  private void requestHelpVideoData() {
    mHelpVideoCall = LinkCallHelper.getApiService()
        .getHelpVideoList(0, 20, mId, "", "", mAccountManager.getToken());
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
          //mHelpVideoView.setVisibility(View.VISIBLE);
          mHelpVideoView.setHelpVideoData(ActivityDetailActivity.this, data.video_list.get(0));
        } else {
          //mHelpVideoView.setVisibility(View.GONE);
          mHelpVideoView.setHelpVideoData(ActivityDetailActivity.this, null);
        }
      }
    });
  }

  protected void addHeaderView() {
    //fg
    mRlWebview1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_webview, null);
    mAdapter.addHeaderView(mRlWebview1);
    mWebView = mRlWebview1.findViewById(R.id.webview1);
    mWebView.setMaxHeight(DensityUtil.dp2px(this, 360));
    mSeeFull = mRlWebview1.findViewById(R.id.seefull);
    final View webview1holder = mRlWebview1.findViewById(R.id.webview1holder);
    mStatusOn = mRlWebview1.findViewById(R.id.status_on);
    mStatusFinish = mRlWebview1.findViewById(R.id.status_finish);
    mPeopleNum = mRlWebview1.findViewById(R.id.people_num);
    createtitle = mRlWebview1.findViewById(R.id.createtitle);
    createName = mRlWebview1.findViewById(R.id.createName);
    createDate = mRlWebview1.findViewById(R.id.createDate);
    signup = mRlWebview1.findViewById(R.id.signup);
    signin = mRlWebview1.findViewById(R.id.signin);
    signup.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(final View view) {
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
                ((TextView) view).setTextColor(getResources().getColor(R.color.black1));
                view.setBackgroundResource(R.drawable.bg_hassignup);
              } else {
                Toast.makeText(ActivityDetailActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
              }
            }
          }
        });
      }
    });
    signin.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(final View view) {
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
                ((TextView) view).setTextColor(getResources().getColor(R.color.black1));
                view.setBackgroundResource(R.drawable.bg_hassignin);
              } else {
                Toast.makeText(ActivityDetailActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
              }
            }
          }
        });
      }
    });
    //bg
    //mRlWebview2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.view_webview, null);
    //mAdapter.addHeaderView(mRlWebview2);
    mWebview2 = mRlWebview1.findViewById(R.id.webview2);
    mWebview2.setMaxHeight(MATCH_PARENT);

    //if (mWebview2.getMeasuredHeight() < mWebview2.getMaxHeight()) {
    //  mSeeFull.setVisibility(View.GONE);
    //} else {
    mSeeFull.setVisibility(View.VISIBLE);
    mSeeFull.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mSeeFull.setVisibility(View.GONE);
        mWebView.setVisibility(View.GONE);
        webview1holder.setVisibility(View.GONE);
        mWebview2.setVisibility(View.VISIBLE);
      }
    });

    mDaCallView = new

        DaCallView(this, mRecyclerView, mId);
    mAdapter.addHeaderView(mDaCallView.getView());
    mHelpVideoView = new

        HelpVideoView(this, mRecyclerView, mId);
    mAdapter.addHeaderView(mHelpVideoView.getView());
  }

  @Override protected void onStop() {
    if (mCall != null) {
      mCall.cancel();
    }
    super.onStop();
  }

  @OnClick({ R.id.shoucang, R.id.share }) public void onBtnClicked(View view) {
    if (TextUtils.isEmpty(accountManager.getToken())) {
      Toast.makeText(this, "没有登录", Toast.LENGTH_SHORT).show();
      return;
    }

    switch (view.getId()) {
      //报名
      //case R.id.signup:

      //break;
      //签到
      //case R.id.signin:

      // break;
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
                  isStore = false;
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
                  isStore = true;
                } else {
                  Toast.makeText(ActivityDetailActivity.this, entity.msg, Toast.LENGTH_SHORT)
                      .show();
                }
              }
            }
          });
        }

        break;
      case R.id.share:
        ShareUtils.getInstance().shareTo(this, mShareBean);
        break;
    }
  }
}
