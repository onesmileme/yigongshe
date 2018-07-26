package com.ygs.android.yigongshe.ui.profile.info;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.actionsheet.ActionSheet;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.utils.ImageLoadUtil;
import com.ygs.android.yigongshe.view.CircleImageView;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;

public class MeInfoChangeAvatarActivity extends BaseActivity implements ActionSheet.MenuItemClickListener {

    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;

    @BindView(R.id.avatar_iv)
    CircleImageView avatarImageView;

    @BindView(R.id.change_avatar_submit_btn)
    Button submitBtn;

    private String avatar;

    @Override
    protected void initIntent(Bundle bundle){

        if (bundle != null){
            avatar = bundle.getString("avatar");
        }
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

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        avatarImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                chooseAvatar();
            }
        });

        if (avatar != null){
            ImageLoadUtil.loadImage(avatarImageView,avatar);
        }

    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_meinfo_change_avatar;
    }


    private void chooseAvatar(){

        ActionSheet menuView = new ActionSheet(this);
        menuView.setCancelButtonTitle("取消");// before add items
        menuView.addItems("拍照", "从相册中选择");
        menuView.setItemClickListener(this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();

    }
    @Override
    public void onItemClick(int itemPosition)
    {
        Toast.makeText(this, (itemPosition + 1) + " click", Toast.LENGTH_SHORT).show();
    }

}
