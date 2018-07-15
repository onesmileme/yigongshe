package com.ygs.android.yigongshe.ui.community;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
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
        .placeholder(R.drawable.defalutavar)
        .error(R.drawable.defalutavar)
        .fallback(R.drawable.defalutavar)
        .transform(new CenterCrop(mContext), new GlideCircleTransform(mContext))
        .into((ImageView) helper.getView(R.id.createAvatar));
    helper.setText(R.id.createName, item.create_name);
    helper.setText(R.id.content, item.topic + item.content);
    Glide.with(mContext)
        .load(item.pic)
        .placeholder(R.drawable.loading2)
        .error(R.drawable.loading2)
        .fallback(R.drawable.loading2)
        .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext))
        .into((ImageView) helper.getView(R.id.pic));
    helper.setText(R.id.createDate, item.create_at);
    helper.setText(R.id.topic, item.topic);
    helper.setText(R.id.markgood, item.zan + "");
    helper.addOnClickListener(R.id.attention);
    helper.addOnClickListener(R.id.iv_markgood);
    if (item.is_follow == 0) {
      helper.setBackgroundRes(R.id.attention, R.drawable.bg_unattention);
    } else {
      helper.setBackgroundRes(R.id.attention, R.drawable.bg_attention);
    }
    if (item.is_like == 0) {
      helper.setImageResource(R.id.iv_markgood, R.drawable.markgood);
    } else {
      helper.setImageResource(R.id.iv_markgood, R.drawable.hasmarkgood);
    }
  }
}
