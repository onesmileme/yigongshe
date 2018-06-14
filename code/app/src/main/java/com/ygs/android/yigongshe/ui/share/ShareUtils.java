package com.ygs.android.yigongshe.ui.share;

import android.content.Context;

/**
 * Created by ruichao on 2018/6/14.
 */

public class ShareUtils {
  public static void shareTo(Context context) {
    new ShareDialog(context, new ShareListener() {
      @Override public void shareToWechat() {

      }

      @Override public void shareToWechatCircle() {

      }

      @Override public void shareToWeibo() {

      }
    });
  }
}
