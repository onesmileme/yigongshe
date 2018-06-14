package com.ygs.android.yigongshe.ui.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;
import butterknife.ButterKnife;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.utils.SystemBarTintManager;

/**
 * Created by ruichao on 2018/6/13.
 */

public abstract class BaseActivity extends FragmentActivity {
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutResId());
    ButterKnife.bind(this);
    initIntent();
    iniView();
  }

  protected abstract void initIntent();

  protected abstract void iniView();

  protected abstract int getLayoutResId();

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();

    if (openTranslucentStatus()) {
      setTranslucentStatus(true);
    }
  }

  protected boolean openTranslucentStatus() {
    return true;
  }

  /**
   * 设置状态栏背景状态
   */
  protected void setTranslucentStatus(boolean on) {
    if (Build.VERSION.SDK_INT >= 19) {
      Window win = getWindow();
      WindowManager.LayoutParams winParams = win.getAttributes();
      final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
      if (on) {
        winParams.flags |= bits;
      } else {
        winParams.flags &= ~bits;
      }
      win.setAttributes(winParams);
      //创建状态栏的管理实例
      SystemBarTintManager tintManager = new SystemBarTintManager(this);
      //激活状态栏设置
      tintManager.setStatusBarTintEnabled(on);
      //设置颜色
      if (on) {
        tintManager.setStatusBarTintResource(R.color.colorPrimary);
      } else {
        tintManager.setStatusBarTintResource(R.color.transparent);
      }
    }
  }
}
