package com.weikan.app.personalcenter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.weikan.app.R;
import com.weikan.app.base.BaseActivity;

/**
 * Created by zhaorenhui on 2015/12/17.
 */
public class ResetPwdActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        initTitleBar();


    }

    private void initTitleBar() {
        TextView titleText = (TextView) findViewById(R.id.tv_titlebar_title);
        titleText.setText("修改密码");
        findViewById(R.id.iv_titlebar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
