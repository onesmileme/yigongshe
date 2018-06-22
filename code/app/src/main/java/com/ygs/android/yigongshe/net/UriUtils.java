package com.ygs.android.yigongshe.net;

import com.ygs.android.yigongshe.BuildConfig;

/**
 * Created by ruichao on 2018/6/17.
 */

public class UriUtils {
  public static String getBaseUri() {
    if (BuildConfig.DEBUG) {
      return "http://47.104.211.211/";
    } else {
      return null;
    }
  }
}
