package com.ygs.android.yigongshe.webview;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import butterknife.BindView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebViewActivity extends BaseActivity {

  public static final String TITLE_KEY = "title";

  public static final String LOCAL_HTML_PATH = "html_path";

  public static final String URL_KEY = "url_key";

  @BindView(R.id.titleBar) CommonTitleBar titleBar;

  @BindView(R.id.webview) WebView mWebView;

  @Override protected void initIntent(Bundle bundle) {

    String title = bundle.getString(TITLE_KEY);
    titleBar.getCenterTextView().setText(title);

    String htmlPath = bundle.getString(LOCAL_HTML_PATH);
    if (htmlPath != null) {
      try {
        InputStream inputStream = getResources().getAssets().open(htmlPath);

        InputStreamReader reader = new InputStreamReader(inputStream);

        BufferedReader bufferedReader = new BufferedReader(reader);

        StringBuilder sb = new StringBuilder();
        String line = null;
        do {
          line = bufferedReader.readLine();
          if (line != null) {
            sb.append(line);
          }
        } while (line != null);
        bufferedReader.close();

        mWebView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
      } catch (IOException ioe) {
        ioe.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      String url = bundle.getString(URL_KEY);
      if (!TextUtils.isEmpty(url)) {
        mWebView.loadUrl(url);
      }
    }
  }

  @Override protected void initView() {

    titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
          finish();
        }
      }
    });
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_webiew;
  }
}
