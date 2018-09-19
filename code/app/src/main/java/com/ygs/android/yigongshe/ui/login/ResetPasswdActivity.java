package com.ygs.android.yigongshe.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import retrofit2.Response;

public class ResetPasswdActivity extends BaseActivity {

  @BindView(R.id.titlebar) CommonTitleBar titleBar;

  @BindView(R.id.phone_et) EditText mPhoneEditText;

  @BindView(R.id.password_captcha_et) EditText mCaptchaEditText;

  @BindView(R.id.new_password_et) EditText mPasswordEditText;

  @BindView(R.id.send_captcha_btn) Button mSendButton;

  @BindView(R.id.change_password_btn) Button mSubmitButton;

  private CountDownTimer countDownTimer;

  private String phoneNum;

  @Override protected void initIntent(Bundle bundle) {
  }

  @Override protected void initView() {

    titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
          finish();
        }
      }
    });
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_reset_passwd;
  }

  private void startCountdown() {

    if (countDownTimer == null) {
      countDownTimer = new CountDownTimer(60 * 1000, 1000) {
        @Override public void onTick(long millisUntilFinished) {
          refreshCountdownTv(millisUntilFinished / 1000);
        }

        @Override public void onFinish() {
          refreshCountdownTv(0);
        }
      };
    }
    countDownTimer.cancel();
    countDownTimer.start();
  }

  @OnClick({ R.id.send_captcha_btn, R.id.change_password_btn }) public void onClick(View v) {
    if (v == mSendButton) {
      sendCaptcha();
    } else {
      submit();
    }
  }

  private void refreshCountdownTv(long secondsLeft) {
    if (mSendButton == null) {
      return;
    }
    if (secondsLeft > 0) {
      mSendButton.setText(secondsLeft + "s后再次发送");
      int color = getResources().getColor(R.color.gray2);
      mSendButton.setBackgroundColor(color);
      mSendButton.setEnabled(false);
    } else {
      mSendButton.setText("点击发送验证码");
      int color = getResources().getColor(R.color.green);
      mSendButton.setBackgroundColor(color);
      mSendButton.setEnabled(true);
    }
  }

  private void updateTip() {

    String tip = "验证码已发至" + phoneNum.substring(0, 3) + "****" + phoneNum.substring(7) + "，请注意查收";
    Toast.makeText(ResetPasswdActivity.this, tip, Toast.LENGTH_SHORT).show();
  }

  private void sendCaptcha() {

    phoneNum = mPhoneEditText.getText().toString();
    if (phoneNum.length() == 0 || phoneNum.length() != 11) {
      Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
      return;
    }

    LinkCall<BaseResultDataInfo<EmptyBean>> call =
        LinkCallHelper.getApiService().sendVerifycode(phoneNum);
    call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>() {
      @Override public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null) {
          if (entity.error == ApiStatus.OK) {
            startCountdown();
            updateTip();
          } else {
            Toast.makeText(ResetPasswdActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
          }
        }
      }
    });
  }

  private void submit() {

    String password = mPasswordEditText.getText().toString();
    String captcha = mCaptchaEditText.getText().toString();
    String tip = null;
    if (captcha.length() == 0) {
      tip = "请输入验证码";
    } else if (password.length() == 0) {
      tip = "请输入密码";
    }
    if (tip != null) {
      Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
      return;
    }

    LinkCall<BaseResultDataInfo<EmptyBean>> call =
        LinkCallHelper.getApiService().forgetpassword(captcha, phoneNum, password);
    call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>() {
      @Override public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity.error == ApiStatus.OK) {
          //finish();
          Toast.makeText(ResetPasswdActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
          showLogin();
        } else {
          Toast.makeText(ResetPasswdActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private void showLogin() {

    Intent intent = new Intent(this, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
  }
}
