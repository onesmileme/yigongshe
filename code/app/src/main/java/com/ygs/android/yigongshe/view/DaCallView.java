package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.response.ActivityDetailResponse;
import com.ygs.android.yigongshe.utils.StringUtil;

/**
 * Created by ruichao on 2018/6/28.
 */

public class DaCallView {
  @BindView(R.id.callstatus) TextView mCallStatus;
  @BindView(R.id.markgood) TextView mMardgood;
  private View mView;
  private Context mContext;

  public DaCallView(Context context, ViewGroup root) {
    mContext = context;
    initView(context, root);
  }

  private void initView(Context context, ViewGroup root) {
    mView = LayoutInflater.from(context).inflate(R.layout.view_dacall, root, false);
    ButterKnife.bind(this, mView);
  }

  public void setDacallViewData(ActivityDetailResponse data) {
    mMardgood.setText(data.cur_call_num);
    mCallStatus.setText(
        StringUtil.getReleaseString(mContext.getResources().getString(R.string.dacallstatus),
            new Object[] { data.target_call_num, data.cur_call_num }));
  }

  public View getView() {
    return this.mView;
  }
}
