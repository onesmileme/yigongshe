package com.weikan.app.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.weikan.app.BuildConfig;

import java.io.IOException;

/**
 * Created by Real on 15/7/29.
 */
public class AppUtils {

    static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

    /**
     * 取得设备型号
     *
     * @return 制造商名称-设备型号
     */
    public static String getDeviceModel() {
        return Build.MANUFACTURER + "-" + Build.MODEL;
    }

    /**
     * 取得Sdk版本号
     *
     * @return 19或者22等等
     */
    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 取得App版本
     *
     * @return 返回versionCode
     */
    public static int getAppVersion() {
        int appVersion = 0;
        PackageManager pm = sContext.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(sContext.getPackageName(), 0);
            appVersion = pi.versionCode;
        } catch (Exception e) {
            // 忽略找不到包信息的异常
        }

        return appVersion;
    }

    /**
     * 取得App版本
     *
     * @return 返回versionName
     */
    public static String getAppVersionName() {
        String appVersion = "";
        PackageManager pm = sContext.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(sContext.getPackageName(), 0);
            appVersion = pi.versionName;
        } catch (Exception e) {
            // 忽略找不到包信息的异常
        }

        return appVersion;
    }

    public static String getChannelCode() {
        if (BuildConfig.TEST_URL) {
            return "00";
        }
        if (TextUtils.isEmpty(sChannel)) {
            readChannel();
        }
        return sChannel;
    }


    public static String getAppkey() {
        String appkey = "";

        PackageManager pm = sContext.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(
                    sContext.getPackageName(), PackageManager.GET_META_DATA);
            appkey = info.metaData.getString("APP_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            // 忽略找不到包信息的异常
        }

        return appkey;
    }

    public static String getChannelName() {
        if (BuildConfig.TEST_URL) {
            return getAppkey() + "_test";
        }
        if (sChannelName == null) {
            readChannel();
        }
        return getAppkey() + sChannelName;
    }

    static String sChannel = "";
    static String sChannelName;

    private static void readChannel() {
        try {
            String channel = MCPTool.getChannelId(sContext, "99/");
            int i = channel.indexOf('/');
            if (i == -1) {
                throw new IOException("channel info error : " + channel);
            }
            sChannel = channel.substring(0, i);
            sChannelName = channel.substring(i + 1);
            if (TextUtils.isEmpty(sChannel) || TextUtils.isEmpty(sChannelName)) {
                throw new IOException("channel info error : " + channel);
            }
        } catch (IOException e) {
            // 如果读不到这个数，那么就当是无渠道
            sChannel = "99";
            sChannelName = "";
        }
    }
}
