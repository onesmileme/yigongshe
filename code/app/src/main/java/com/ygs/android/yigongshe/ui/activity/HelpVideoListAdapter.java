package com.ygs.android.yigongshe.ui.activity;

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
    AccountManager accountManager = YGApplication.accountManager;
    Glide.with(mContext)
        .load(item.thumbnail)
        //.placeholder(R.drawable.loading2)
        //.error(R.drawable.loading2)
        //.fallback(R.drawable.loading2)
        .thumbnail(0.1f)
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
    helper.setText(R.id.markgood, item.like_num + "");
    if (item.is_like == 0) {
      helper.setImageResource(R.id.iv_markgood, R.drawable.markgood);
      helper.setTextColor(R.id.markgood, mContext.getResources().getColor(R.color.gray2));
    } else {
      helper.setImageResource(R.id.iv_markgood, R.drawable.hasmarkgood);
      helper.setTextColor(R.id.markgood, mContext.getResources().getColor(R.color.green));
    }
    helper.addOnClickListener(R.id.iv_markgood);
    helper.addOnClickListener(R.id.markgood);
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
