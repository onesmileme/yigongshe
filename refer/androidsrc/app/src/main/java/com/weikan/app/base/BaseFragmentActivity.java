package com.weikan.app.base;

import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;
import com.weikan.app.AppManager;
import com.weikan.app.Constants;
import com.weikan.app.LoadingDialog;
import com.weikan.app.R;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 基类Activity
 * 
 * @author Patrick.Li
 * 
 */
public class BaseFragmentActivity extends SwipeBackActivity {

	private DialogFragment loadingDialog;


	public void showLoadingDialog() {
		if (loadingDialog == null) {
			loadingDialog = LoadingDialog.newInstance();
		}

		if (loadingDialog.isAdded()) {
			return;
		}

		loadingDialog.show(getFragmentManager(), "");
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.add(loadingDialog, null);
//        transaction.commit();
	}

	public void hideLoadingDialog() {
		if (loadingDialog == null) {
			return;
		}

		loadingDialog.dismissAllowingStateLoss();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (openTranslucentStatus()) {
			setTranslucentStatus(true);
		}

		String nameString = this.getClass().getSimpleName();
		MobclickAgent.onPageStart(nameString); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();

		String nameString = this.getClass().getSimpleName();
		MobclickAgent.onPageEnd(nameString); // 保证 onPageEnd 在onPause
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		AppManager.addActivity(this, isSupportSwipBack());
		setContentView(R.layout.content);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Constants.SCREEN_DENSITY = metrics.density;
		Constants.SCREEN_HEIGHT = metrics.heightPixels;
		Constants.SCREEN_WIDTH = metrics.widthPixels;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.finishActivity(this);
	}

	/**
	 * 是否支持左滑返回效果
	 * @return
	 */
	public boolean isSupportSwipBack(){
		return true;
	}

	protected boolean openTranslucentStatus(){
		return true;
	}

	/**
	 * 不能后退到上一个Fragment
	 * 
	 * @param fragment
	 */
	public void replaceFragment(Fragment fragment) {
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.fragment_content, fragment);
		transaction.commitAllowingStateLoss();
	}

	/**
	 * 可以后退到上一个Fragment
	 * 
	 * @param fragment
	 */
	public void addFragment(Fragment fragment) {
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.fragment_content, fragment);
		transaction.addToBackStack(fragment.getClass().getName());
		transaction.commitAllowingStateLoss();
	}

	/**
	 * 不能后退到上一个Fragment
	 * 
	 * @param fragment
	 */
	public void addFragmentWithoutBack(Fragment fragment) {
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content, fragment);
		transaction.commitAllowingStateLoss();
	}

	/**
	 * 后退到后退栈中的某个Fragment
	 * 
	 * @param fragmentName
	 */
	public void backToFragment(String fragmentName) {
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		fragmentManager.popBackStackImmediate(fragmentName, 0);
		transaction.commitAllowingStateLoss();
	}

	/**
	 * 后退到第一个Fragment
	 * 
	 */
	public void backToTop() {
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
			fragmentManager.popBackStack();
		}
		// fragmentManager.popBackStack(null,
		// FragmentManager.POP_BACK_STACK_INCLUSIVE);
		transaction.commitAllowingStateLoss();
	}

	/**
	 * 设置状态栏背景状态
	 */
	protected void setTranslucentStatus(boolean on)
	{

		if(Build.VERSION.SDK_INT>=19) {
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
			tintManager.setStatusBarTintEnabled(true);
			//设置颜色
			tintManager.setStatusBarTintResource(R.color.news_titlebar_bg);
		}
	}

	/**
	 * 获取状态栏高度
	 * @return
	 */
	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

}
