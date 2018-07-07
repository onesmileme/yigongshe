package com.ygs.android.yigongshe.ui.community;

import android.os.Bundle;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.CommunityItemBean;
import com.ygs.android.yigongshe.ui.base.BaseDetailActivity;
import com.ygs.android.yigongshe.view.CommunityDetailHeaderView;

/**
 * Created by ruichao on 2018/6/28.
 */

public class CommunityDetailActivity extends BaseDetailActivity {
  private CommunityDetailHeaderView mHeaderView;
  private CommunityItemBean mCommunityItemBean;

  @Override protected int getLayoutResId() {
    return R.layout.activity_community_detail;
  }

  @Override protected void initIntent(Bundle bundle) {
    mId = bundle.getInt("pubcircle_id");
    mTitle = "详情";
    mCommunityItemBean = (CommunityItemBean) bundle.getSerializable("item");
  }

  protected void addHeaderView() {
    mHeaderView = new CommunityDetailHeaderView(this, mRecyclerView);
    mHeaderView.setViewData(mCommunityItemBean);
    mAdapter.addHeaderView(mHeaderView.getView());
  }
}
