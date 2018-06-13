package com.weikan.app.personalcenter;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alibaba.fastjson.annotation.JSONField;
import com.weikan.app.R;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.util.URLDefine;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;

import java.util.Map;

/**
 * Created by Lee on 2016/12/6.
 */
public class MineDealActivity extends BaseActivity {
    @Bind(R.id.tv_deal_content)
    TextView tvDealContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_deal);
        ButterKnife.bind(this);
        ((TextView) findViewById(R.id.tv_titlebar_title)).setText("用户使用协议");

        sendRequest();
    }

    private void sendRequest() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.CONFIG_PROTO);

        Map<String, String> params = new ArrayMap<>();
        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<DealObject>() {

            @Override
            public void success(@NonNull DealObject data) {
                if (tvDealContent != null) {
                    String appName = getApplicationName();
                    tvDealContent.setMovementMethod(new ScrollingMovementMethod());
                    tvDealContent.setText(data.content.replaceAll("_XXXX_", appName));
                }
            }
        });
    }

    private static class DealObject{
        @JSONField(name="content")
        public String content;
    }

    @OnClick(R.id.iv_titlebar_back)
    public void back() {
        finish();
    }

    public String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
