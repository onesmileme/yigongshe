package com.ygs.android.yigongshe.ui.community;

import android.graphics.Paint;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
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
    AccountManager accountManager = YGApplication.accountManager;
    Glide.with(mContext)
        .load(item.create_avatar)
        .placeholder(R.drawable.defalutavar)
        .error(R.drawable.defalutavar)
        .fallback(R.drawable.defalutavar)
        .transform(new CenterCrop(mContext), new GlideCircleTransform(mContext))
        .into((ImageView) helper.getView(R.id.createAvatar));
    helper.setText(R.id.createName, item.create_name);
    helper.setText(R.id.content, item.content);
    if (TextUtils.isEmpty(item.pic)) {
      helper.setGone(R.id.pic, false);
    } else {
      helper.setVisible(R.id.pic, true);
      Glide.with(mContext)
          .load(item.pic)
          .placeholder(R.drawable.loading2)
          .error(R.drawable.loading2)
          .fallback(R.drawable.loading2)
          .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext))
          .into((ImageView) helper.getView(R.id.pic));
    }
    if (!TextUtils.isEmpty(item.topic)) {
      helper.setText(R.id.topic1, "#" + item.topic + "#");
      helper.setText(R.id.topic, "#" + item.topic + "#");
    }
    helper.setText(R.id.createDate, item.create_at);
    helper.setText(R.id.markgood, item.like_num + "");
    helper.addOnClickListener(R.id.attention);
    helper.addOnClickListener(R.id.iv_markgood);

    if (item.is_follow == 0) {
      helper.setBackgroundRes(R.id.attention, R.drawable.bg_unattention);
    } else {
      helper.setBackgroundRes(R.id.attention, R.drawable.bg_attention);
    }
    if (item.is_like == 0) {
      helper.setTextColor(R.id.markgood, mContext.getResources().getColor(R.color.gray2));
      helper.setImageResource(R.id.iv_markgood, R.drawable.markgood);
    } else {
      helper.setTextColor(R.id.markgood, mContext.getResources().getColor(R.color.green));
      helper.setImageResource(R.id.iv_markgood, R.drawable.hasmarkgood);
    }
    helper.addOnClickListener(R.id.iv_markgood);
    if (!TextUtils.isEmpty(accountManager.getToken())
        && item.create_id == accountManager.getUserid()) {
      helper.setVisible(R.id.delete, true);
      TextView tv = helper.getView(R.id.delete);
      tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
      helper.addOnClickListener(R.id.delete);
    } else {
      helper.setGone(R.id.delete, false);
    }
  }
}
