package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.community.TopicSelectActivity;

/**
 * Created by ruichao on 2018/6/28.
 */

public class CommunityListHeader {
  private View mView;
  private Context mContext;
  @BindView(R.id.topic) TextView mTopic;
  @BindView(R.id.city) TextView mCity;

  public CommunityListHeader(Context context, ViewGroup root) {
    mContext = context;
    initView(context, root);
  }

  private void initView(Context context, ViewGroup root) {
    mView = LayoutInflater.from(context).inflate(R.layout.view_community_header, root, false);
    ButterKnife.bind(this, mView);
  }

  @OnClick({ R.id.topic, R.id.city }) public void onBtnClicked(TextView tv) {
    switch (tv.getId()) {
      case R.id.topic:
        //mContext.startActivity(TopicSelectActivity.this);
        break;
      case R.id.city:
        break;
    }
  }

  public View getView() {
    return this.mView;
  }
}
