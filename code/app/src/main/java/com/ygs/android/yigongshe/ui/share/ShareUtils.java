package com.ygs.android.yigongshe.ui.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

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

  /**
   * 分享网页到微信
   *
   * @param url 网页地址
   * @param title 网页标题
   * @param description 网页描述
   * @param thumbSource 网页缩略图资源
   * @param isCircle 是否分享到朋友圈
   */
  public static void shareWebpageToWechat(String url, String title, String description,
      Bitmap thumbSource, boolean isCircle) {
    if (TextUtils.isEmpty(url)) {
      return;
    }

    //WXWebpageObject webpage = new WXWebpageObject();
    //webpage.webpageUrl = url;
    //
    //WXMediaMessage msg = new WXMediaMessage(webpage);
    //msg.title = Tools.trim(title);
    //msg.description = Tools.trim(description);
    //if (thumbSource != null) {
    //  msg.thumbData = Tools.revitionImageSize(thumbSource, 150, 20 * 1024);
    //  // if (thumbSource != null)
    //  // {
    //  // thumbSource.recycle();
    //  // }
    //}
    //
    //shareToWechat("webpage", msg, isCircle);
  }
}
