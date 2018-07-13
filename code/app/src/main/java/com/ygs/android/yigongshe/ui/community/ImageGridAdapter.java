package com.ygs.android.yigongshe.ui.community;

import android.content.Context;
import android.widget.CompoundButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.ImageItem;

public class ImageGridAdapter extends BaseQuickAdapter<ImageItem, BaseViewHolder> {
  private CheckListener mCheckListener;

  public ImageGridAdapter(Context context, CheckListener checkListener) {
    super(R.layout.view_select_picture_griditem, null);
    mCheckListener = checkListener;
  }

  @Override protected void convert(final BaseViewHolder helper, final ImageItem item) {
    Glide.with(mContext)
        .load(item.imageUrl)
        .placeholder(R.drawable.loading2)
        .error(R.drawable.loading2)
        .fallback(R.drawable.loading2)
        .override(60, 60)
        .into(new SimpleTarget<GlideDrawable>() {
          @Override public void onResourceReady(GlideDrawable resource,
              GlideAnimation<? super GlideDrawable> glideAnimation) {
            helper.setImageDrawable(R.id.iv_pic, resource);
          }
        });

    helper.setOnCheckedChangeListener(R.id.iv_selectbox,
        new CompoundButton.OnCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            helper.setChecked(R.id.iv_selectbox, b);
            mCheckListener.onCheckChanged(item);
          }
        });
  }

  public interface CheckListener {
    void onCheckChanged(ImageItem imageItem);
  }
}
