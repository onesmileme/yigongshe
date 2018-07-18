package com.ygs.android.yigongshe.ui.share;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ygs.android.yigongshe.R;

/**
 * Created by ruichao on 2018/6/14.
 */

public class ShareDialog extends Dialog {
  private ShareListener shareListener;

  public ShareDialog(@NonNull Context context, ShareListener shareListener) {
    super(context, R.style.dialog_bottom);
    this.shareListener = shareListener;
    View view = View.inflate(context, R.layout.dialog_share, null);
    Window window = this.getWindow();
    window.setContentView(view);
    ButterKnife.bind(this, view);
  }

  @OnClick({ R.id.ll_wechat, R.id.ll_wechat_circle, R.id.ll_sinaweibo })
  public void shareTo(LinearLayout ll) {
    dismiss();
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
