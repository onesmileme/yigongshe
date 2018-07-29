package com.ygs.android.yigongshe.ui.profile.activity;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
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
        .placeholder(R.drawable.loading2)
        .error(R.drawable.loading2)
        .fallback(R.drawable.loading2)
        .thumbnail(0.1f)
        .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext))
        .into((ImageView) helper.getView(R.id.img));

    helper.setText(R.id.title, item.title);
    helper.setText(R.id.time, item.create_at);
    helper.setText(R.id.content, item.desc);
  }
}
