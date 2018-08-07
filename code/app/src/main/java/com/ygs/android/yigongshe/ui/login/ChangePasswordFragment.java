package com.ygs.android.yigongshe.ui.login;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;
import retrofit2.Response;

public class ChangePasswordFragment extends BaseFragment {


    @BindView(R.id.titlebar) CommonTitleBar titleBar;

    @BindView(R.id.change_password_et)
    EditText mPasswordEditText;

    @BindView(R.id.re_password_et)
    EditText mReinputPasswordEditText;

    @BindView(R.id.change_password_btn)
    Button submitButton;

    String phone;
    String captcha;

    SwitcherListener switcherListener;

    @Override
    protected void initView(){

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    switcherListener.goBack(ChangePasswordFragment.this);
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
    public int getLayoutResId(){
        return R.layout.fragment_change_password;
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
            Toast.makeText(this.getActivity(),tip,Toast.LENGTH_SHORT);
            return;
        }

        LinkCall<BaseResultDataInfo<EmptyBean>> call = LinkCallHelper.getApiService().resetPassword(captcha,phone,password,repassword);
        call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity.error == ApiStatus.OK){
                    //setResult(1,null);
                    //finish();
                }else {
                    Toast.makeText(ChangePasswordFragment.this.getActivity(),entity.msg,Toast.LENGTH_SHORT);
                }
            }
        });

    }
}
