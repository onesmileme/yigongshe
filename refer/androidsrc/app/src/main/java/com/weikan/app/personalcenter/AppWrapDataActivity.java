package com.weikan.app.personalcenter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.weikan.app.BuildConfig;
import com.weikan.app.R;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.push.PushManager;

/**
 * Created by shijicheng on 2015/8/22.
 */
public class AppWrapDataActivity extends BaseActivity {
    private TextView tv_Title;
    private TextView tv_AppWrapData;
    private StringBuffer appWrapDataText = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_wrap_data);
        tv_Title = (TextView)findViewById(R.id.tv_titlebar_title);
        tv_Title.setText("应用设备信息");
        findViewById(R.id.iv_titlebar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_AppWrapData = (TextView)findViewById(R.id.tv_app_wrap_data);

        appWrapDataText.append("应用信息\n");
        appWrapDataText.append("\n版本:\t"+ BuildConfig.BUILD_TYPE);
        appWrapDataText.append("\n渠道号:\t" + BuildConfig.FLAVOR);
        appWrapDataText.append("\n版本号:\tV" + BuildConfig.VERSION_NAME);
        appWrapDataText.append("\nVersionCode:\t" + BuildConfig.VERSION_CODE);
        appWrapDataText.append("\n发布时间:\t"+ BuildConfig.RELEASE_TIME);
//        appWrapDataText.append("\nHOST_APP:\t" + URLDefine.HOST_APP);
//        appWrapDataText.append("\nHOST_API:\t" + URLDefine.HOST_API);
//        appWrapDataText.append("\nHOST_API2:\t" + URLDefine.HOST_API2);
        appWrapDataText.append("\nPush token:\t" + PushManager.getInstance().getToken(this));

        tv_AppWrapData.setText(appWrapDataText.toString());
    }
}
