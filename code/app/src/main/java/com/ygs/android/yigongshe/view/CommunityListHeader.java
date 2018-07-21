package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ygs.android.yigongshe.R;

/**
 * Created by ruichao on 2018/6/28.
 */

public class CommunityListHeader {
  private View mView;
  private Context mContext;
  @BindView(R.id.topic) TextView mTopic;
  @BindView(R.id.city) TextView mCity;
  private SelectBtnListener mListener;
  private String mTopicString, mCityString;

  public CommunityListHeader(Context context, ViewGroup root, SelectBtnListener listener) {
    mContext = context;
    mListener = listener;
    initView(context, root);
  }

  private void initView(Context context, ViewGroup root) {
    mView = LayoutInflater.from(context).inflate(R.layout.view_community_header, root, false);
    ButterKnife.bind(this, mView);
  }

  @OnClick({ R.id.topic, R.id.city }) public void onBtnClicked(TextView tv) {
    switch (tv.getId()) {
      case R.id.topic:
        mListener.onBtnSelected(0);

        break;
      case R.id.city:
        mListener.onBtnSelected(1);
        break;
    }
  }

  public void setViewData(int id, String data) {
    if (TextUtils.isEmpty(data)) {
      return;
    }
    switch (id) {
      case 0:
        mTopic.setText(data);
        break;
      case 1:
        mCity.setText(data);
        break;
    }
  }

  public View getView() {
    return this.mView;
  }

  public interface SelectBtnListener {
    //0---话题；1--城市
    void onBtnSelected(int id);
  }
}
