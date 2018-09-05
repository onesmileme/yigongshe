package com.ygs.android.yigongshe;

import android.app.Application;
import android.os.Debug;

import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.push.PushManager;

import cn.jpush.android.api.JPushInterface;

public class YGApplication extends Application {

  public static AccountManager accountManager = null;
  public static Application mApplication;
  //public LocationService locationService;
  public static MainActivity mMainActivity;

  @Override
  public void onCreate() {

    super.onCreate();
    mApplication = this;
    accountManager = new AccountManager(getApplicationContext());
    /***
     * 初始化定位sdk
     */
    //locationService = new LocationService(getApplicationContext());
    //SDKInitializer.initialize(getApplicationContext());

    JPushInterface.init(this);
    PushManager.getInstance().getToken(this);
    JPushInterface.setDebugMode(true);
  }
}
