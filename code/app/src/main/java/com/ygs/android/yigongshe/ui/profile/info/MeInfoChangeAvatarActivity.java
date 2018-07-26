package com.ygs.android.yigongshe.ui.profile.info;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;

public class MeInfoChangeAvatarActivity extends BaseActivity {

    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;

    @BindView(R.id.avatar_iv)
    ImageView avatarImageView;

    @BindView(R.id.change_avatar_submit_btn)
    Button submitBtn;

    protected void initIntent(Bundle bundle){}


    protected void initView(){

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    finish();
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    protected int getLayoutResId(){
        return R.layout.activity_meinfo_change_avatar;
    }


}
