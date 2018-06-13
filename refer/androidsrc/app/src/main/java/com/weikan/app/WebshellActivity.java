package com.weikan.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.*;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.push.PushManager;
import com.weikan.app.util.BundleParamKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/1/31
 * Time: 11:40
 */
public class WebshellActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mTitleBack;
    private TextView mTitleText;
//    private ImageView mTitleMenu;

    private WebView mWebView;
    private ShellWebViewClient mWebViewClient;
    private ShellWebChromeClient mWebChromeClient;
    private ProgressBar mProgress;
    private String param1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webshell);

        mTitleBack = (ImageView) findViewById(R.id.iv_titlebar_back);
        mTitleText = (TextView) findViewById(R.id.tv_titlebar_title);
//        mTitleMenu = (ImageView) findViewById(R.id.title_menu);

        mTitleBack.setOnClickListener(this);
//        mTitleMenu.setOnClickListener(this);

        initWebView();

        updateData(getIntent().getExtras());


    }

    private void updateData(Bundle bundle) {
        if (bundle != null) {
            String url = bundle.getString(BundleParamKey.URL);
            if (!TextUtils.isEmpty(url)) {
                mWebView.loadUrl(url);
            }
        }
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webshell_webview);
        mProgress = (ProgressBar) findViewById(R.id.webshell_progress);

        mWebView.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);

        mWebViewClient = new ShellWebViewClient();
        mWebChromeClient = new ShellWebChromeClient();

        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
        mWebView.setFocusable(true);
        mWebView.setFocusableInTouchMode(true);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.requestFocusFromTouch();
        mWebView.requestFocus();
        mWebView.setWebChromeClient(mWebChromeClient);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);// 必须要调用这句
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_titlebar_back:
                goback();
                break;
            case R.id.tv_titlebar_title:

                break;
            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        goback();
    }

    private void goback() {
        if (mWebView != null) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        }
    }

    private void showWebView() {
        mWebView.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.GONE);
//        mTipText.setVisibility(View.GONE);
//        mLoadErrImg.setVisibility(View.GONE);
    }

    private void onWebViewError() {
        mWebView.setVisibility(View.GONE);
        mProgress.setVisibility(View.GONE);
//        mTipText.setVisibility(View.GONE);
//        mLoadErrImg.setVisibility(View.VISIBLE);
    }

    final class ShellWebViewClient extends WebViewClient {
        public ShellWebViewClient() {
            super();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Log.d("baidumap", "shouldOverrideUrlLoading url:"+url);
            if (url == null) {
                return false;
            }

            if (url.startsWith("tel") || url.startsWith("mailto")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }

            if(url.startsWith("hunshui://")){
                // 通用协议
                PushManager.getInstance().executePushJump(WebshellActivity.this,url);
                return true;
            }

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            showWebView();
            if (!TextUtils.isEmpty(mWebView.getTitle()) && mTitleText != null) {
                mTitleText.setText(mWebView.getTitle());
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            onWebViewError();
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);

        }
    }

    
    ArrayList<String> photos = new ArrayList<String>();
    
    class ShellWebChromeClient extends WebChromeClient {

        public ShellWebChromeClient() {
            super();
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
//            if (newProgress < 100) {
//                mProgress.setVisibility(View.VISIBLE);
//            } else {
//                mProgress.setVisibility(View.GONE);
//            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            mTitleText.setText(title);
            super.onReceivedTitle(view, title);
        }

    }

    /**
     * 去掉url中的路径，留下请求参数部分
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String TruncateUrlPage(String strURL)
    {
        String strAllParam=null;
        String[] arrSplit=null;

        strURL=strURL.trim();

        arrSplit=strURL.split("[?]");
        if(strURL.length()>1)
        {
            if(arrSplit.length>1)
            {
                if(arrSplit[1]!=null)
                {
                    strAllParam=arrSplit[1];
                }
            }
        }

        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     * @param URL  url地址
     * @return  url请求参数部分
     */
    public static Map<String, String> URLRequest(String URL)
    {
        Map<String, String> mapRequest = new HashMap<String, String>();

        String[] arrSplit=null;

        String strUrlParam=TruncateUrlPage(URL);
        if(strUrlParam==null)
        {
            return mapRequest;
        }
        //每个键值为一组 www.2cto.com
        arrSplit=strUrlParam.split("[&]");
        for(String strSplit:arrSplit)
        {
            String[] arrSplitEqual=null;
            arrSplitEqual= strSplit.split("[=]");

            //解析出键值
            if(arrSplitEqual.length>1)
            {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            }
            else
            {
                if(arrSplitEqual[0]!="")
                {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
