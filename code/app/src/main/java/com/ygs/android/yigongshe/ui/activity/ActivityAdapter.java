package com.ygs.android.yigongshe.ui.activity;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.ActivityItemBean;
import com.ygs.android.yigongshe.view.GlideRoundTransform;

/**
 * Created by ruichao on 2018/6/28.
 */

public class ActivityAdapter extends BaseQuickAdapter<ActivityItemBean, BaseViewHolder> {
  public ActivityAdapter() {
    super(R.layout.item_activity, null);
  }

  @Override protected void convert(final BaseViewHolder helper, ActivityItemBean item) {
    Glide.with(mContext)
        .load(item.pic)
        .placeholder(R.drawable.loading2)
        .error(R.drawable.loading2)
        .fallback(R.drawable.loading2)
        .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext))
        .into((ImageView) helper.getView(R.id.img));

    helper.setText(R.id.title, item.title);
    helper.setText(R.id.time, item.create_at);
    helper.setText(R.id.content, item.desc);
    helper.setText(R.id.current_pro, item.cur_call_num + "");
    helper.setText(R.id.totalPro, item.target_call_num + "");
    helper.setProgress(R.id.progressbar_degree, 100 * item.cur_call_num / item.target_call_num);
  }
}
