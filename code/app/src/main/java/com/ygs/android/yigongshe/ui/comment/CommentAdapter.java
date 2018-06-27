package com.ygs.android.yigongshe.ui.comment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.CommentItemBean;
import com.ygs.android.yigongshe.view.GlideCircleTransform;

/**
 * Created by ruichao on 2018/6/26.
 */

public class CommentAdapter extends BaseQuickAdapter<CommentItemBean, BaseViewHolder> {
  public CommentAdapter() {
    super(R.layout.item_comment, null);
  }

  @Override protected void convert(final BaseViewHolder helper, CommentItemBean item) {
    Glide.with(mContext)
        .load(item.create_avatar)
        .transform(new GlideCircleTransform(mContext))
        .into(new SimpleTarget<GlideDrawable>() {
          @Override public void onResourceReady(GlideDrawable resource,
              GlideAnimation<? super GlideDrawable> glideAnimation) {
            helper.setImageDrawable(R.id.createAvatar, resource);
          }
        });
  }
}
