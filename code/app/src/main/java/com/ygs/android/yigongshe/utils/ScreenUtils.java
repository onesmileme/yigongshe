package com.ygs.android.yigongshe.utils;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by ruichao on 2018/6/27.
 */

public class ScreenUtils {
  public static void showSoftInputKeyBoard(Context context, View focusView) {
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(focusView, InputMethodManager.SHOW_FORCED);
  }

  public static void hideSoftInputKeyBoard(Context context, View focusView) {
    if (focusView != null) {
      IBinder binder = focusView.getWindowToken();
      if (binder != null) {
        InputMethodManager imd =
            (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imd.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_IMPLICIT_ONLY);
      }
    }
  }
}
