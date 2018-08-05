package com.ygs.android.yigongshe.ui.otherhomepage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.OtherUserInfoBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.profile.activity.OtherActivitiesActivity;
import com.ygs.android.yigongshe.ui.profile.community.OtherCommunityActivity;
import com.ygs.android.yigongshe.ui.profile.focus.MeFocusActivity;
import com.ygs.android.yigongshe.ui.profile.message.MsgTalkActivity;
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

    private LinkCall<BaseResultDataInfo<OtherUserInfoBean>> mCall;
    private OtherUserInfoBean userInfoBean;

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
                    userInfoBean = entity.data;
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

    private void doFollow(){
        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<EmptyBean>> followCall = LinkCallHelper.getApiService().doFollow(token,userId);
        followCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatusInterface.OK){
                    userInfoBean.is_followed = "1";
                    updateUI(userInfoBean);
                }else{
                    String msg = "关注失败";
                    if (entity != null && entity.msg != null){
                        msg += "("+entity.msg+")";
                    }
                    Toast.makeText(OtherHomePageActivity.this,msg,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    @Override
    public void onClick(View view){

        if (view == activityView){

            Bundle bundle = new Bundle();
            bundle.putString("otherUid",userId);
            goToOthers(OtherActivitiesActivity.class,bundle);

        }else if(view == circleView){

            Bundle bundle = new Bundle();
            bundle.putString("otherUid",userId);
            goToOthers(OtherCommunityActivity.class,bundle);

        }else if(view == followBtn){
            doFollow();
        }else if(view == sendBtn){

            Bundle bundle = new Bundle();
            bundle.putString("otherUid",userId);
            bundle.putString("type","message");
            bundle.putString("name",userInfoBean.username);
            goToOthers(MsgTalkActivity.class,bundle);
        }

    }
}
