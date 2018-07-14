package com.ygs.android.yigongshe.ui.comment;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
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
        .placeholder(R.drawable.defalutavar)
        .error(R.drawable.defalutavar)
        .fallback(R.drawable.defalutavar)
        .transform(new CenterCrop(mContext), new GlideCircleTransform(mContext))
        .into((ImageView) helper.getView(R.id.createAvatar));
    helper.setText(R.id.createName, item.create_name);
    helper.setText(R.id.createDate, item.create_at);
    helper.setText(R.id.content, item.content);
  }
}
