package com.ygs.android.yigongshe;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import butterknife.BindView;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.login.LoginActivity;

/**
 * Created by ruichao on 2018/8/15.
 */

public class SlpashActivity extends BaseActivity {
  @BindView(R.id.splashImage) ImageView mSplash;

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
  }

  @Override protected void initIntent(Bundle bundle) {

  }

  @Override protected void initView() {
    iniImage();
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_splash;
  }

  private void iniImage() {
    //
    //ScaleAnimation scaleAnim =
    //    new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f,
    //        Animation.RELATIVE_TO_SELF, 0.5f);
    //
    //scaleAnim.setFillAfter(true);
    //scaleAnim.setDuration(2000);
    //scaleAnim.setAnimationListener(new Animation.AnimationListener() {
    //  @Override public void onAnimationStart(Animation animation) {
    //
    //  }
    //
    //  @Override public void onAnimationEnd(Animation animation) {
    //    //在这里做一些初始化的操作
    //    //跳转到指定的Activity
    //    goToOthers(LoginActivity.class, null);
    //    finish();
    //  }
    //
    //  @Override public void onAnimationRepeat(Animation animation) {
    //
    //  }
    //});
    //mSplash.startAnimation(scaleAnim);

    Handler mHandler = new Handler();
    mHandler.postDelayed(new Runnable() {
      @Override public void run() {
        goToOthers(LoginActivity.class, null);
        finish();
      }
    }, 2000);
  }
}
