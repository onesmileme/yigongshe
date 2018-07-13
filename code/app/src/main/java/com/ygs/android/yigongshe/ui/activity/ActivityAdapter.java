package com.ygs.android.yigongshe.ui.activity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
        .transform(new GlideRoundTransform(mContext))
        .into(new SimpleTarget<GlideDrawable>() {
          @Override public void onResourceReady(GlideDrawable resource,
              GlideAnimation<? super GlideDrawable> glideAnimation) {
            helper.setImageDrawable(R.id.img, resource);
          }
        });

    helper.setText(R.id.title, item.title);
    helper.setText(R.id.time, item.create_at);
    helper.setText(R.id.content, item.desc);
    helper.setText(R.id.current_pro, item.cur_call_num + "");
    helper.setText(R.id.totalPro, item.target_call_num + "");
    helper.setProgress(R.id.progressbar_degree, item.cur_call_num);
  }
}
