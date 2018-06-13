package com.weikan.app.common.net;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.weikan.app.account.AccountManager;
import com.weikan.app.common.manager.FunctionConfig;
import com.weikan.app.common.net.bean.AppConfigObject;
import com.weikan.app.util.URLDefine;
import platform.http.HttpUtils;
import platform.http.result.FailedResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liujian on 16/11/6.
 */
public class CommonAgent {

    public static final String DEFAULT_HOST = URLDefine.HUN_WATER_HOST_API;

    public static String makeUrl(String path) {
        return makeUrl(DEFAULT_HOST, path);
    }

    public static String makeUrl(String host, String path) {
        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http");
        builder.encodedAuthority(host);
        builder.encodedPath(path);

        return builder.build().toString();
    }

    /**
     * 绑定推送状态
     *
     */
    public static void pushBind( ) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.PUSH_BIND_PATH);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, !TextUtils.isEmpty(AccountManager.getInstance().getUserId()) ? AccountManager.getInstance().getUserId() : "");
        params.put("device_type", "1");

        HttpUtils.get(builder.build().toString(), params, new platform.http.responsehandler.SimpleJsonResponseHandler() {
            @Override
            public void success() {

            }

            @Override
            protected void failed(FailedResult r) {
                r.setIsHandled(true);
            }
        });
    }

    /**
     * 获取app各种配置项
     *
     */
    public static void getAppConfig( ) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.CONFIG_APP);

        Map<String, String> params = new HashMap<String, String>();

        HttpUtils.get(builder.build().toString(), params, new platform.http.responsehandler.JsonResponseHandler<AppConfigObject>() {
            @Override
            public void success(@NonNull AppConfigObject data) {
                FunctionConfig.getInstance().setConfig(data);
            }

            @Override
            protected void failed(FailedResult r) {
                r.setIsHandled(true);
            }
        });
    }
}
