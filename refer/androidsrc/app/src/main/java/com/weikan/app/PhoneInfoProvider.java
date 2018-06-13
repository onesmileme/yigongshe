package com.weikan.app;

import android.os.Build;
import android.support.annotation.NonNull;

import com.lanjinger.cuid.util.CommonParam;
import com.weikan.app.account.AccountManager;
import com.weikan.app.util.AppUtils;

import java.util.HashMap;
import java.util.Map;

import platform.http.IPhoneInfoProvider;

/**
 * Created by kailun on 16/7/28.
 */
public class PhoneInfoProvider implements IPhoneInfoProvider {

    private Map<String, String> basicInfo;

    @Override
    public void modifyForGet(@NonNull Map<String, String> params) {
        params.put("os", "android");
        params.put("ov", AppUtils.getSdkVersion() + "");
        params.put("mb", Build.MANUFACTURER+"-"+Build.MODEL);
        params.put("sv", AppUtils.getAppVersionName() + "");
        params.put("net","");
        params.put("cuid", CommonParam.getCuidForLanjing(MainApplication.getInstance()));
        params.put("appkey", AppUtils.getAppkey());
        params.put("channel", AppUtils.getChannelCode());
        params.put("token", AccountManager.getInstance().getSession());
    }

    @NonNull
    public Map<String, String> getParams() {
        if (basicInfo == null) {
            initBasicInfo();
        }
        return basicInfo;
    }

    @NonNull
    public String getCuid() {
        return CommonParam.getCuidForLanjing(MainApplication.getInstance());
    }

    private void initBasicInfo() {
        HashMap<String, String> map = new HashMap<>();
        map.put("os", "android");
        map.put("ov", AppUtils.getSdkVersion() + "");
        map.put("mb", Build.MANUFACTURER+"-"+Build.MODEL);
        map.put("sv", AppUtils.getAppVersionName() + "");
        map.put("cuid", CommonParam.getCuidForLanjing(MainApplication.getInstance()));
        map.put("net","");
        map.put("appkey", AppUtils.getAppkey());
        map.put("channel", AppUtils.getChannelCode());
        map.put("token", AccountManager.getInstance().getSession());

        basicInfo = map;
    }
}
