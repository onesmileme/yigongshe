package com.weikan.app.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import com.weikan.app.AppManager;
import com.weikan.app.LoginAndRgistActivity;
import com.weikan.app.MainApplication;
import com.weikan.app.account.bean.LoginEvent;
import com.weikan.app.account.bean.LoginResult;
import com.weikan.app.account.bean.LogoutEvent;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.base.BaseFragmentActivity;
import com.weikan.app.bean.UserInfoObject;
import com.weikan.app.common.net.CommonAgent;
import com.weikan.app.listener.LoginListener;
import com.weikan.app.personalcenter.BindPhoneActivity;
import com.weikan.app.util.LToast;
import com.weikan.app.util.PrefDefine;
import com.weikan.app.util.ShareTools;
import com.weikan.app.util.URLDefine;
import de.greenrobot.event.EventBus;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.result.FailedResult;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: liujian06 Date: 2015/2/7 Time: 15:16
 */
public class AccountManager {
    private final static String SESSION = "session";
    private final static String UID = "uid";
    private final static String USERDATA = "userdata";

    private static class Holder {
        public static final AccountManager inst = new AccountManager();
    }

    public static AccountManager getInstance() {
        return Holder.inst;
    }

    private AccountManager() {
    }

    private boolean isLogin = false;
    private boolean isVerify = false;

    private String userid = "";
    private UserInfoObject.UserInfoContent userdata;
    private String session;

    public String getUserId() {
        return isLogin ? userid : "0";
    }

    public String getDemoUserId() {
        return getUserId();
    }

    public void setUserData(UserInfoObject.UserInfoContent data) {
        this.userdata = data;
        if (userdata != null) {
            ByteArrayOutputStream bOut = null;
            ObjectOutputStream objOut = null;
            try {
                bOut = new ByteArrayOutputStream();
                objOut = new ObjectOutputStream(bOut);
                objOut.writeObject(userdata);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 写入登陆信息
            if (objOut != null) {
                SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(PrefDefine.PREF_FILE,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(USERDATA, Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT));
                editor.apply();
            }
        }
        EventBus.getDefault().post(new AccountObtainEvent(data));
    }

    /**
     * 获取用户信息，如果为空，自动发起请求，接收端直接接收消息通知
     *
     * @return
     */
    public UserInfoObject.UserInfoContent getUserData() {
        if (isLogin) {
            if (userdata == null) {
                readUserDataFromPref();
            }
            if (userdata != null) {
                return userdata;
            } else {
                updateUserInfo();
            }
        }
        return new UserInfoObject.UserInfoContent();
    }

    private void readUserDataFromPref() {
        // 从文件读取
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(PrefDefine.PREF_FILE,
                Context.MODE_PRIVATE);
        String data = sp.getString(USERDATA, null);
        if (data != null) {
            Object obj = null;
            try {
                ByteArrayInputStream bIn = new ByteArrayInputStream(Base64.decode(data, Base64.DEFAULT));
                ObjectInputStream objIn = new ObjectInputStream(bIn);
                obj = objIn.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (obj != null && obj instanceof UserInfoObject.UserInfoContent) {
                userdata = (UserInfoObject.UserInfoContent) obj;
            }
        }
    }

    public String getSession() {
        return isLogin ? session : "";
    }

    public void init(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PrefDefine.PREF_FILE, Context.MODE_PRIVATE);
        String se = sp.getString(SESSION, "");
        String uid = sp.getString(UID, "");
        if (!TextUtils.isEmpty(se) && !TextUtils.isEmpty(uid)) {
            session = se;
            userid = uid;
            isLogin = true;
        }
    }

    public void saveSession(Context context, String session, String uid) {
        this.isLogin = true;
        this.session = session;
        this.userid = uid;
        SharedPreferences sp = context.getSharedPreferences(PrefDefine.PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SESSION, session);
        editor.putString(UID, uid);
        editor.apply();

        CommonAgent.pushBind();
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void logout(Activity context) {
        isLogin = false;
        session = null;
        userid = null;
        userdata = null;
        SharedPreferences sp = context.getSharedPreferences(PrefDefine.PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(SESSION);
        editor.remove(UID);
        editor.remove(USERDATA);
        editor.apply();

        ShareTools.getInstance().exitThirdLogin(context, null);
        EventBus.getDefault().post(new LogoutEvent());

        CommonAgent.pushBind();
    }

    public void gotoLogin(final Activity context) {
        Intent intent = new Intent(context, LoginAndRgistActivity.class);
        context.startActivity(intent);
    }

    public void gotoDirectWeiXinLogin(final Activity context, final LoginListener loginListener){
//        Intent intent = new Intent();
////        intent.setClass(context, AccountLoginActivity.class);
//        intent.setClass(context, GuideActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        context.startActivity(intent);
        if (!ShareTools.isWeixinAvilible(context)) {
            LToast.showToast("请安装微信后再登陆");
            return;
        }

        final Activity act = AppManager.currentActivity();
//        if(act instanceof BaseFragmentActivity){
//            ((BaseFragmentActivity) act).showLoadingDialog();
//        }else if(act instanceof BaseActivity){
//            ((BaseActivity) act).showLoadingDialog();
//        }
        ShareTools.getInstance().LoginWeixin(context, new ShareTools.OnThirdLoginListener() {
            @Override
            public void onLoginSuccess() {
                String openid = ShareTools.getInstance().getLoginParam(context,
                        "weixin_openid");
                String iconurl = ShareTools.getInstance().getLoginParam(context,
                        "weixin_headimgurl");
                String name = ShareTools.getInstance().getLoginParam(context,
                        "weixin_nickname");
                String unionid = ShareTools.getInstance().getLoginParam(context,
                        "weixin_unionid");
                String token = ShareTools.getInstance().getLoginParam(context,
                        "weixin_access_token");
                sendUserLoginRequest(context,token,iconurl,"weixin",name,openid,unionid,loginListener);
            }

            @Override
            public void onLoginFailed() {
//                if(act instanceof BaseFragmentActivity){
//                    ((BaseFragmentActivity) act).hideLoadingDialog();
//                } else if(act instanceof BaseActivity){
//                    ((BaseActivity) act).hideLoadingDialog();
//                }
                LToast.showToast("登陆失败");
            }
        });
    }

    public void updateUserInfo() {
        if (isLogin()) {
            sendUserInfoRequest();
        }
    }

    private void sendUserInfoRequest() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.USERINFO_USER_INFO);
        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<UserInfoObject>() {
            @Override
            public void success(@NonNull UserInfoObject data) {
                if (data.content != null) {
                    setUserData(data.content);
                }
            }

        });
    }

