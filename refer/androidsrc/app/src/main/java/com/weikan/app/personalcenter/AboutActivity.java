package com.weikan.app.personalcenter;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.common.widget.SimpleNavigationView;
import com.weikan.app.update.UpdateManager;
import com.weikan.app.util.IntentUtils;

/**
 * 设置-关于界面.
 * User: liujian06
 * Date: 2015/3/21
 * Time: 12:53
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private SimpleNavigationView navigation;
    private TextView versionText;
    private int appWrapDataDisplaySwitch;
//    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

//        if(progressDialog==null){
//            progressDialog = new ProgressDialog(AboutActivity.this);
//        }
        navigation = (SimpleNavigationView) findViewById(R.id.navigation);
        navigation.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView titleTextView = navigation.getTitleTextView();
        if (titleTextView != null) {
            titleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appWrapDataDisplaySwitch++;
                    if (appWrapDataDisplaySwitch % 20 == 0) {
                        IntentUtils.to(AboutActivity.this, AppWrapDataActivity.class);
                    }
                }
            });
        }

//        versionText = (TextView) findViewById(R.id.tv_app_version);
//        versionText.setText("当前版本:V" + getVersionName());

        findViewById(R.id.ll_about_check).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UpdateManager updateManager = new UpdateManager(AboutActivity.this, 0);
                        updateManager.checkUpdate();
                    }
                }
        );

        TextView tvDes = (TextView) findViewById(R.id.tv_about_des);
        StringBuilder sb = new StringBuilder();

        sb.append("第一步: 请首先加微信号  kkapp01   (昵称：看看微信小助手)为微信好友；\r\n");
        sb.append("\r\n");
        sb.append("第二步: 看看APP用户登陆后，请点击【关联看看微信小助手】；\n");
        sb.append("\r\n");
        sb.append("第三步: 点击【关联看看微信小助手】后，转发一条消息到微信好友 kkapp01 (昵称：看看微信小助手)，完成关联；\r\n");
        sb.append("\r\n");
        sb.append("以上三步即完成绑定，在微信中发现好的文章、图片、小视频，转发给看看微信小助手，即可实时同步到看看APP，永久保存！\r\n");
        sb.append("\r\n");
        tvDes.setText(sb.toString());
    }

    @Override
    public void onClick(View view) {

    }

    private String getVersionName()
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            // 没找到
        }
        String version = "";
        if(packInfo!=null) {
            version = packInfo.versionName;
        }
        return version;
    }
}
