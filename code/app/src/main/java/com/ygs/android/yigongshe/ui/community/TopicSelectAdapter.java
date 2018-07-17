package com.ygs.android.yigongshe.ui.community;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.utils.StringUtil;

/**
 * Created by ruichao on 2018/6/27.
 */

public class TopicSelectAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

  //public TopicSelectAdapter(@Nullable List<String> data) {
  //  super(R.layout.item_topic_select, data);
  //}
  public TopicSelectAdapter() {
    super(R.layout.item_topic_select, null);
  }

  @Override protected void convert(BaseViewHolder helper, String item) {
    helper.setText(R.id.tv_topic,
        StringUtil.getReleaseString(mContext.getResources().getString(R.string.topicItem),
            new Object[] { item }));
  }
}
