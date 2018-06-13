package com.weikan.app.live;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.listener.OnNoRepeatClickListener;
import com.weikan.app.live.bean.NewLiveObject;
import com.weikan.app.util.AppUtils;
import com.weikan.app.util.LToast;
import com.weikan.app.util.PermissionUtil;

import platform.http.responsehandler.JsonResponseHandler;

/**
 * 新建直播推流的界面
 */
public class NewLiveActivity extends BaseActivity {

    private EditText etTitle;
    private Button btnStartLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_live);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initViews();
    }

    private void initViews() {
        etTitle = (EditText) findViewById(R.id.et_title);
        btnStartLive = (Button) findViewById(R.id.btn_start_live);
        btnStartLive.setOnClickListener(new OnNoRepeatClickListener() {
            @Override
            public void onNoRepeatClick(View v) {
                requestPostNew();
            }
        });

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void requestPostNew() {
        if (!PermissionUtil.isCameraPermission()) {
            LToast.showToast("请开启相机权限再重试");
            return;
        }
        if (!PermissionUtil.isVoicePermission()) {
            LToast.showToast("请开启麦克风权限再重试");
            return;
        }
        String uid = AccountManager.getInstance().getUserId();
        String title = etTitle.getText().toString();

        LiveAgent.postNew(uid, title, new JsonResponseHandler<NewLiveObject>() {
            @Override
            public void begin() {
                super.begin();
                showLoadingDialog();
            }

            @Override
            public void success(@NonNull NewLiveObject data) {
                Intent intent = LiveRecordActivity.makeIntent(NewLiveActivity.this,
                        data.liveId, data.liveInfo);
                startActivity(intent);
                finish();
            }

            @Override
            public void end() {
                super.end();
                hideLoadingDialog();
            }
        });
    }
}
