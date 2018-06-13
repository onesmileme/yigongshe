package com.weikan.app;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.TextView;

import com.umeng.analytics.AnalyticsConfig;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.common.net.CommonAgent;
import com.weikan.app.news.NewsAgent;
import com.weikan.app.news.bean.CategoryListData;
import com.weikan.app.news.utils.CategoryManager;
import com.weikan.app.util.AppUtils;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.IntentUtils;
import com.weikan.app.util.PrefDefine;
import com.weikan.app.util.SharePrefsUtils;

import java.lang.ref.WeakReference;

import platform.http.responsehandler.JsonResponseHandler;

public class SplashActivity extends BaseActivity {

    private MyHandler handler;
    private long requestStartTime; // 请求分类列表配置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 使用自己的渠道打包方案，不再在manifest中配置渠道号
        AnalyticsConfig.setChannel(AppUtils.getChannelName());
        if (checkNotificationIntent()) {
            return;
        }

        // 解决android 系统bug，按home在重新点icon启动，会在一个栈里多次调起主页的问题
        if (!isTaskRoot()) {
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);
        setSwipeBackEnable(false);
        handler = new MyHandler(this);
        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText("V " + AppUtils.getAppVersionName());

        CommonAgent.pushBind();

        // 获取app的所有配置
        CommonAgent.getAppConfig();

        AccountManager.getInstance().init(this);

    }

    @Override
    public void onResume() {
//        JPushInterface.onResume(this);//极光统计
        super.onResume();
        getCategoryListConfig();

//        handler.postDelayed(delayRun, 1000);
//        delayRun.run();
    }

    @Override
    public void onPause() {
//        JPushInterface.onPause(this);//极光统计
        super.onPause();
//        HttpUtils.cancelAllRequest();
        handler.removeCallbacks(delayRun);
    }

    private void gotoLogin(){
//        AccountManager.getInstance().logout(LanjingApplication.getInstance().getApplicationContext());
//        AccountManager.getInstance().gotoLogin(LanjingApplication.getInstance().getApplicationContext());
    }

    Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            SharePrefsUtils sharePrefsUtils = new SharePrefsUtils(SplashActivity.this, PrefDefine.FIRSTLOGIN_STRING);
            boolean isFirstLogin = sharePrefsUtils.getBoolean("is_first_login", true);
            if (isFirstLogin) {
                // 首次登录，跳转至新手引导页
//                Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
//                overridePendingTransition(R.anim.push_left_out, R.anim.push_right_in);
//                startActivity(intent);
//                finish();
                IntentUtils.to(SplashActivity.this, MainActivity.class);
                finish();
            } else {
//                IntentUtils.to(SplashActivity.this, NewLiveActivity.class);
//                finish();
                boolean isLogin = AccountManager.getInstance().isLogin();
                String session = AccountManager.getInstance().getSession();
                if (!isLogin || TextUtils.isEmpty(session)) {
                    // 未登录，跳转至登录页
//                    Log.d("debug", "not logged in");
//                    Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
//                    startActivity(intent);
                    IntentUtils.to(SplashActivity.this, MainActivity.class);
                    finish();
                } else {
                    // 已登录，进入主页
                    IntentUtils.to(SplashActivity.this, MainActivity.class);
                    finish();
//                    // 已登录，获取用户信息
//                    Uri.Builder builder = new Uri.Builder();
//                    builder.scheme(URLDefine.SCHEME);
//                    builder.encodedAuthority(URLDefine.HUN_WATER_HOST_ACCOUNT);
//                    builder.encodedPath(URLDefine.USERINFO_USER_INFO);
//
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
//                    params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
//                    HttpUtil.newMutipartPost(builder.build().toString(), new HashMap<String, String>(), params, new HashMap<String, File>(), new TextHttpResponseHandler() {
//                        //HttpUtils.get(builder.build().toString(), params, new TextHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                            UserInfoObject object = null;
//                            try {
//                                object = JSONObject.parseObject(responseString, UserInfoObject.class);
//                            } catch (com.alibaba.fastjson.JSONException ex) {
//                                ex.printStackTrace();
//                            }
//                            if (object == null) {
//                                LToast.showDebugToast("获取用户信息失败。");
//                                gotoLogin();
//                                return;
//                            }
//                            if (object.errno != 0) {
//                                ErrorResponseHandler.handleError(object.errno);
//                                gotoLogin();
//                                return;
//                            }
//                            if (object.data == null) {
//                                Log.d("debug", "object.data is null");
//                                gotoLogin();
//                                return;
//                            }
//                            //if (object.data.content.is_complete == 0) {
//                            //    IntentUtils.to(SplashActivity.this, AccountModifyUserinfActivity.class);
//                            //    finish();
//                            //    return;
//                            //}
//                            AccountManager.getInstance().setUserData(object.data);
//                            IntentUtils.to(SplashActivity.this, NewLiveActivity.class);
//                            finish();
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                            gotoLogin();
//                        }
//                    });
                }
            }
        }
    };

    private static class MyHandler extends Handler {
        private WeakReference<SplashActivity> mActivity;

        private MyHandler(SplashActivity activity) {
            mActivity = new WeakReference<>(activity);
        }
    }

    /**
     * 推送过来的通知，点击后就这儿来处理
     *
     * @return 返回false，则页面走正常流程；返回true，则不再显示Splash
     */
    private boolean checkNotificationIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return false;
        }

        if (!bundle.containsKey(BundleParamKey.NOTIFICATION_INTENT_TARGET)) {
            return false;
        }

//        AccountLoginActivity accountActivity = (AccountLoginActivity) AppManager.findActivity(AccountLoginActivity.class); //判断是否是登录界面要是不SplashActivity不显示
//        if (accountActivity != null) {
//            finish();
//            return true; // SplashActivity不再显示
//        }

        // 把bundle记下来，以便于MainActivity来处理
        MainActivity.setNotificationBundle(bundle);

        // 判断MainActivity是不是在
        // 如果在的话，直接交给MainActivity来处理
        MainActivity main = (MainActivity) AppManager.findActivity(MainActivity.class);

        if (main != null) {
            main.handleNotificationBundle();
            finish();
            return true; // SplashActivity不再显示
        }

        return false;
    }

    private void getCategoryListConfig() {
        // 记录请求开始的时间
        requestStartTime = System.currentTimeMillis();

        NewsAgent.getCategoryListConfig(new JsonResponseHandler<CategoryListData>() {
            @Override
            public void success(@NonNull CategoryListData data) {
                CategoryManager.getInstance().saveCategoryListData(
                        SplashActivity.this, data.categoryList);
            }

            @Override
            public void end() {
                long requestEndTime = System.currentTimeMillis();
                long timeSpan = requestEndTime - requestStartTime;
                if (timeSpan >= 1000) {
                    delayRun.run(); // 直接下一步了
                } else {
                    handler.postDelayed(delayRun, 1000 - timeSpan);
                }
            }
        });
    }
}
