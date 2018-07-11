package com.ygs.android.yigongshe.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.ActivityDetailResponse;
import com.ygs.android.yigongshe.bean.response.HelpVideoListResponse;
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
  private LinkCall<BaseResultDataInfo<HelpVideoListResponse>> mHelpVideoCall;

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
          mHelpVideoView.setHelpVideoData(data);
        } else {
          mHelpVideoView.setVisibility(View.GONE);
        }
      }
    });
  }

  protected void addHeaderView() {
    mWebView = new WebView(this);
    mAdapter.addHeaderView(mWebView);
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
}
