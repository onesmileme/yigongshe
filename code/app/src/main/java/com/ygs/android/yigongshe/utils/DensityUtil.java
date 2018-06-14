package com.ygs.android.yigongshe.utils;

import android.content.Context;
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
}
