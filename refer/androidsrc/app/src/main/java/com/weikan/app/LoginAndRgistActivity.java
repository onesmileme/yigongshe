package com.weikan.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.weikan.app.account.bean.LoginEvent;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.live.bean.PayEvent;
import com.weikan.app.personalcenter.MineLoginFragment;
import com.weikan.app.personalcenter.MineLoginWithPhoneFragment;

import de.greenrobot.event.EventBus;

/**
 * Created by ylp on 2016/12/7.
 */

public class LoginAndRgistActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_login_regist);
        findViewById(R.id.iv_titlebar_back).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_titlebar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_titlebar_title)).setText("登录");
        Fragment fragment;
        if(BuildConfig.IS_PHONE_LOGIN_SUPPORT){
            fragment = new MineLoginWithPhoneFragment();
            ((MineLoginWithPhoneFragment)fragment).setTitleShow(false);
        } else {
            fragment = new MineLoginFragment();
            ((MineLoginFragment)fragment).setTitleShow(false);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commitAllowingStateLoss();
    }
    public void onEventMainThread(LoginEvent event) {
        finish();
    }
}
