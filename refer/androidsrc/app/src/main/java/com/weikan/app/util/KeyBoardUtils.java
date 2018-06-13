package com.weikan.app.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * 键盘操作工具类
 * 
 * @author Patrick.Li
 * 
 */
public class KeyBoardUtils {

	/**
	 * 隐藏键盘
	 * 
	 * @param context
	 * @param view
	 *            当前获得输入焦点的控件
	 */
	public static void hide(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 隐藏键盘
	 * 
	 * @param activity
	 */
	public static void hide(Activity activity) {
		// 如果键盘显示，则隐藏
		if (activity.getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			View view = activity.getCurrentFocus();
			if (view != null) {
				imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}
}
