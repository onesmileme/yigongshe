package com.ygs.android.yigongshe.ui.community;

import android.content.Context;
import android.view.View;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.ImageItem;

public class ImageGridAdapter extends BaseQuickAdapter<ImageItem, BaseViewHolder> {

  public ImageGridAdapter(Context context) {
    super(R.layout.view_select_picture_griditem, null);
  }

  @Override protected void convert(final BaseViewHolder helper, final ImageItem item) {
    Glide.with(mContext).load(item.imageUrl).into(new SimpleTarget<GlideDrawable>() {
      @Override public void onResourceReady(GlideDrawable resource,
          GlideAnimation<? super GlideDrawable> glideAnimation) {
        helper.setImageDrawable(R.id.iv_pic, resource);
        helper.setChecked(R.id.iv_selectbox, item.isSelected);
      }
    });
  }
}
