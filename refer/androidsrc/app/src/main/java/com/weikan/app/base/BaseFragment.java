package com.weikan.app.base;

import android.support.v4.app.Fragment;
import com.umeng.analytics.MobclickAgent;

/**
 * Fragment基类
 * 
 * @author Patrick.Li
 * 
 */
public class BaseFragment extends Fragment {

	/**
	 * 不能后退到上一个Fragment
	 * 
	 * @param fragment
	 */
	public void replaceFragment(Fragment fragment) {
		((BaseFragmentActivity) getActivity()).replaceFragment(fragment);
	}

	/**
	 * 可以后退到上一个Fragment
	 * 
	 * @param fragment
	 */
	public void addFragment(Fragment fragment) {
		((BaseFragmentActivity) getActivity()).addFragment(fragment);
	}

	/**
	 * 不能后退到上一个Fragment
	 * 
	 * @param fragment
	 */
	public void addFragmentWithoutBack(Fragment fragment) {
		((BaseFragmentActivity) getActivity()).addFragmentWithoutBack(fragment);
	}

	/**
	 * 后退到后退栈中的某个Fragment
	 * 
	 * @param fragmentName
	 */
	public void backToFragment(String fragmentName) {
		((BaseFragmentActivity) getActivity()).backToFragment(fragmentName);
	}

	/**
	 * 后退到第一个Fragment
	 * 
	 */
	public void backToTop() {
		((BaseFragmentActivity) getActivity()).backToTop();
	}

	/**
	 * 后退到前一个Fragment
	 *
	 */
	public void goback( ) {
		((BaseFragmentActivity) getActivity()).getSupportFragmentManager().popBackStack();
	}

	@Override
	public void onResume() {
		super.onResume();
		 String nameString = this.getClass().getSimpleName();
		 MobclickAgent.onPageStart(nameString); // 统计页面
	}

	@Override
	public void onPause() {
		super.onPause();
		 String nameString = this.getClass().getSimpleName();
		 MobclickAgent.onPageEnd(nameString);
	}

	// protected void showOrHideMenu() {
	// ((MainActivity) getActivity()).showOrHideMenu();
	// }

	public void showLoadingDialog(){
		if(getActivity() instanceof BaseFragmentActivity){
			((BaseFragmentActivity) getActivity()).showLoadingDialog();
		}
	}

	public void hideLoadingDialog(){
		if(getActivity() instanceof BaseFragmentActivity){
			((BaseFragmentActivity) getActivity()).hideLoadingDialog();
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
