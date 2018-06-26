package com.ygs.android.yigongshe.ui.login;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.LoginBean;
import com.ygs.android.yigongshe.bean.UserInfoBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

import butterknife.BindView;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.login_login_btn)
    Button mLoginButton;

    @BindView(R.id.login_official_login_btn)
    Button mOfficialLoginButton;

    @BindView(R.id.login_forget_password_btn)
    Button mForgetButton;

    @BindView(R.id.login_phone_et)
    EditText mPhoneEditText;

    @BindView(R.id.login_password_et)
    EditText mPasswordEditText;

    @BindView(R.id.titlebar_backward_btn)
    Button mNavBackButton;

    @BindView(R.id.titlebar_right_btn)
    Button mNavRightButton;

    private LinkCall<BaseResultDataInfo<LoginBean>> mLoginCall;

    protected void initIntent() {

    }

    protected void initView() {

        mNavBackButton.setOnClickListener(this);
        mNavRightButton.setVisibility(View.VISIBLE);

        this.mNavRightButton.setText(R.string.register);
        this.mNavRightButton.setVisibility(View.VISIBLE);
        this.mNavRightButton.setOnClickListener(this);

        mLoginButton.setOnClickListener(this);
        mOfficialLoginButton.setOnClickListener(this);
        mForgetButton.setOnClickListener(this);

        setTranslucentStatus(true);


        //for test
        mPhoneEditText.setText("18993368867");
        mPasswordEditText.setText("admin");
    }

    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    public void onClick(View view) {

        if (view == mLoginButton) {
            tryLogin();
        } else if (view == mOfficialLoginButton) {
            tryOfficialLogin();
        } else if (view == mForgetButton) {
            forgetPassword();
        } else if (view == mNavRightButton) {
            //do register
            doRegister();
        } else if (view == mNavBackButton) {
            finish();
        }
    }

    private void tryLogin() {

        if (mPhoneEditText.getText().length() == 0) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG);
            return;
        } else if (mPasswordEditText.getText().length() == 0) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_LONG);
            return;
        }
        loginAction();

    }

    private void tryOfficialLogin() {

    }

    private void forgetPassword() {

    }

    private void loginAction() {

        String phone = mPhoneEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (phone.length() == 0) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG);
        } else if (password.length() == 0) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_LONG);
        }


        mLoginCall = LinkCallHelper.getApiService().doLogin(phone, password);
        mLoginCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<LoginBean>>() {

            @Override
            public void onResponse(BaseResultDataInfo<LoginBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity.error == ApiStatusInterface.OK) {
                    Log.e("LOGIN", "onResponse: login data is"+entity.msg+" "+entity.data.token );
                    AccountManager accountManager = YGApplication.accountManager;
                    if (accountManager != null) {
                        accountManager.updateToken(entity.data.token, entity.data.token_expire);
//                        LoginActivity.this.loadUserInfo(entity.data.token);
                        Intent intent = new Intent();
                        intent.setAction("com.ygs.android.yigongshe.login");
                        intent.putExtra("token", entity.data.token);
                        LoginActivity.this.sendBroadcast(intent);
                    }
                    LoginActivity.this.finish();
                } else {
                    Toast.makeText(LoginActivity.this, entity.msg, Toast.LENGTH_LONG);
                }
            }
        });

    }

    private void doRegister() {

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }


    private void loadUserInfo(final String token) {

        LinkCall<BaseResultDataInfo<UserInfoBean>> userInfoCall = LinkCallHelper.getApiService().getUserInfo(token);
        userInfoCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UserInfoBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<UserInfoBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity.error == ApiStatusInterface.OK) {
                    AccountManager accountManager = YGApplication.accountManager;
                    if (accountManager != null) {
                        accountManager.updateUserInfo(entity.data);
                    }

                }

            }
        });

    }

}
