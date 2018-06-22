package com.ygs.android.yigongshe.ui.dynamic;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.bean.DynamicItemBean;
import com.ygs.android.yigongshe.view.GlideRoundTransform;

import static com.ygs.android.yigongshe.R.id;
import static com.ygs.android.yigongshe.R.layout;

/**
 * Created by ruichao on 2018/6/14.
 */

public class DynamicAdapter extends BaseQuickAdapter<DynamicItemBean, BaseViewHolder> {
  public DynamicAdapter() {
    super(layout.item_dynamic, null);
  }

  @Override protected void convert(final BaseViewHolder helper, DynamicItemBean item) {
    Glide.with(mContext)
        .load("http://img1.imgtn.bdimg.com/it/u=3044191397,2911599132&fm=27&gp=0.jpg")
        .transform(new GlideRoundTransform(mContext))
        .into(new SimpleTarget<GlideDrawable>() {
          @Override public void onResourceReady(GlideDrawable resource,
              GlideAnimation<? super GlideDrawable> glideAnimation) {
            helper.setImageDrawable(id.img, resource);
          }
        });

    helper.setText(id.title, item.title);
    helper.setText(id.time, item.create_at);
    helper.setText(id.content, item.desc);
  }
}
