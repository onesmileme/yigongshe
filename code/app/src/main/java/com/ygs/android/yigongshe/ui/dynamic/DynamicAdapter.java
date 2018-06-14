package com.ygs.android.yigongshe.ui.dynamic;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.DynamicItemBean;

/**
 * Created by ruichao on 2018/6/14.
 */

public class DynamicAdapter extends BaseQuickAdapter<DynamicItemBean, BaseViewHolder> {
  public DynamicAdapter() {
    super(R.layout.item_dynamic, null);
  }

  @Override protected void convert(BaseViewHolder helper, DynamicItemBean item) {
    helper.setText(R.id.tv_test, item.title);
  }
}
