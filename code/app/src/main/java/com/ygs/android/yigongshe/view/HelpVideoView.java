package com.ygs.android.yigongshe.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.HelpVideoItemBean;
import com.ygs.android.yigongshe.ui.activity.ActivityDetailActivity;
import com.ygs.android.yigongshe.ui.activity.HelpVideoDetailActivity;
import com.ygs.android.yigongshe.ui.activity.HelpVideoListActivity;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

/**
 * Created by ruichao on 2018/6/28.
 */

public class HelpVideoView {
  private View mView;
  //@BindView(R.id.video_list) RecyclerView mRecyclerView;
  @BindView(R.id.videoView) ImageView mVideoView;
  //private HelpVideoAdapter mAdapter;
  private int mActivityId;
  private Context mContext;

  public HelpVideoView(Context context, ViewGroup root, int activityId) {
    mActivityId = activityId;
    initView(context, root);
  }

  private void initView(final Context context, ViewGroup root) {
    mView = LayoutInflater.from(context).inflate(R.layout.view_helpvideo, root, false);
    ButterKnife.bind(this, mView);
    //LinearLayoutManager layoutManager = new LinearLayoutManager(context);
    //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    //mRecyclerView.setLayoutManager(layoutManager);
    //
    //mAdapter = new HelpVideoAdapter();
    //mRecyclerView.setAdapter(mAdapter);
    //mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
    //  @Override public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
    //    Intent intent = new Intent(context, HelpVideoListActivity.class);
    //    Bundle bundle = new Bundle();
    //    //ActivityItemBean itemBean = ((ActivityItemBean) adapter.getItem(position));
    //    bundle.putInt("activity_id", mActivityId);
    //    intent.putExtra(BaseActivity.PARAM_INTENT, bundle);
    //    context.startActivity(intent);
    //  }
    //});
  }

  public void setHelpVideoData(final Context context, final HelpVideoItemBean data) {
    mContext = context;
    if (data != null) {
      Glide.with(context)
          .load(data.thumbnail)
          .placeholder(R.drawable.loading2)
          .error(R.drawable.loading2)
          .fallback(R.drawable.loading2)
          .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext))
          .into(mVideoView);
      mVideoView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          Bundle bundle = new Bundle();
          bundle.putString("src", data.src);
          Intent intent = new Intent(context, HelpVideoDetailActivity.class);
          intent.putExtra(BaseActivity.PARAM_INTENT, bundle);

          ((Activity)context).startActivity(intent);
        }
      });
    }
    //mAdapter.setNewData(data.video_list);
  }

  //public class HelpVideoAdapter extends BaseQuickAdapter<HelpVideoItemBean, BaseViewHolder> {
  //
  //  public HelpVideoAdapter() {
  //    super(R.layout.item_helpvideo, null);
  //  }
  //
  //  @Override protected void convert(final BaseViewHolder helper, HelpVideoItemBean item) {
  //    Glide.with(mContext)
  //        .load(item.thumbnail)
  //        .transform(new GlideRoundTransform(mContext))
  //        .placeholder(R.drawable.loading2)
  //        .fitCenter()
  //        .into(new SimpleTarget<GlideDrawable>() {
  //          @Override public void onResourceReady(GlideDrawable resource,
  //              GlideAnimation<? super GlideDrawable> glideAnimation) {
  //            helper.setImageDrawable(R.id.item_helpVideo, resource);
  //          }
  //        });
  //  }
  //}

  public View getView() {
    return this.mView;
  }

  public void setVisibility(int show) {
    mView.setVisibility(show);
  }

  @OnClick(R.id.videomore) public void onBtnClicked() {
    Intent intent = new Intent(mContext, HelpVideoListActivity.class);
    Bundle bundle = new Bundle();
    bundle.putInt("activity_id", mActivityId);
    intent.putExtra(BaseActivity.PARAM_INTENT, bundle);
    mContext.startActivity(intent);
  }
}
