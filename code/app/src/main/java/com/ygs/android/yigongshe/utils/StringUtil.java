package com.ygs.android.yigongshe.utils;

import android.text.Html;
import android.text.Spanned;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.text.TextUtils.isEmpty;

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

  /** MD5 encrypt */
  public static String md5(String str) {
    try {
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      localMessageDigest.update(str.getBytes());
      byte[] arrayOfByte = localMessageDigest.digest();
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < arrayOfByte.length; i++) {
        int j = 0xFF & arrayOfByte[i];
        if (j < 16) {
          localStringBuffer.append("0");
        }
        localStringBuffer.append(Integer.toHexString(j));
      }
      return localStringBuffer.toString();
    } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
      localNoSuchAlgorithmException.printStackTrace();
    }
    return "";
  }

  public static String trim(String text) {
    return isEmpty(text) ? "" : text.trim();
  }
}
