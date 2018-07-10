package com.ygs.android.yigongshe.ui.dynamic;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.DynamicDetailResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseDetailActivity;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/25.
 */

public class DynamicDetailActivity extends BaseDetailActivity {
  private WebView mWebView;
  private LinkCall<BaseResultDataInfo<DynamicDetailResponse>> mCall;

  @Override protected void initIntent(Bundle bundle) {
    mId = bundle.getInt("news_id");
    mTitle = bundle.getString("news_title");
  }

  @Override protected void initView() {
    super.initView();
    mTitleBar.getCenterTextView().setText(mTitle);
    requestDetailData();
    requestCommentData(TYPE_DYNAMIC, true);
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_dynamic_detail;
  }

  //添加很多个headerview
  protected void addHeaderView() {
    mWebView = new WebView(this);
    mAdapter.addHeaderView(mWebView);
  }

  private void requestDetailData() {
    mCall = LinkCallHelper.getApiService().getDynamicDetail(mId);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<DynamicDetailResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<DynamicDetailResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          DynamicDetailResponse data = entity.data;
          if (data != null && data.news_info != null && !TextUtils.isEmpty(
              data.news_info.content)) {
            mWebView.loadDataWithBaseURL(null, data.news_info.content, "text/html", "utf-8", null);
          }
        }
      }
    });
  }
}
