package com.ygs.android.yigongshe.ui.profile.activity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.ActivityItemBean;
import com.ygs.android.yigongshe.view.GlideRoundTransform;

public class MeAcitivityAdapter extends BaseQuickAdapter<ActivityItemBean, BaseViewHolder> {

  public MeAcitivityAdapter() {
    super(R.layout.item_dynamic, null);
  }

  @Override protected void convert(final BaseViewHolder helper, ActivityItemBean item) {

    Glide.with(mContext)
        .load(item.pic)
        .placeholder(R.drawable.defalutavar)
        .error(R.drawable.defalutavar)
        .fallback(R.drawable.defalutavar)
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
  }
}
