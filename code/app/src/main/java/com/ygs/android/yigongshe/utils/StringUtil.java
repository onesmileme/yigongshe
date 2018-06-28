package com.ygs.android.yigongshe.utils;

import android.text.Html;
import android.text.Spanned;

/**
 * Created by ruichao on 2018/6/28.
 */

public class StringUtil {

  /**
   * 获取替换字符串
   *
   * @param data 字符串原型
   * @param args 要替换的参数
   * @return 替换后的字符串
   */
  public static String getReleaseString(String data, Object[] args) {
    String s = String.format(data, args);
    Spanned span = Html.fromHtml(s);
    return span.toString();
  }
}
