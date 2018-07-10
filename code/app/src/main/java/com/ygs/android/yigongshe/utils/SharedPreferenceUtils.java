package com.ygs.android.yigongshe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.ygs.android.yigongshe.YGApplication;

/**
 * Created by ruichao on 2018/7/10.
 * 不使用。使用AccountManager.java
 */

public class SharedPreferenceUtils {
  private SharedPreferences sp;

  private SharedPreferences.Editor editor;

  private static class SingletonHolder {
    private static SharedPreferenceUtils instance =
        new SharedPreferenceUtils(YGApplication.mApplication);
  }

  public static SharedPreferenceUtils getInstance() {
    return SingletonHolder.instance;
  }

  private SharedPreferenceUtils(Context context) {
    sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
    editor = sp.edit();
  }

  public void setAccessToken(String accessToken) {
    editor.putString("accessToken", accessToken);
    editor.commit();
  }

  public String getAccessToken() {
    return sp.getString("accessToken", null);
  }
}
