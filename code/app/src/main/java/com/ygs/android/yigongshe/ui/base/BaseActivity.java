package com.ygs.android.yigongshe.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.utils.AppUtils;
import com.ygs.android.yigongshe.utils.KeyboardConflictCompat;

/**
 * Created by ruichao on 2018/6/13.
 */

public abstract class BaseActivity extends FragmentActivity {

  private Unbinder unbinder;
  public static final String PARAM_INTENT = "intentData";
  private Bundle intentData;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutResId());
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    unbinder = ButterKnife.bind(this);

    if (savedInstanceState == null) {
      intentData = getIntent().getExtras();
    } else {
      intentData = savedInstanceState.getBundle(PARAM_INTENT);
    }
    Bundle bundle =
        intentData != null && intentData.getBundle(PARAM_INTENT) != null ? intentData.getBundle(
            PARAM_INTENT) : intentData;
    initIntent(bundle != null ? bundle : new Bundle());
    initView();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putBundle(PARAM_INTENT, intentData);
    super.onSaveInstanceState(outState);
  }

  protected abstract void initIntent(Bundle bundle);

  protected abstract void initView();

  protected abstract int getLayoutResId();

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (!openTranslucentStatus()) {
      setTranslucentStatus();
    } else {
      KeyboardConflictCompat.assistWindow(getWindow());
      AppUtils.StatusBarLightMode(getWindow());
      AppUtils.transparencyBar(getWindow());
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }

  // true:沉浸式
  //false:非沉浸式，然后将状态栏默认设置为白色
  protected boolean openTranslucentStatus() {
    return true;
  }

  /**
   * 设置状态栏背景状态
   */
  protected void setTranslucentStatus() {
    //5.0以上
    if (Build.VERSION.SDK_INT >= 21) {
      Window win = getWindow();
      WindowManager.LayoutParams winParams = win.getAttributes();
      final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
      //if (on) {
      winParams.flags |= bits;
      //} else {
      //  winParams.flags &= ~bits;
      //}
      winParams.flags &= ~bits;
      win.setAttributes(winParams);
      //创建状态栏的管理实例
      //SystemBarTintManager tintManager = new SystemBarTintManager(this);
      //激活状态栏设置
      //tintManager.setStatusBarTintEnabled(on);
      //设置颜色
      //if (on) {
      //tintManager.setStatusBarTintResource(R.color.white);
      //} else {
      //tintManager.setStatusBarTintResource(R.color.white);
      // }

      //取消状态栏透明
      win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      //添加Flag把状态栏设为可绘制模式
      win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      //设置状态栏颜色
      win.setStatusBarColor(getResources().getColor(R.color.white));
      //设置系统状态栏处于可见状态
      win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
      //让view不根据系统窗口来调整自己的布局
      ViewGroup mContentView = (ViewGroup) win.findViewById(Window.ID_ANDROID_CONTENT);
      View mChildView = mContentView.getChildAt(0);
      if (mChildView != null) {
        ViewCompat.setFitsSystemWindows(mChildView, false);
        ViewCompat.requestApplyInsets(mChildView);
      }
    } else {
      Window window = getWindow();
      //设置Window为全透明
      window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

      ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
      //获取父布局
      View mContentChild = mContentView.getChildAt(0);
      //获取状态栏高度
      int statusBarHeight = AppUtils.getStatusBarHeight(this);

      //如果已经存在假状态栏则移除，防止重复添加
      removeFakeStatusBarViewIfExist(this);
      //添加一个View来作为状态栏的填充
      addFakeStatusBarView(this, getResources().getColor(R.color.white), statusBarHeight);
      //  //设置子控件到状态栏的间距
      addMarginTopToContentChild(mContentChild, statusBarHeight);
      //  //不预留系统栏位置
      if (mContentChild != null) {
        ViewCompat.setFitsSystemWindows(mContentChild, false);
      }
      //  //如果在Activity中使用了ActionBar则需要再将布局与状态栏的高度跳高一个ActionBar的高度，否则内容会被ActionBar遮挡
      int action_bar_id = getResources().getIdentifier("action_bar", "id", getPackageName());
      View view = findViewById(action_bar_id);
      if (view != null) {
        TypedValue typedValue = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
          int actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data,
              getResources().getDisplayMetrics());
          setContentTopPadding(this, actionBarHeight);
        }
      }
    }
  }

  private static void removeFakeStatusBarViewIfExist(Activity activity) {
    Window window = activity.getWindow();
    ViewGroup mDecorView = (ViewGroup) window.getDecorView();

    View fakeView = mDecorView.findViewWithTag("statusBarView");
    if (fakeView != null) {
      mDecorView.removeView(fakeView);
    }
  }

  //
  private static View addFakeStatusBarView(Activity activity, int statusBarColor,
      int statusBarHeight) {
    Window window = activity.getWindow();
    ViewGroup mDecorView = (ViewGroup) window.getDecorView();

    View mStatusBarView = new View(activity);
    FrameLayout.LayoutParams layoutParams =
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
    layoutParams.gravity = Gravity.TOP;
    mStatusBarView.setLayoutParams(layoutParams);
    mStatusBarView.setBackgroundColor(statusBarColor);
    mStatusBarView.setTag("statusBarView");

    mDecorView.addView(mStatusBarView);
    return mStatusBarView;
  }

  //
  private static void addMarginTopToContentChild(View mContentChild, int statusBarHeight) {
    if (mContentChild == null) {
      return;
    }
    if (!"marginAdded".equals(mContentChild.getTag())) {
      FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentChild.getLayoutParams();
      lp.topMargin += statusBarHeight;
      mContentChild.setLayoutParams(lp);
      mContentChild.setTag("marginAdded");
    }
  }

  //
  static void setContentTopPadding(Activity activity, int padding) {
    ViewGroup mContentView =
        (ViewGroup) activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
    mContentView.setPadding(0, padding, 0, 0);
  }

  protected void goToOthers(Class<?> cls, Bundle bundle) {
    Intent intent = new Intent(this, cls);
    intent.putExtra(BaseActivity.PARAM_INTENT, bundle);
    startActivity(intent);
  }

  protected void goToOthersForResult(Class<?> cls, Bundle bundle, int requestCode) {
    Intent intent = new Intent(this, cls);
    intent.putExtra(BaseActivity.PARAM_INTENT, bundle);
    startActivityForResult(intent, requestCode);
  }

  public void backForResult(Class<?> cls, Bundle bundle, int resultCode) {
    Intent intent = new Intent();
    if (cls != null) {
      intent.setClass(this, cls);
    }
    intent.putExtra(BaseActivity.PARAM_INTENT, bundle);
    setResult(resultCode, intent);
    finish();
  }

  @Override protected void onStop() {
    super.onStop();
  }
}
