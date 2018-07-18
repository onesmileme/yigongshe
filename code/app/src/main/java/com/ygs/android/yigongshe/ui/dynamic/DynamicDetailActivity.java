package com.ygs.android.yigongshe.ui.dynamic;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.ShareBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.DynamicDetailResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseDetailActivity;
import com.ygs.android.yigongshe.ui.share.ShareUtils;
import com.ygs.android.yigongshe.utils.NetworkUtils;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/25.
 */

public class DynamicDetailActivity extends BaseDetailActivity {
  private WebView mWebView;
  private LinkCall<BaseResultDataInfo<DynamicDetailResponse>> mCall;
  private ShareBean mShareBean;

  @Override protected void initIntent(Bundle bundle) {
    mId = bundle.getInt("news_id");
    mTitle = bundle.getString("news_title");
    mType = TYPE_DYNAMIC;
    mShareBean = (ShareBean) bundle.getSerializable("shareBean");
  }

  @Override protected void initView() {
    super.initView();
    mTitleBar.getCenterTextView().setText(mTitle);
    mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_RIGHT_BUTTON) {
          ShareUtils.getInstance().shareTo(DynamicDetailActivity.this, mShareBean);
        }
      }
    });
    mErrorView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        requestDetailData();
      }
    });
    requestDetailData();
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
    if (!NetworkUtils.isConnected(this)) {
      showError(true);
      return;
    }
    showLoading(true);
    mCall = LinkCallHelper.getApiService().getDynamicDetail(mId);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<DynamicDetailResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<DynamicDetailResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          showLoading(false);
          DynamicDetailResponse data = entity.data;
          if (data != null && data.news_info != null && !TextUtils.isEmpty(
              data.news_info.content)) {
            requestCommentData(TYPE_DYNAMIC, true);
            mWebView.loadDataWithBaseURL(null, data.news_info.content, "text/html", "utf-8", null);
          }
        }
      }
    });
  }
}
