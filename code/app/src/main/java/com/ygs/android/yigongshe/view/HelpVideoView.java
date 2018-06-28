package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.response.ActivityDetailResponse;

/**
 * Created by ruichao on 2018/6/28.
 */

public class HelpVideoView {
  private View mView;
  @BindView(R.id.video_list) RecyclerView mRecyclerView;
  private HelpVideoAdapter mAdapter;

  public HelpVideoView(Context context, ViewGroup root) {
    initView(context, root);
  }

  private void initView(Context context, ViewGroup root) {
    mView = LayoutInflater.from(context).inflate(R.layout.view_helpvideo, root, false);
    ButterKnife.bind(this, mView);
    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    mRecyclerView.setLayoutManager(layoutManager);

    mAdapter = new HelpVideoAdapter();
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
      @Override public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
      }
    });
  }

  public void setHelpVideoData(ActivityDetailResponse data) {
    mAdapter.setNewData(data.videos);
  }

  public class HelpVideoAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public HelpVideoAdapter() {
      super(R.layout.item_helpvideo, null);
    }

    @Override protected void convert(final BaseViewHolder helper, String item) {
      Glide.with(mContext)
          .load(item)
          .transform(new GlideRoundTransform(mContext))
          .into(new SimpleTarget<GlideDrawable>() {
            @Override public void onResourceReady(GlideDrawable resource,
                GlideAnimation<? super GlideDrawable> glideAnimation) {
              helper.setImageDrawable(R.id.item_helpVideo, resource);
            }
          });
    }
  }

  public View getView() {
    return this.mView;
  }
}
