package com.ygs.android.yigongshe.ui.profile.apply;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

/**
 * 我的申请
 */
public class MeApplyActivity extends BaseActivity implements View.OnClickListener {

  @BindView(R.id.me_apply_submit_btn) Button mSubmitButton;

  @BindView(R.id.layout_titlebar) CommonTitleBar mTitleBar;

  @BindView(R.id.me_apply_duration_et) EditText mDurationEditText;

  @BindView(R.id.me_apply_reason_et) EditText mReasonEditText;

  protected void initIntent(Bundle bundle) {

  }

  protected void initView() {

    mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override
      public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
          finish();
        }
      }
    });
    mSubmitButton.setOnClickListener(this);

  }

  protected int getLayoutResId() {
    return R.layout.activity_me_apply;
  }

  public void onClick(View view) {
    if (view == mSubmitButton) {

    }
  }
}
