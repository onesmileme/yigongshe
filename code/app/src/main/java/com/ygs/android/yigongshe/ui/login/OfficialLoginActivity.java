package com.ygs.android.yigongshe.ui.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import com.ygs.android.yigongshe.MainActivity;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.LoginBean;
import com.ygs.android.yigongshe.bean.UserInfoBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.push.PushManager;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import retrofit2.Response;

public class OfficialLoginActivity extends BaseActivity implements View.OnClickListener {

  @BindView(R.id.login_login_btn) Button mLoginButton;

  @BindView(R.id.login_official_login_btn) Button mOfficialLoginButton;

  @BindView(R.id.login_forget_password_btn) Button mForgetButton;

  @BindView(R.id.login_phone_et) EditText mPhoneEditText;

  @BindView(R.id.login_password_et) EditText mPasswordEditText;

  //@BindView(R.id.titlebar) CommonTitleBar titleBar;
  @BindView(R.id.login_register_btn) Button mRegisterButton;

  private AccountManager accountManager = YGApplication.accountManager;

  private LinkCall<BaseResultDataInfo<LoginBean>> mLoginCall;

  @Override protected void initIntent(Bundle bundle) {
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!TextUtils.isEmpty(accountManager.getToken())) {
      goToOthers(MainActivity.class, null);
      OfficialLoginActivity.this.finish();
    }
  }

  @Override protected void initView() {

    //titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
    //  @Override public void onClicked(View v, int action, String extra) {
    //    if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
    //      finish();
    //    } else if (action == CommonTitleBar.ACTION_RIGHT_TEXT
    //        || action == CommonTitleBar.ACTION_RIGHT_BUTTON) {
    //      doRegister();
    //    }
    //  }
    //});

    mLoginButton.setOnClickListener(this);
    mOfficialLoginButton.setOnClickListener(this);
    mForgetButton.setOnClickListener(this);
    mRegisterButton.setOnClickListener(this);

  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_official_login;
  }

  @Override public void onClick(View view) {

    if (view == mLoginButton) {
      tryLogin();
    } else if (view == mOfficialLoginButton) {
      tryOfficialLogin();
    } else if (view == mForgetButton) {
      forgetPassword();
    } else if (view == mRegisterButton) {
      showRegisterAlert();
    }
  }

  private void tryLogin() {

    if (mPhoneEditText.getText().length() == 0) {
      Toast.makeText(this, "请输入用户名", Toast.LENGTH_LONG).show();
      return;
    } else if (mPasswordEditText.getText().length() == 0) {
      Toast.makeText(this, "请输入密码", Toast.LENGTH_LONG).show();
      return;
    }else if (mPasswordEditText.getText().length() < 6){
        Toast.makeText(this, "密码至少6位", Toast.LENGTH_LONG).show();
    }

    loginAction();
  }

  private void tryOfficialLogin() {
      Intent intent = new Intent(this,LoginActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
  }

  private void forgetPassword() {

    Intent intent = new Intent(this, ResetPasswdActivity.class);
    startActivity(intent);
  }

  private void loginAction() {
    showDialog();

    String phone = mPhoneEditText.getText().toString();
    String password = mPasswordEditText.getText().toString();

    if (phone.length() == 0) {
      Toast.makeText(this, "请输入用户名", Toast.LENGTH_LONG).show();
    } else if (password.length() == 0) {
      Toast.makeText(this, "请输入密码", Toast.LENGTH_LONG).show();
    }

    String token = PushManager.getInstance().getToken(this);
    if (token == null) {
      token = "";
    }
    mLoginCall = LinkCallHelper.getApiService().doLogin(phone, password, token);
    mLoginCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<LoginBean>>() {

      @Override public void onResponse(BaseResultDataInfo<LoginBean> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == ApiStatus.OK) {
          dismissDialog();
          Log.e("LOGIN", "onResponse: login data is" + entity.msg + " " + entity.data.token);
          if (accountManager != null) {
            accountManager.updateToken(entity.data.token, entity.data.token_expire);
            accountManager.updateUserId(entity.data.userid);
            Intent intent = new Intent();
            intent.setAction("com.ygs.android.yigongshe.login");
            intent.putExtra("token", entity.data.token);
            LocalBroadcastManager broadcastManager =
                LocalBroadcastManager.getInstance(OfficialLoginActivity.this);
            broadcastManager.sendBroadcast(intent);
          }
          goToOthers(MainActivity.class, null);
          OfficialLoginActivity.this.finish();
        } else {
          dismissDialog();
          String msg = "登录失败";
          if (entity != null && entity.msg != null) {
            msg += "(" + entity.msg + ")";
          }
          Toast.makeText(OfficialLoginActivity.this, msg, Toast.LENGTH_LONG).show();
        }
      }
    });
  }

  AlertDialog dialog;

  private void showDialog() {
    dialog = new AlertDialog.Builder(this).setMessage("登录中...").setCancelable(false).show();
  }

  private void dismissDialog() {
    if (dialog != null) {
      dialog.dismiss();
    }
  }

  private void showRegisterAlert() {

    new AlertDialog.Builder(this)
        .setMessage("放弃正在注册的信息，前往登录页面？")
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        })
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            doRegister();
          }
        }).show();

  }

  private void doRegister() {

    Intent intent = new Intent(this, RegisterActivity.class);
    startActivity(intent);
  }

  private void loadUserInfo(final String token) {

    LinkCall<BaseResultDataInfo<UserInfoBean>> userInfoCall =
        LinkCallHelper.getApiService().getUserInfo(token);
    userInfoCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UserInfoBean>>() {
      @Override
      public void onResponse(BaseResultDataInfo<UserInfoBean> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity.error == ApiStatus.OK) {
          AccountManager accountManager = YGApplication.accountManager;
          if (accountManager != null) {
            accountManager.updateUserInfo(entity.data);
          }
        }
      }
    });
  }
}
