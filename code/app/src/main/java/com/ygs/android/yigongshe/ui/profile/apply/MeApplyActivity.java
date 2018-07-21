package com.ygs.android.yigongshe.ui.profile.apply;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import java.util.zip.Inflater;

/**
 * 申请时长
 */
public class MeApplyActivity extends BaseActivity implements View.OnClickListener {

  @BindView(R.id.me_apply_submit_btn) Button mSubmitButton;

  @BindView(R.id.layout_titlebar) CommonTitleBar mTitleBar;

  @BindView(R.id.me_apply_duration_et) EditText mDurationEditText;

  @BindView(R.id.me_apply_reason_et) EditText mReasonEditText;

  @BindView(R.id.apply_container_layout) ConstraintLayout constraintLayout;


  private  View loadingView ;

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
      submit();
    }
  }

  private void showLoading(){

    Log.e("APPLY", "showLoading: " );
    if (loadingView == null){

      loadingView = LayoutInflater.from(this).inflate(R.layout.loading_view,constraintLayout);
      View view = loadingView.findViewById(R.id.loadingview);
      view.setBackgroundColor(Color.TRANSPARENT);

      constraintLayout.removeView(loadingView);
      ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      constraintLayout.addView(loadingView,0,layoutParams);

    }

    Log.e("APPLY", "showLoading: loadingview is: "+loadingView );

    loadingView.setVisibility(View.VISIBLE);

  }

  private void hideLoading(){
    loadingView.setVisibility(View.GONE);
  }


  private void submit(){

    showLoading();


  }
}
