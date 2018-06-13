package com.weikan.app.live;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weikan.app.R;
import com.weikan.app.ShareActivity;
import com.weikan.app.account.AccountManager;
import com.weikan.app.account.bean.LoginEvent;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.live.bean.LiveListObject;
import com.weikan.app.util.AppUtils;
import com.weikan.app.util.LToast;
import com.weikan.app.util.PayUtil;
import com.weikan.app.util.SchemaUtil;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wxapi.WechatPaymentEvent;

import java.util.Map;

import de.greenrobot.event.EventBus;
import platform.http.responsehandler.AmbJsonResponseHandler;
import platform.http.result.FailedResult;

/**
 * 直播 - 播放页面
 * @author kailun on 16/8/17.
 */
public class LivePlayActivity extends BaseActivity {

    private static final String BUNDLE_URL = "bundle_url";
    private static final String BUNDLE_OBJ = "bundle_obj";

    public static final String SCHEMA = "appfac";

    private WebView webView;

    private String url = "";
    private LiveListObject liveData;
    private String userCallBack;
    private BroadcastReceiver netReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if(info != null && info.isAvailable()) {
                    String name = info.getTypeName();
                    if(webView!=null){
                        webView.loadUrl("javascript:reloadPage()");
                    }
                }
            }
        }
    };

    public static Intent makeIntent(Context context, LiveListObject obj) {
        Intent intent = new Intent(context, LivePlayNativeActivity.class);
        intent.putExtra(BUNDLE_OBJ, obj);
        return intent;
    }

    private void parseIntent() {
        liveData = (LiveListObject) getIntent().getSerializableExtra(BUNDLE_OBJ);
        if(liveData == null){
            final String liveid = getIntent().getStringExtra("liveid");
            if(TextUtils.isEmpty(liveid)){
                initReceiver();
                LToast.showToast("系统异常，请稍后重试");
                return;
            }
            try {
                LiveAgent.getLiveNativeDetail(Long.parseLong(liveid), new AmbJsonResponseHandler<LiveListObject>() {


                    @Override
                    public void success(@Nullable LiveListObject data) {
                        liveData = data;
                        initViews();
                        loadPage();
                        initReceiver();
                    }

                    @Override
                    protected void failed(FailedResult r) {
                        super.failed(r);
                    }
                });
            }catch(Exception e){

            }
        }else{
            initViews();
            loadPage();
            initReceiver();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_play);
        parseIntent();
    }

    @Override
    protected boolean openTranslucentStatus() {
        return false;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        EventBus.getDefault().register(LivePlayActivity.this);
        webView = (WebView) findViewById(R.id.web_view);
        WebSettings settings = webView.getSettings();

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setLoadsImagesAutomatically(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        settings.setAppCacheEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setAllowFileAccess(true);

        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String databasePath = this.getApplicationContext()
                    .getDir("database", Context.MODE_PRIVATE)
                    .getPath();
            settings.setDatabasePath(databasePath);
        }
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
//                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (webView != null) {
                    webView.loadUrl("javascript:onPageFinished()");
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (webView != null) {
                    webView.loadUrl("about:blank");
                }
                LToast.showToast("请求失败，请检查网络并重试。");
                finish();
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                if(!TextUtils.isEmpty(message) && message.startsWith(SCHEMA)){
                    String path = SchemaUtil.parsePath(message);
                    Map<String,String> kvs = SchemaUtil.urlRequest(message);
                    if(!TextUtils.isEmpty(path)){
                        if(path.equals("close")){
                            finish();
                        } else if(path.equals("share")){
                                String title;
                                String content;
                                String shareurl;
                                String picurl;
                                if (liveData.share != null) {
                                    title = liveData.share.title;
                                    content = liveData.share.desc;
                                    shareurl = liveData.share.url;
                                    picurl = liveData.share.icon != null ? liveData.share.icon : "";
                                    if (TextUtils.isEmpty(title)) {
                                        title = "分享来自刘文静的直播";
                                    }
                                    if (TextUtils.isEmpty(content)) {
//                                    content = "来"+getString(R.string.app_name)+"看直播吧";
                                        content = "";
                                    }
                                    new ShareActivity.Builder()
                                            .context(LivePlayActivity.this)
                                            .title(title)
                                            .shareWenyouType(ShareActivity.SHARE_WENYOU_FOR_LIVE)
                                            .username(liveData.author)
                                            .content(content)
                                            .url(TextUtils.isEmpty(shareurl) ? URLDefine.SHARE_URL : shareurl)
                                            .imgurl(picurl)
                                            .liveId((int)liveData.liveId)
                                            .buildAndShow();
                                }
                        } else if(path.equals("get_user_info")){
                             userCallBack = kvs.get("callback");
                            if (!TextUtils.isEmpty(userCallBack)) {
                                String uid = "";
                                if(AccountManager.getInstance().isLogin()){
                                    uid = AccountManager.getInstance().getUserId();
                                }

                                String js = String.format("javascript:%s({\"uid\": \"%s\"})", userCallBack, uid);
                                webView.loadUrl(js);
                            }
                        }else if(path.equals("pay")){
                            try {
                                int amount = Integer.parseInt(kvs.get("value"));
                                showLoadingDialog();
                                PayUtil.getInstance().pay(amount,LivePlayActivity.this);
                            }catch (Exception e){
                                LToast.showToast("系统异常，请稍后重试");
                            }
                        } else if(path.equals("goto_login")){
                            AccountManager.getInstance().gotoLogin(LivePlayActivity.this);
                        } else if(path.equals("get_video_info")){
                            String callback = kvs.get("callback");
                            if (!TextUtils.isEmpty(callback)) {
                                String json = JSONObject.toJSONString(liveData);
                                String js = "javascript:" + callback + "(" + json + ")";
                                webView.loadUrl(js);
                            }
                        } else if(path.equals("get_app_info")){
                            String callback = kvs.get("callback");
                            if (!TextUtils.isEmpty(callback)) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("app_key", AppUtils.getAppkey());
                                String json = jsonObject.toJSONString();
                                String js = "javascript:" + callback + "(" + json + ")";
                                webView.loadUrl(js);
                            }
                        }
                    }
                }
                result.confirm("");
                return true;
            }
        });
    }

    private void loadPage() {
        url = liveData.h5Url;

        if (AccountManager.getInstance().isLogin()) {
            url += "&uid=" + AccountManager.getInstance().getUserId();
        }

        webView.loadUrl(url);
    }

    private void initReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.netReceiver, intentFilter);
    }

    private void unregisterReceiver(){
        unregisterReceiver(netReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView = null;
        }
        unregisterReceiver();
        EventBus.getDefault().unregister(this);
    }
    public void onEventMainThread(WechatPaymentEvent event) {
           String  js = "javascript:pay("+ JSON.toJSONString(event)+")";
           webView.loadUrl(js);
          loadPage();

    }

    public void onEventMainThread(LoginEvent event) {
        if (!TextUtils.isEmpty(userCallBack)) {
            String uid = "";
            if(AccountManager.getInstance().isLogin()){
                uid = AccountManager.getInstance().getUserId();
            }
            String userJs = String.format("javascript:%s({\"uid\": \"%s\"})", userCallBack, uid);
            webView.loadUrl(userJs);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            String  js = "javascript:returnClose()";
            webView.loadUrl(js);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