    public void sendUserLoginRequest(final Context context, final String accessToken, String iconurl, String platform, String username, final String openid, final String unionid,final LoginListener loginListener) {
        final Activity act = AppManager.currentActivity();
//        if(act instanceof BaseFragmentActivity){
//            ((BaseFragmentActivity) act).showLoadingDialog();
//        }else if(act instanceof BaseActivity){
//            ((BaseActivity) act).showLoadingDialog();
//        }
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.USER_LOGIN);
        Map<String, String> params = new HashMap<String, String>();
        params.put("accessToken", accessToken);
        params.put("iconURL", iconurl);
        params.put("platformName", platform);
        params.put("userName", username);
        params.put("usid", openid);
        params.put("unionid", unionid);
        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<LoginResult.UserInfoContent>() {
            @Override
            public void success(@NonNull LoginResult.UserInfoContent data) {
                if (act instanceof BaseFragmentActivity) {
                    ((BaseFragmentActivity) act).hideLoadingDialog();
                } else if (act instanceof BaseActivity) {
                    ((BaseActivity) act).hideLoadingDialog();
                }
                if(data.need_bind==0) {
                    // 不需要绑定手机号，登陆成功
                    LToast.showToast("登陆成功");
                    onUserLoginSuccess(context, data);
                    if (loginListener != null) {
                        loginListener.success();
                    }
                } else {
                    // 需要绑定手机号
                    LToast.showToast("请绑定手机号。");
                    Intent intent = new Intent(context, BindPhoneActivity.class);
                    intent.putExtra(URLDefine.UID, data.uid);
                    context.startActivity(intent);
                }
            }

            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
//                if (act instanceof BaseFragmentActivity) {
//                    ((BaseFragmentActivity) act).hideLoadingDialog();
//                } else if (act instanceof BaseActivity) {
//                    ((BaseActivity) act).hideLoadingDialog();
//                }
            }
        });
    }

    public void onUserLoginSuccess(final Context context, LoginResult.UserInfoContent data){
        saveSession(context, data.token, data.uid);
        sendUserInfoRequest();
        EventBus.getDefault().post(new LoginEvent());
    }
}
