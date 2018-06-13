package com.weikan.app.personalcenter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.weikan.app.account.AccountManager;
import com.weikan.app.account.bean.LoginResult;
import com.weikan.app.bean.UserInfoObject;
import com.weikan.app.original.bean.UploadImageObject;
import com.weikan.app.personalcenter.bean.FollowResultObject;
import com.weikan.app.personalcenter.bean.MyAttentionResponseObject;
import com.weikan.app.util.URLDefine;

import java.util.HashMap;
import java.util.Map;

import platform.http.HttpUtils;
import platform.http.responsehandler.ConfusedJsonResponseHandler;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;

import static com.weikan.app.common.net.CommonAgent.makeUrl;

/**
 * 个人中心的Agent
 *
 * @author kailun on 16/7/28.
 */
@SuppressWarnings("WeakerAccess")
public class PersonalAgent {

    public static void getUserHome(@NonNull String myUid,
                                   @NonNull String targetUid,
                                   @NonNull JsonResponseHandler<UserInfoObject> handler) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.USER_HOME);

        Map<String, String> params = new HashMap<>();
        params.put("uid", myUid);
        params.put("search_uid", targetUid);
        HttpUtils.get(builder.build().toString(), params, handler);
    }

    public static void postUserInfo(@NonNull UserInfoObject.UserInfoContent userInfo,
                                    @NonNull SimpleJsonResponseHandler handler) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.USER_EDIT);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put("headimgurl", userInfo.headimgurl);
        params.put("nickname", userInfo.nick_name);
        params.put("sex", userInfo.sex + "");
        params.put("autograph", userInfo.autograph);
        params.put("birthday", userInfo.birthday + "");
        params.put("province", userInfo.province + "");
        params.put("city", userInfo.city + "");

        HttpUtils.urlEncodedPost(builder.build().toString(), params, handler);
    }

    public static void getAttentionList(
            final int relType, // 1: 关注列表, 2: 粉丝列表
            @NonNull final String myUid,
            @NonNull final String targetUid,
            @NonNull final String type,
            final int lastUTime,
            @NonNull final ConfusedJsonResponseHandler<MyAttentionResponseObject> handler) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.USER_REL_LIST);

        Map<String, String> params = new HashMap<>();
        params.put("rel_type", relType + "");
        params.put("uid", myUid);
        params.put("search_uid", targetUid);
        params.put("type", type);

        if (type.equals("next")) {
            params.put("last_utime", lastUTime + "");
        }

        HttpUtils.get(builder.build().toString(), params, handler);
    }

    public static void postAddFollow(@NonNull final String myUid,
                                     @NonNull final String targetUid,
                                     @NonNull final JsonResponseHandler<FollowResultObject> handler) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.USER_ADD_FOLLOW);


        Map<String, String> params = new HashMap<>();
        params.put("uid", myUid);
        params.put("search_uid", targetUid);

        HttpUtils.urlEncodedPost(builder.build().toString(), params, handler);
    }

    public static void postCancelFollow(@NonNull final String myUid,
                                        @NonNull final String targetUid,
                                        @NonNull final JsonResponseHandler<FollowResultObject> handler) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.USER_CANCEL_FOLLOW);


        Map<String, String> params = new HashMap<>();
        params.put("uid", myUid);
        params.put("search_uid", targetUid);

        HttpUtils.urlEncodedPost(builder.build().toString(), params, handler);
    }

    public static void postUserVerify(
            @NonNull final String uid,
            @NonNull final String realName,
            @NonNull final String identify,
            @NonNull final String company,
            @NonNull final String duty,
            @NonNull final String city,
            @NonNull final UploadImageObject pics,
            @NonNull final SimpleJsonResponseHandler handler) {

        // real_name    : 真实姓名
        // identify     : 认证身份
        // company      : 所在公司
        // title        : 职位
        // city         : 市
        // uid          : 用户id
        // pics         : 图片信息        json格式

        String url = makeUrl(URLDefine.USER_VERIFY_SUBMIT);

        Map<String, String> params = new ArrayMap<>();
        params.put("uid", uid);
        params.put("real_name", realName);
        params.put("identify", identify);
        params.put("company", company);
        params.put("title", duty);
        params.put("city", city);
        params.put("pics", JSON.toJSONString(pics.img));

        HttpUtils.urlEncodedPost(url, params, handler);
    }


    /**
     * 登录，使用手机号
     *
     * @param phone   phone
     * @param pass    pass
     * @param handler handler
     */
    public static void postLoginByPhone(@NonNull String phone, @NonNull String pass,
                                        @NonNull JsonResponseHandler<LoginResult.UserInfoContent> handler) {
        String url = makeUrl(URLDefine.USER_SELF_LOGIN);
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phone);
        params.put("password", pass);

        HttpUtils.urlEncodedPost(url, params, handler);
    }

    /**
     * 注册，使用手机号
     *
     * @param phone   phone
     * @param pass    pass
     * @param handler handler
     */
    public static void postRegistByPhone(@NonNull String phone, @NonNull String code, @NonNull String pass, @NonNull String name,
                                         @NonNull JsonResponseHandler<LoginResult.UserInfoContent> handler) {
        String url = makeUrl(URLDefine.USER_MOBILE_REGISTER);
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phone);
        params.put("verifycode", code);
        params.put("nickname", name);
        params.put("password", pass);

        HttpUtils.urlEncodedPost(url, params, handler);
    }

    /**
     * 忘记密码，使用手机号
     *
     * @param phone   phone
     * @param pass    pass
     * @param handler handler
     */
    public static void postForgetPwd(@NonNull String phone, @NonNull String code, @NonNull String pass,
                                     @NonNull SimpleJsonResponseHandler handler) {
        String url = makeUrl(URLDefine.USER_FORGET_PASSWORD);
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phone);
        params.put("verifycode", code);
        params.put("password", pass);

        HttpUtils.urlEncodedPost(url, params, handler);
    }

}
