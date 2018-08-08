package com.ygs.android.yigongshe;

import android.app.Application;
import android.os.Debug;

import com.baidu.mapapi.SDKInitializer;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.utils.LocationService;

import cn.jpush.android.api.JPushInterface;

public class YGApplication extends Application {

  public static AccountManager accountManager = null;
  public static Application mApplication;
  public LocationService locationService;

  @Override
  public void onCreate() {

    super.onCreate();
    mApplication = this;
    accountManager = new AccountManager(getApplicationContext());
    /***
     * 初始化定位sdk
     */
    locationService = new LocationService(getApplicationContext());
    SDKInitializer.initialize(getApplicationContext());

    JPushInterface.init(this);

    JPushInterface.setDebugMode(true);
  }
}
