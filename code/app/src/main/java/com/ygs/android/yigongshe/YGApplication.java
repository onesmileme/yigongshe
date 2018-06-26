package com.ygs.android.yigongshe;

import android.app.Application;

import com.ygs.android.yigongshe.account.AccountManager;

public class YGApplication extends Application {

    static AccountManager instance = null;

    public void onCreate(){

        super.onCreate();

        instance = new AccountManager(getApplicationContext());
    }




}
