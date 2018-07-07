package com.ygs.android.yigongshe.ui.community;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.CommunityItemBean;
import com.ygs.android.yigongshe.view.GlideCircleTransform;
import com.ygs.android.yigongshe.view.GlideRoundTransform;

/**
 * Created by ruichao on 2018/6/15.
 */

public class CommunityAdapter extends BaseQuickAdapter<CommunityItemBean, BaseViewHolder> {
  public CommunityAdapter() {
    super(R.layout.item_community, null);
  }

  @Override protected void convert(final BaseViewHolder helper, CommunityItemBean item) {
    Glide.with(mContext)
        .load(item.create_avatar)
        .transform(new GlideCircleTransform(mContext))
        .into(new SimpleTarget<GlideDrawable>() {
          @Override public void onResourceReady(GlideDrawable resource,
              GlideAnimation<? super GlideDrawable> glideAnimation) {
            helper.setImageDrawable(R.id.createAvatar, resource);
          }
        });
    helper.setText(R.id.createName, item.create_name);
    helper.setText(R.id.content, item.topic + item.content);
    Glide.with(mContext)
        .load(item.pic)
        .transform(new GlideRoundTransform(mContext))
        .into(new SimpleTarget<GlideDrawable>() {
          @Override public void onResourceReady(GlideDrawable resource,
              GlideAnimation<? super GlideDrawable> glideAnimation) {
            helper.setImageDrawable(R.id.pic, resource);
          }
        });
    helper.setText(R.id.createDate, item.create_at);
    helper.setText(R.id.topic, item.topic);
    helper.setText(R.id.markgood, item.zan + "");
  }
}
