package com.ygs.android.yigongshe.ui.profile.info;

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

    @BindView(R.id.titleBar)
    CommonTitleBar titleBar;

    @BindView(R.id.change_nickname_et)
    EditText editText;

    @BindView(R.id.change_nickname_btn)
    Button submitButton;

    protected void initIntent(Bundle bundle){}

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

    protected int getLayoutResId(){return R.layout.activity_meinfo_change_nickname;}

    private void submit(){

        final String nickname = editText.getText().toString();
        if (nickname.length() == 0){
            Toast.makeText(this,"请输入昵称",Toast.LENGTH_SHORT);
            return;
        }
        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<UserInfoBean>>call = LinkCallHelper.getApiService().modifyUsername(token,nickname);
        call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UserInfoBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<UserInfoBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);

                if (entity.error == ApiStatusInterface.OK){
                    Toast.makeText(MeInfoChangeNickNameActivity.this,"修改昵称成功",Toast.LENGTH_SHORT);
                    YGApplication.accountManager.updateUserName(nickname);
                }else{
                    Toast.makeText(MeInfoChangeNickNameActivity.this,entity.msg,Toast.LENGTH_SHORT);
                }

            }

        });
    }

}
