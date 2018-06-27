package com.ygs.android.yigongshe.ui.dynamic;

import android.os.Bundle;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseDetailActivity;

/**
 * Created by ruichao on 2018/6/25.
 */

public class DynamicDetailActivity extends BaseDetailActivity {

  @Override protected void initIntent(Bundle bundle) {
    mId = bundle.getInt("news_id");
    mTitle = bundle.getString("news_title");
  }

  @Override protected void initView() {
    super.initView();
    mTitleBar.getCenterTextView().setText(mTitle);
    requestData(TYPE_DYNAMIC, true);
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_dynamic_detail;
  }
}
