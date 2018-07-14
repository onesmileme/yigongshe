package com.ygs.android.yigongshe.ui.activity;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
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
        .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext))
        .into((ImageView) helper.getView(R.id.item_helpVideo));

    Glide.with(mContext)
        .load(item.avatar)
        .placeholder(R.drawable.defalutavar)
        .error(R.drawable.defalutavar)
        .fallback(R.drawable.defalutavar)
        .transform(new GlideCircleTransform(mContext))
        .into((ImageView) helper.getView(R.id.createAvatar));
    helper.setText(R.id.createName, item.create_name);
    helper.setText(R.id.createDate, item.create_at);
  }
}
