package com.ygs.android.yigongshe.ui.login;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.RelativeLayout;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import butterknife.BindView;
import retrofit2.Response;

public class ResetPasswordActivity extends BaseActivity implements SwitcherListener{

    @BindView(R.id.reset_password_layout) RelativeLayout mRelativeLayout;

    private InputPhoneFragment inputPhoneFragment;
    private InputCaptchaFragment inputCaptchaFragment;
    private ChangePasswordFragment changePasswordFragment;

    static final String PHONE_TAG = "input_phone";
    static final String CAPTCHA_TAG = "input_captcha";
    static final String PASSWORD_TAG = "change_password";

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private String phone;
    private String captcha;


    @Override
    protected void initIntent(Bundle bundle){

    }

    @Override
    protected void initView(){

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        inputPhoneFragment  = new InputPhoneFragment();
        inputCaptchaFragment = new InputCaptchaFragment();
        changePasswordFragment = new ChangePasswordFragment();

        inputPhoneFragment.switcherListener = this;
        changePasswordFragment.switcherListener = this;
        inputCaptchaFragment.switcherListener = this;

        fragmentTransaction.add(R.id.reset_password_layout,inputPhoneFragment,PHONE_TAG);
        fragmentTransaction.add(R.id.reset_password_layout,inputCaptchaFragment,CAPTCHA_TAG);
        fragmentTransaction.add(R.id.reset_password_layout,changePasswordFragment,PASSWORD_TAG);

        fragmentTransaction.replace(R.id.reset_password_layout,inputPhoneFragment,PHONE_TAG).commit();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_reset_password;
    }

    @Override
    public void goBack(BaseFragment fragment){

        BaseFragment nextFragment = null;
        String tag = null;

        if (fragment instanceof  ChangePasswordFragment){
            nextFragment = inputCaptchaFragment;
            tag = CAPTCHA_TAG;
        }else if(fragment instanceof InputCaptchaFragment){
            nextFragment = inputPhoneFragment;
            tag = PHONE_TAG;
        }else if(fragment instanceof InputPhoneFragment){
            finish();
            return;
        }
        if (nextFragment != null){
            fragmentManager.beginTransaction().replace(R.id.reset_password_layout,nextFragment,tag).commit();
        }

    }

    @Override
    public void goNex(BaseFragment fragment){

        BaseFragment nextFragment = null;
        String tag = null;
        if (fragment instanceof  InputPhoneFragment){
            InputPhoneFragment inputPhoneFragment1 = (InputPhoneFragment)fragment;
            phone = inputPhoneFragment1.getPhone();
            inputCaptchaFragment.updatPhone(phone);
            nextFragment = inputCaptchaFragment;
            tag = CAPTCHA_TAG;
        }else if(fragment instanceof  InputCaptchaFragment){
            InputCaptchaFragment inputCaptchaFragment1 = (InputCaptchaFragment)fragment;
            captcha = inputCaptchaFragment1.getCaptcha();
            changePasswordFragment.phone = phone;
            changePasswordFragment.captcha = captcha;
            nextFragment = changePasswordFragment;
            tag = PASSWORD_TAG;
        }
        if (nextFragment != null){
            fragmentManager.beginTransaction().replace(R.id.reset_password_layout,nextFragment,tag).commit();
        }
    }
}
