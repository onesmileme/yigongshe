package com.ygs.android.yigongshe.ui.login;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;
import retrofit2.Response;

public class InputCaptchaFragment extends BaseFragment implements View.OnClickListener{

    @BindView(R.id.titlebar) CommonTitleBar titleBar;
    @BindView(R.id.phone_tip_tv) TextView mTipTextView;
    @BindView(R.id.captcha_et) EditText mCaptchaEditText;
    @BindView(R.id.send_captcha_btn) Button mSendButton;
    @BindView(R.id.next_btn) Button mNextButton;

    private CountDownTimer countDownTimer;
    private String phoneNum;

    SwitcherListener switcherListener;

    @Override
    protected void initView(){

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    switcherListener.goBack(InputCaptchaFragment.this);
                }
            }
        });

        mNextButton.setOnClickListener(this);
        mSendButton.setOnClickListener(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public int getLayoutResId(){
        return R.layout.fragment_send_captcha;
    }

    @Override
    public void onClick(View v) {

        if (v == mSendButton){
            sendCaptcha();
        }else if (v == mNextButton){
            if (switcherListener != null){
                switcherListener.goNex(this);
            }
        }

    }

    public void updatPhone(String phoneNum){
        this.phoneNum = phoneNum;
    }

    public String getCaptcha(){
        return mCaptchaEditText.getText().toString();
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
        if (mSendButton == null){
            return;
        }
        if (secondsLeft > 0){
            mSendButton.setText(secondsLeft+"s后再次发送");
            int color = getResources().getColor(R.color.gray2);
            mSendButton.setBackgroundColor(color);
            mSendButton.setEnabled(false);
        }else{
            mSendButton.setText("点击发送验证码");
            int color = getResources().getColor(R.color.green);
            mSendButton.setBackgroundColor(color);
            mSendButton.setEnabled(true);
        }
    }

    private void updateTip(){
        String tip = "验证码已发至"+phoneNum.substring(0,3)+"****"+phoneNum.substring(7)+"，请注意查收";
        mTipTextView.setText(tip);
    }

    private void sendCaptcha(){

        //phoneNum = mCaptchaEditText.getText().toString();
        //if (phoneNum.length() == 0 || phoneNum.length() != 11){
        //    Toast.makeText(this.getActivity(),"请输入正确的手机号",Toast.LENGTH_LONG).show();
        //    return;
        //}

        LinkCall<BaseResultDataInfo<EmptyBean>> call = LinkCallHelper.getApiService().sendVerifycode(phoneNum);
        call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity.error == ApiStatusInterface.OK){
                    startCountdown();
                    updateTip();
                }else{
                    Toast.makeText(InputCaptchaFragment.this.getActivity(),entity.msg,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
