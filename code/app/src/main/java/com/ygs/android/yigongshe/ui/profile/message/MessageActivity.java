package com.ygs.android.yigongshe.ui.profile.message;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.utils.DensityUtil;
import com.ygs.android.yigongshe.view.SegmentControlView;

public class MessageActivity extends BaseActivity implements View.OnClickListener {

  @BindView(R.id.titlebar_backward_btn) Button mNavBackButton;

  @BindView(R.id.titlebar_right_btn) Button mNavRightButton;

  @BindView(R.id.titlebar_text_title) TextView mTitleView;

  @BindView(R.id.message_segment) SegmentControlView segmentControlView;

  @BindView(R.id.message_list) RecyclerView recyclerView;

  @BindView(R.id.message_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

  private MessageAdapter messageAdapter;

  protected void initIntent(Bundle bundle) {

  }

  protected void initView() {

    mNavBackButton.setOnClickListener(this);
    mNavRightButton.setText("操作");

    mNavRightButton.setTextColor(getResources().getColor(R.color.gray1));
    mNavRightButton.setVisibility(View.VISIBLE);
    mNavRightButton.setOnClickListener(this);

    mTitleView.setText("消息");

    messageAdapter = new MessageAdapter();
    segmentControlView.setTextSize(DensityUtil.dp2px(this, 12));
    int norColor = getResources().getColor(R.color.gray2);
    int selColor = Color.WHITE;
    segmentControlView.setTextColor(norColor, selColor);
    segmentControlView.setOnSegmentChangedListener(
        new SegmentControlView.OnSegmentChangedListener() {
          @Override public void onSegmentChanged(int newSelectedIndex) {
            changeSegment(newSelectedIndex);
          }
        });
    swipeRefreshLayout.setOnRefreshListener(messageAdapter);
    messageAdapter.setSwipeRefreshLayout(swipeRefreshLayout);
  }

  protected int getLayoutResId() {
    return R.layout.activity_message;
  }

  private void changeSegment(int position) {

  }

  public void onClick(View view) {

    if (mNavBackButton == view) {
      finish();
    } else if (mNavRightButton == view) {

    }
  }
}
