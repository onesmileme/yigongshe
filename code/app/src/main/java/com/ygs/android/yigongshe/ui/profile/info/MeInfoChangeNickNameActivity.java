package com.ygs.android.yigongshe.ui.profile.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
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

public class MeInfoChangeNickNameActivity extends BaseActivity {

    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;

    @BindView(R.id.change_nickname_et)
    EditText editText;

    @BindView(R.id.change_nickname_btn)
    Button submitButton;


    LinkCall<BaseResultDataInfo<UserInfoBean>> mCall;

    @Override
    protected void initIntent(Bundle bundle){}

    @Override
    protected void initView(){

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if(action == CommonTitleBar.ACTION_LEFT_BUTTON){
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
    protected int getLayoutResId(){return R.layout.activity_meinfo_change_nickname;}

    @Override
    protected void onStop() {
        super.onStop();
        if (mCall != null && !mCall.isCanceled()){
            mCall.cancel();
        }
    }

    private void submit(){

        final String nickname = editText.getText().toString();
        if (nickname.length() == 0){
            Toast.makeText(this,"请输入昵称",Toast.LENGTH_SHORT).show();
            return;
        }
        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<UserInfoBean>>call = LinkCallHelper.getApiService().modifyUsername(token,nickname);
        call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UserInfoBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<UserInfoBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);

                if (entity.error == ApiStatusInterface.OK){
                    YGApplication.accountManager.updateUserName(nickname);
                    Intent intent = new Intent();
                    intent.putExtra("name",nickname);
                    setResult(1,intent);
                    finish();
                }else{
                    Toast.makeText(MeInfoChangeNickNameActivity.this,entity.msg,Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

}
