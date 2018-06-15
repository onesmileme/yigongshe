package com.ygs.android.yigongshe.ui.community;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.CommunityItemBean;

/**
 * Created by ruichao on 2018/6/15.
 */

public class CommunityAdapter extends BaseQuickAdapter<CommunityItemBean, BaseViewHolder> {
  public CommunityAdapter() {
    super(R.layout.item_community, null);
  }

  @Override protected void convert(BaseViewHolder helper, CommunityItemBean item) {
    helper.setText(R.id.tv_test, item.title);
  }
}
