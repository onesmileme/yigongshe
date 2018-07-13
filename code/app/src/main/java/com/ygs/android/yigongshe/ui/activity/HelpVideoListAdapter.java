package com.ygs.android.yigongshe.ui.activity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.HelpVideoItemBean;
import com.ygs.android.yigongshe.view.GlideCircleTransform;
import com.ygs.android.yigongshe.view.GlideRoundTransform;

/**
 * Created by ruichao on 2018/7/7.
 */

public class HelpVideoListAdapter extends BaseQuickAdapter<HelpVideoItemBean, BaseViewHolder> {
  public HelpVideoListAdapter() {
    super(R.layout.item_helpvideo2, null);
  }

  @Override protected void convert(final BaseViewHolder helper, HelpVideoItemBean item) {
    Glide.with(mContext)
        .load(item.thumbnail)
        .placeholder(R.drawable.loading2)
        .error(R.drawable.loading2)
        .fallback(R.drawable.loading2)
        .centerCrop()
        .transform(new GlideRoundTransform(mContext))
        .into(new SimpleTarget<GlideDrawable>() {
          @Override public void onResourceReady(GlideDrawable resource,
              GlideAnimation<? super GlideDrawable> glideAnimation) {
            helper.setImageDrawable(R.id.item_helpVideo, resource);
          }
        });

    Glide.with(mContext)
        .load(item.avatar)
        .placeholder(R.drawable.defalutavar)
        .error(R.drawable.defalutavar)
        .fallback(R.drawable.defalutavar)
        .transform(new GlideCircleTransform(mContext))
        .into(new SimpleTarget<GlideDrawable>() {
          @Override public void onResourceReady(GlideDrawable resource,
              GlideAnimation<? super GlideDrawable> glideAnimation) {
            helper.setImageDrawable(R.id.createAvatar, resource);
          }
        });
    helper.setText(R.id.createName, item.create_name);
    helper.setText(R.id.createDate, item.create_at);
  }
}
