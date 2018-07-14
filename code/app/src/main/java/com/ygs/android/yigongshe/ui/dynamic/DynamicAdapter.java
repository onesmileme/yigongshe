package com.ygs.android.yigongshe.ui.dynamic;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
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
        .load(item.pic)
        .placeholder(R.drawable.loading2)
        .error(R.drawable.loading2)
        .fallback(R.drawable.loading2)
        .thumbnail(0.1f)
        .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext))
        .into((ImageView) helper.getView(id.img));

    helper.setText(id.title, item.title);
    helper.setText(id.time, item.create_at);
    helper.setText(id.content, item.desc);
  }
}
