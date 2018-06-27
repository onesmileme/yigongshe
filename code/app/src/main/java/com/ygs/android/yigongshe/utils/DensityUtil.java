package com.ygs.android.yigongshe.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 单位转换工具类
 *
 * @author wanghl-a
 */
public class DensityUtil {

  /**
   * Don't let anyone instantiate this class.
   */
  private DensityUtil() {
    throw new IllegalStateException("Do not need instantiate!");
  }

  /**
   * dip转换px
   *
   * @param context 上下文
   * @param dpValue dip值
   * @return px值
   */
  public static int dp2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /**
   * sp转pd
   *
   * @param context 上下文
   * @param spValue sp值
   * @return px值
   */
  public static int sp2px(Context context, float spValue) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue,
        context.getResources().getDisplayMetrics());
  }

  /**
   * px转换dip
   *
   * @param context 上下文
   * @param pxValue px值
   * @return dip值
   */
  public static int px2dp(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  public static int[] getScreenPixelSize(Context context) {
    DisplayMetrics metrics = getDisplayMetrics(context);
    return new int[] { metrics.widthPixels, metrics.heightPixels };
  }

  public static DisplayMetrics getDisplayMetrics(Context context) {
    Activity activity;
    if (!(context instanceof Activity) && context instanceof ContextWrapper) {
      activity = (Activity) ((ContextWrapper) context).getBaseContext();
    } else {
      activity = (Activity) context;
    }
    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    return metrics;
  }
}
