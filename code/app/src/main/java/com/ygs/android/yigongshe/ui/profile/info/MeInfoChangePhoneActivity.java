package com.ygs.android.yigongshe.ui.profile.info;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.UserInfoBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;
import retrofit2.Response;

public class MeInfoChangePhoneActivity extends BaseActivity {

    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;

    @BindView(R.id.change_phone_btn)
    Button submitButton;

    @BindView(R.id.countdown_tv)
    TextView countdownTv;

    @BindView(R.id.phone_et)
    EditText phoneEt;

    @BindView(R.id.input_captcha_et)
    EditText captchaEt;

    @BindView(R.id.phone_tip_tv)
    TextView tipTextView;

    CountDownTimer countDownTimer;

    String phoneNum;

    protected void initIntent(Bundle bundle){

        phoneNum = bundle.getString("phone");

    }


    protected void initView(){

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    finish();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        countdownTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCaptcha();
            }
        });

        refreshCountdownTv(0);

    }

    private void startCountdown(){

        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                   refreshCountdownTv(millisUntilFinished/1000);
                }

                @Override
                public void onFinish() {
                    refreshCountdownTv(0);
                }
            };
        }
        countDownTimer.cancel();
        countDownTimer.start();
    }

    private void refreshCountdownTv(long secondsLeft){
        if (secondsLeft > 0){
            countdownTv.setText(secondsLeft+"s后再次发送");
            int color = getResources().getColor(R.color.gray2);
            countdownTv.setBackgroundColor(color);
            countdownTv.setEnabled(false);
        }else{
            countdownTv.setText("点击发送验证码");
            int color = getResources().getColor(R.color.green);
            countdownTv.setBackgroundColor(color);
            countdownTv.setEnabled(true);
        }
    }

    protected int getLayoutResId(){
        return R.layout.activity_meinfo_chane_phone;
    }


    private void updateTip(){
        String tip = "验证码已发至"+phoneNum.substring(0,3)+"****"+phoneNum.substring(7);
        tipTextView.setText(tip);
    }

    private void submit(){

        final String phone = phoneEt.getText().toString();
        String captcha = captchaEt.getText().toString();

        String tip = null;
        if (phone.length() == 0){
            tip = "请输入手机号";
        }else if(captcha.length() == 0){
            tip = "请输入验证码";
        }
        if (tip != null){
            Toast.makeText(this,tip,Toast.LENGTH_SHORT);
            return;
        }
        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<UserInfoBean>>call = LinkCallHelper.getApiService().modifyPhone(token,phone,captcha);
        call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UserInfoBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<UserInfoBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity.error == ApiStatusInterface.OK){
                    YGApplication.accountManager.updatePhone(phone);
                }else {
                    Toast.makeText(MeInfoChangePhoneActivity.this,entity.msg,Toast.LENGTH_SHORT);
                    return;
                }
            }
        });

    }

    private void sendCaptcha(){

        LinkCall<BaseResultDataInfo<EmptyBean>> call = LinkCallHelper.getApiService().sendVerifycode(phoneNum);
        call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity.error == ApiStatusInterface.OK){
                    startCountdown();
                    updateTip();
                }else{
                    Toast.makeText(MeInfoChangePhoneActivity.this,entity.msg,Toast.LENGTH_SHORT);
                }
            }
        });
    }

}
