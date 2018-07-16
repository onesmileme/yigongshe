package com.ygs.android.yigongshe;

import android.app.Application;
import com.baidu.mapapi.SDKInitializer;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.utils.LocationService;

public class YGApplication extends Application {

  public static AccountManager accountManager = null;
  public static Application mApplication;
  public LocationService locationService;

  public void onCreate() {

    super.onCreate();
    mApplication = this;
    accountManager = new AccountManager(getApplicationContext());
    /***
     * 初始化定位sdk
     */
    locationService = new LocationService(getApplicationContext());
    SDKInitializer.initialize(getApplicationContext());
  }
}
