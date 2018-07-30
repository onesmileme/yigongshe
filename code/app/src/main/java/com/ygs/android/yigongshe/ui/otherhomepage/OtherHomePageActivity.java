package com.ygs.android.yigongshe.ui.otherhomepage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.OtherUserInfoBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.utils.ImageLoadUtil;
import com.ygs.android.yigongshe.view.CircleImageView;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Response;

public class OtherHomePageActivity extends BaseActivity implements View.OnClickListener{


    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;

    @BindView(R.id.avatar_iv)
    CircleImageView avatarImageView;

    @BindView(R.id.name_tv)
    TextView nameTextView;

    @BindView(R.id.phone_tv)
    TextView phoneTextView;

    @BindView(R.id.activity_layout)
    View activityView;

    @BindView(R.id.circle_layout)
    View circleView;


    @BindView(R.id.send_msg_btn)
    Button sendBtn;

    @BindView(R.id.follow_btn)
    Button followBtn;

    private String userId;

    LinkCall<BaseResultDataInfo<OtherUserInfoBean>> mCall;

    @Override
    protected void initIntent(Bundle bundle){

        this.userId = bundle.getString("userid");
    }


    @Override
    protected  void initView(){

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    finish();
                }
            }
        });


        activityView.setOnClickListener(this);
        circleView.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        followBtn.setOnClickListener(this);

        loadUserInfo();

    }

    @Override
    protected  int getLayoutResId(){
        return R.layout.activity_other_homepage;
    }

    private void loadUserInfo(){

        String token = YGApplication.accountManager.getToken();

        mCall = LinkCallHelper.getApiService().getOtherInfo(token,userId);
        mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<OtherUserInfoBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<OtherUserInfoBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatusInterface.OK){
                    updateUI(entity.data);
                }else{
                    String msg = "请求用户信息失败";
                    if (entity != null ){
                        msg += "("+entity.msg+")";
                    }
                    Toast.makeText(OtherHomePageActivity.this,msg,Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void updateUI(OtherUserInfoBean otherUserInfoBean){

        ImageLoadUtil.loadImage(avatarImageView,otherUserInfoBean.avatar);
        nameTextView.setText(otherUserInfoBean.username);
        phoneTextView.setText(otherUserInfoBean.phone);

        Boolean followed = "1".equals(otherUserInfoBean.is_followed);
        followBtn.setText(followed?R.string.followed:R.string.follow);
        followBtn.setEnabled(!followed);

    }

    @Override
    public void onClick(View view){

    }
}
