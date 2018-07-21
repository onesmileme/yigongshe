package com.ygs.android.yigongshe.ui.share;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import com.ygs.android.yigongshe.R;

/**
 * Created by ruichao on 2018/6/14.
 */

public class ShareDialog extends Dialog {
  @BindView(R.id.rl_container) RelativeLayout mRelativeLayout;
  @BindView(R.id.shareView) LinearLayout mShareView;
  private ShareListener shareListener;

  public ShareDialog(@NonNull Context context, ShareListener shareListener) {
    super(context, R.style.dialog_bottom);
    this.shareListener = shareListener;
    View view = View.inflate(context, R.layout.dialog_share, null);
    Window window = this.getWindow();
    window.setContentView(view);
    window.setGravity(Gravity.BOTTOM);
    ButterKnife.bind(this, view);
  }

  @OnClick({ R.id.ll_wechat, R.id.ll_wechat_circle, R.id.ll_sinaweibo })
  public void shareTo(View ll) {
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

  @OnTouch(R.id.rl_container) public boolean onLeftTouched(View ll, MotionEvent ev) {
    if (ll.getId() == R.id.rl_container) {
      if (!isTouchPointInView(mShareView, ev.getX(), ev.getY())) {
        dismiss();
        return true;
      }
    }
    return false;
  }

  private boolean isTouchPointInView(View view, float x, float y) {
    if (view == null) {
      return false;
    }
    int[] location = new int[2];
    view.getLocationOnScreen(location);
    int left = location[0];
    int top = location[1];
    int right = left + view.getMeasuredWidth();
    int bottom = top + view.getMeasuredHeight();
    //view.isClickable() &&
    if (y >= top && y <= bottom && x >= left && x <= right) {
      return true;
    }
    return false;
  }
}
