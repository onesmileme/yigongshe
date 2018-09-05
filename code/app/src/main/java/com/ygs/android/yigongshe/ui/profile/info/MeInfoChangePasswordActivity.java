package com.ygs.android.yigongshe.ui.profile.info;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.utils.ZProgressHUD;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;
import retrofit2.Response;

public class MeInfoChangePasswordActivity extends BaseActivity {

    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;

    @BindView(R.id.change_password_et)
    EditText mPasswordEditText;

    @BindView(R.id.re_password_et)
    EditText mReinputPasswordEditText;

    @BindView(R.id.change_password_btn)
    Button submitButton;

    @Override
    protected void initIntent(Bundle bundle){

    }

    @Override
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

    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_meinfo_change_password;
    }

    private void submit(){

        String password = mPasswordEditText.getText().toString();
        String repassword = mReinputPasswordEditText.getText().toString();

        String tip = null;
        if (password.length() == 0){
            tip = "请输入密码";
        }else if(!password.equals(repassword)){
            tip = "两次密码不一致";
        }
        if (tip != null){
            Toast.makeText(this,tip,Toast.LENGTH_SHORT).show();
            return;
        }

        final ZProgressHUD hud = ZProgressHUD.getInstance(this);
        hud.setMessage("");
        hud.show();
        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<EmptyBean>>call = LinkCallHelper.getApiService().modifyPassword(token,password,repassword);
        call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity.error == ApiStatus.OK){
                    setResult(1,null);
                    hud.dismissWithSuccess("密码修改成功");
                    //Toast.makeText(MeInfoChangePasswordActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    hud.dismissWithFailure(entity.msg);
                    //Toast.makeText(MeInfoChangePasswordActivity.this,entity.msg,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
