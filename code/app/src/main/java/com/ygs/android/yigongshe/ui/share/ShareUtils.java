package com.ygs.android.yigongshe.ui.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Toast;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.ShareBean;
import com.ygs.android.yigongshe.utils.StringUtil;

/**
 * Created by ruichao on 2018/6/14.
 */

public class ShareUtils {
  public static final String WX_APP_ID = "wx397a918840390b93";
  public static final String WeiBo_APP_ID = "3718455954";
  public static final String WeiBo_APP_SECRET = "5474c77225cfb99fadc866a2eb5e59b0";
  private IWXAPI mIWXAPI;
  private Context mContext;
  private WbShareHandler shareHandler;

  public ShareUtils() {
    mContext = YGApplication.mApplication;
  }

  public static ShareUtils getInstance() {
    return SingletonHolder.instance;
  }

  private static class SingletonHolder {
    private static ShareUtils instance = new ShareUtils();
  }

  public void regToWx() {
    mIWXAPI = WXAPIFactory.createWXAPI(mContext, WX_APP_ID, false);
    mIWXAPI.registerApp(WX_APP_ID);
  }

  public void regToWeibo() {
    WbSdk.install(mContext, new AuthInfo(mContext, WeiBo_APP_ID, null, null));
  }

  public void shareTo(final Context context, final ShareBean shareBean) {
    new ShareDialog(context, new ShareListener() {
      @Override public void shareToWechat() {
        shareWebpageToWechat(shareBean, false);
      }

      @Override public void shareToWechatCircle() {
        shareWebpageToWechat(shareBean, true);
      }

      @Override public void shareToWeibo() {
        shareToWeiBo(context, shareBean);
      }
    }).show();
  }

  /**
   * 分享网页到微信
   * //*
   * //* @param url 网页地址
   * //* @param title 网页标题
   * //* @param description 网页描述
   * //* @param thumbSource 网页缩略图资源
   *
   * @param isCircle 是否分享到朋友圈
   */
  private void shareWebpageToWechat(ShareBean shareBean, boolean isCircle) {
    if (TextUtils.isEmpty(shareBean.url)) {
      return;
    }

    WXWebpageObject webpage = new WXWebpageObject();
    webpage.webpageUrl = shareBean.url;
    //
    WXMediaMessage msg = new WXMediaMessage(webpage);
    msg.title = StringUtil.trim(shareBean.title);
    msg.description = StringUtil.trim(shareBean.description);
    //if (thumbSource != null) {
    //  msg.thumbData = Tools.revitionImageSize(thumbSource, 150, 20 * 1024);
    //  // if (thumbSource != null)
    //  // {
    //  // thumbSource.recycle();
    //  // }
    //}
    //
    shareToWechat("webpage", msg, isCircle);
  }

  private void shareToWechat(String type, WXMediaMessage msg, boolean isCircle) {
    if (mIWXAPI == null) {
      ShareUtils.getInstance().regToWx();
    }
    if (mIWXAPI.isWXAppInstalled()) {
      mIWXAPI.registerApp(WX_APP_ID);
      SendMessageToWX.Req req = new SendMessageToWX.Req();
      req.transaction = buildTransaction(StringUtil.trim(type));
      req.message = msg;
      req.scene = isCircle ? 1 : 0;
      mIWXAPI.sendReq(req);
    } else {
      Toast.makeText(mContext, "没有安装微信", Toast.LENGTH_SHORT).show();
    }
  }

  private String buildTransaction(String type) {
    return type == null ? String.valueOf(System.currentTimeMillis())
        : type + System.currentTimeMillis();
  }

  private void shareToWeiBo(Context context, ShareBean shareBean) {
    //微博
    shareHandler = new WbShareHandler((Activity) context);
    shareHandler.registerApp();
    sendMultiMessage(shareBean, true, false);
  }

  private void sendMultiMessage(ShareBean shareBean, boolean hasText, boolean hasImage) {

    WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
    if (hasText) {
      weiboMessage.textObject = getTextObj(shareBean);
    }
    if (hasImage) {
      weiboMessage.imageObject = getImageObj();
    }
    shareHandler.shareMessage(weiboMessage, false);
  }

  /**
   * 获取分享的文本模板。
   */
  private String getSharedText() {
    int formatId = R.string.weibosdk_demo_share_text_template;
    String format = mContext.getResources().getString(formatId);
    String text = format;
    return text;
  }

  /**
   * 创建文本消息对象。
   *
   * @return 文本消息对象。
   */
  private TextObject getTextObj(ShareBean shareBean) {
    TextObject textObject = new TextObject();
    textObject.text = getSharedText();
    textObject.title = shareBean.title;
    textObject.actionUrl = shareBean.url;
    return textObject;
  }

  /**
   * 创建图片消息对象。
   *
   * @return 图片消息对象。
   */
  private ImageObject getImageObj() {
    ImageObject imageObject = new ImageObject();
    Bitmap bitmap =
        BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_foreground);
    imageObject.setImageObject(bitmap);
    return imageObject;
  }

  /**
   * 创建多媒体（网页）消息对象。
   *
   * @return 多媒体（网页）消息对象。
   */
  private WebpageObject getWebpageObj() {
    WebpageObject mediaObject = new WebpageObject();
    mediaObject.identify = Utility.generateGUID();
    mediaObject.title = "测试title";
    mediaObject.description = "测试描述";
    Bitmap bitmap =
        BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_background);
    // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
    mediaObject.setThumbImage(bitmap);
    mediaObject.actionUrl = "http://news.sina.com.cn/c/2013-10-22/021928494669.shtml";
    mediaObject.defaultText = "Webpage 默认文案";
    return mediaObject;
  }
}
