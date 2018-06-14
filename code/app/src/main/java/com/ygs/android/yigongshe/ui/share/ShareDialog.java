package com.ygs.android.yigongshe.ui.share;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.OnClick;
import com.ygs.android.yigongshe.R;

/**
 * Created by ruichao on 2018/6/14.
 */

public class ShareDialog extends Dialog {
  @BindView(R.id.ll_wechat) LinearLayout ll_wechat;
  @BindView(R.id.ll_wechat_circle) LinearLayout ll_wechat_circle;
  @BindView(R.id.ll_sinaweibo) LinearLayout ll_sinaweibo;
  private ShareListener shareListener;

  public ShareDialog(@NonNull Context context, ShareListener shareListener) {
    super(context, R.style.dialog_bottom);
    this.shareListener = shareListener;
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_share);
  }

  @OnClick({ R.id.ll_wechat, R.id.ll_wechat_circle, R.id.ll_sinaweibo })
  public void shareTo(LinearLayout ll) {
    switch (ll.getId()) {
      case R.id.ll_wechat:
        shareListener.shareToWechat();
        break;
      case R.id.ll_wechat_circle:
        shareListener.shareToWechatCircle();
        break;
      case R.id.ll_sinaweibo:
        shareListener.shareToWeibo();
        break;
    }
  }
}
