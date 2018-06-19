package com.ygs.android.yigongshe.ui.login;

import android.app.ActionBar;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

import butterknife.BindView;

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

    protected void initIntent(){

    }

    protected void initView() {


        mLoginButton.setOnClickListener(this);
        mOfficialLoginButton.setOnClickListener(this);
        mForgetButton.setOnClickListener(this);


        this.mNavRightButton.setText(R.string.register);
        this.mNavRightButton.setVisibility(View.VISIBLE);
        this.mNavRightButton.setOnClickListener(this);
    }

    protected  int getLayoutResId()
    {
        return R.layout.activity_login;
    }

    @Override
    public void onClick(View view){

        if (view == mLoginButton){
            tryLogin();
        }else if (view == mOfficialLoginButton){
            tryOfficialLogin();
        }else if(view == mForgetButton){
            forgetPassword();
        }else if(view == mNavRightButton){
            //do register
            doRegister();
        }
    }

    private void tryLogin(){

    }

    private  void tryOfficialLogin(){

    }

    private void forgetPassword(){

    }

    private void registerActoin(){

    }

    private void doRegister(){

        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);

    }


}
