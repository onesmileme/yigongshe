package com.ygs.android.yigongshe.ui.activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.HelpVideoItemBean;

/**
 * Created by ruichao on 2018/7/7.
 */

public class HelpVideoListAdapter extends BaseQuickAdapter<HelpVideoItemBean, BaseViewHolder> {
  public HelpVideoListAdapter() {
    super(R.layout.item_helpvideo2, null);
  }

  @Override protected void convert(BaseViewHolder helper, HelpVideoItemBean item) {

  }
}
