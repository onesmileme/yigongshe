package com.weikan.app.original;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.weikan.app.account.AccountManager;
import com.weikan.app.original.bean.UploadImageObject;
import platform.http.HttpUtils;
import com.weikan.app.util.URLDefine;
import platform.http.responsehandler.JsonResponseHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 原创模块的网络请求
 *
 * @author kailun on 16/4/14
 */
public class OriginalAgent {

    public static void uploadAvatar(@NonNull File imageFile, @NonNull JsonResponseHandler<UploadImageObject> handler) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.UPLOAD_USER_PIC);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());

        Map<String, File> fileParams = new HashMap<>();
        fileParams.put("file", imageFile);

        HttpUtils.multipartPost(builder.build().toString(), params, fileParams, handler);
    }

    public static void newUploadAvatar(@NonNull File imageFile,
                                       @NonNull platform.http.responsehandler.JsonResponseHandler<UploadImageObject> handler) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.UPLOAD_USER_PIC);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());

        Map<String, File> fileParams = new HashMap<>();
        fileParams.put("file", imageFile);

        HttpUtils.multipartPost(builder.build().toString(), params, fileParams, handler);
    }

    public static void uploadImage(@NonNull File imageFile, @NonNull JsonResponseHandler<UploadImageObject> handler) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.UPLOAD_TWEET_PIC);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());

        Map<String, File> fileParams = new HashMap<>();
        fileParams.put("file", imageFile);

        HttpUtils.multipartPost(builder.build().toString(), params, fileParams, handler);
    }
}
