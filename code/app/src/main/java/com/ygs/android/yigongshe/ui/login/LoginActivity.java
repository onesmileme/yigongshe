package com.ygs.android.yigongshe.ui.login;

import android.app.ActionBar;
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


    protected void initIntent(){

    }

    protected void initView() {


        mLoginButton.setOnClickListener(this);
        mOfficialLoginButton.setOnClickListener(this);
        mForgetButton.setOnClickListener(this);

        ActionBar actionBar = getActionBar();
        actionBar.show();



        actionBar.setCustomView(R.layout.view_register_button);
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

}
