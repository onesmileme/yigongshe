package com.ygs.android.yigongshe.ui.dynamic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
  private TextView createtitle, createName, createDate;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

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
        } else if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
          finish();
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
    LinearLayout linearLayout =
        (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_webviewwithholder, null);
    mWebView = linearLayout.findViewById(R.id.webview);
    createtitle = linearLayout.findViewById(R.id.createtitle);
    createName = linearLayout.findViewById(R.id.createName);
    createDate = linearLayout.findViewById(R.id.createDate);
    mAdapter.addHeaderView(linearLayout);
  }

  private void requestDetailData() {
    if (!NetworkUtils.isConnected(this)) {
      showError(true);
      return;
    }
    if (TextUtils.isEmpty(mAccountManager.getToken())) {
      return;
    }
    showLoading(true);
    mCall = LinkCallHelper.getApiService().getDynamicDetail(mId, mAccountManager.getToken());
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<DynamicDetailResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<DynamicDetailResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        showLoading(false);
        if (entity != null && entity.error == 2000) {
          showLoading(false);
          DynamicDetailResponse data = entity.data;
          if (data != null && data.news_info != null && !TextUtils.isEmpty(
              data.news_info.content)) {
            mShareBean.url = data.news_info.share_url;
            requestCommentData(TYPE_DYNAMIC, true);
            //String htmlText = "<html>"
            //    + "<head>"
            //    + "<style type=\"text/css\">"
            //    + "body{padding-left: 14px;padding-right: 14px;}"
            //    + "</style>"
            //    + "</head>";
            createtitle.setText(data.news_info.title);
            createName.setText("发布机构 " + data.news_info.create_name);
            createDate.setText(data.news_info.create_at + " 发布");
            mWebView.loadDataWithBaseURL(null, /**htmlText + **/data.news_info.content, "text/html",
                "utf-8", null);
          }
        }
      }
    });
  }
}
