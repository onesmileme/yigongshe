package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.CommunityItemBean;

/**
 * Created by ruichao on 2018/6/28.
 */

public class CommunityDetailHeaderView {
  private View mView;
  @BindView(R.id.createAvatar) ImageView mAvatar;
  @BindView(R.id.createName) TextView mCreateName;
  @BindView(R.id.content) TextView mContent;
  @BindView(R.id.pic) ImageView mPic;
  @BindView(R.id.createDate) TextView mCreateDate;
  @BindView(R.id.topic) TextView mTopic;
  @BindView(R.id.markgood) TextView mMarkGood;
  private Context mContext;

  public CommunityDetailHeaderView(Context context, ViewGroup root) {
    mContext = context;
    initView(context, root);
  }

  private void initView(Context context, ViewGroup root) {
    mView = LayoutInflater.from(context).inflate(R.layout.item_community, root, false);
    ButterKnife.bind(this, mView);
  }

  public void setViewData(CommunityItemBean item) {
    Glide.with(mContext)
        .load(item.create_avatar)
        .placeholder(R.drawable.defalutavar)
        .error(R.drawable.defalutavar)
        .fallback(R.drawable.defalutavar)
        .transform(new GlideCircleTransform(mContext))
        .into(new SimpleTarget<GlideDrawable>() {
          @Override public void onResourceReady(GlideDrawable resource,
              GlideAnimation<? super GlideDrawable> glideAnimation) {
            mAvatar.setImageDrawable(resource);
          }
        });
    mCreateName.setText(item.create_name);
    mContent.setText(item.topic + item.content);
    Glide.with(mContext)
        .load(item.pic)
        .placeholder(R.drawable.loading2)
        .error(R.drawable.loading2)
        .fallback(R.drawable.loading2)
        .transform(new GlideRoundTransform(mContext))
        .into(new SimpleTarget<GlideDrawable>() {
          @Override public void onResourceReady(GlideDrawable resource,
              GlideAnimation<? super GlideDrawable> glideAnimation) {
            mPic.setImageDrawable(resource);
          }
        });
    mCreateDate.setText(item.create_at);
    mTopic.setText(item.topic);
    mMarkGood.setText(item.zan + "");
  }

  public View getView() {
    return this.mView;
  }
}
