package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by ruichao on 2018/7/14.
 */

public class MyWebView extends WebView {
  private int mMaxHeight = -1;

  public MyWebView(Context context) {
    this(context, null);
  }

  public MyWebView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MyWebView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setMaxHeight(int height) {
    mMaxHeight = height;
  }

  public int getMaxHeight() {
    return mMaxHeight;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // TODO Auto-generated method stub
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    if (mMaxHeight > -1 && getMeasuredHeight() > mMaxHeight) {
      setMeasuredDimension(getMeasuredWidth(), mMaxHeight);
    }
  }
}