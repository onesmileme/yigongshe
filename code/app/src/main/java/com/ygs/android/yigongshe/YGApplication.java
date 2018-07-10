package com.ygs.android.yigongshe;

import android.app.Application;
import com.ygs.android.yigongshe.account.AccountManager;

public class YGApplication extends Application {

  public static AccountManager accountManager = null;
  public static Application mApplication;

  public void onCreate() {

    super.onCreate();
    mApplication = this;
    accountManager = new AccountManager(getApplicationContext());
  }
}
