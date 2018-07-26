package com.ygs.android.yigongshe.ui.profile.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.MeInfoItemBean;
import com.ygs.android.yigongshe.bean.UserInfoBean;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.profile.MeSectionDecoration;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import com.ygs.android.yigongshe.view.MyDecoration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

public class MeInfoActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    CommonTitleBar titleBar;

    @BindView(R.id.my_info_recycleview)
    RecyclerView mRecycleView;

    MeInfoAdapter infoAdapter;


    private final int RET_AVATAR = 0;
    private final int RET_NICK = 1;
    private final int RET_PHONE = 2;
    private final int RET_PASSWORD = 3;
    private final int RET_SCHOOL = 4;

    @Override
    protected  void initIntent(Bundle bundle){

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

        List<Integer> showList = new LinkedList<>();
        showList.add(1);
        int color = getResources().getColor(R.color.gray5);
        MyDecoration decoration = new MyDecoration(this,MyDecoration.VERTICAL_LIST,1,color);
        //MeSectionDecoration decoration = new MeSectionDecoration(showList,this);
        //decoration.setHintHight(1);
        mRecycleView.addItemDecoration(decoration);

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        infoAdapter = new MeInfoAdapter();
        mRecycleView.setAdapter(infoAdapter);

        infoAdapter.setNewData(makeData());

        infoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    switch (position){
                        case RET_AVATAR:
                            changeAvatar();
                            break;
                        case 1:
                            changeNickname();
                            break;
                        case 2:
                            changePhone();
                            break;
                        case 3:
                            chnagePassword();
                            break;
                        case 4:
                            changeSchool();
                            break;
                            default:
                                break;
                    }
            }
        });

    }

    @Override
    protected  int getLayoutResId(){
        return R.layout.activity_me_info;
    }


    private void changeAvatar(){

        Intent intent = new Intent(this,MeInfoChangeAvatarActivity.class);
        AccountManager accountManager = YGApplication.accountManager;
        UserInfoBean userInfoBean = accountManager.getUserInfoBean();
        intent.putExtra("avatar",userInfoBean.avatar);
        startActivityForResult(intent,RET_AVATAR);

    }
    private void changeNickname(){

        Intent intent = new Intent(this,MeInfoChangeNickNameActivity.class);
        this.startActivityForResult(intent,RET_NICK);

    }
    private void changePhone(){

        Intent intent = new Intent(this,MeInfoChangePhoneActivity.class);

        AccountManager accountManager = YGApplication.accountManager;
        UserInfoBean userInfoBean = accountManager.getUserInfoBean();
        intent.putExtra("phone",userInfoBean.phone);
        startActivityForResult(intent,RET_PHONE);

    }

    private void chnagePassword(){

        Intent intent = new Intent(this,MeInfoChangePasswordActivity.class);
        startActivityForResult(intent,RET_PASSWORD);
    }
    private void changeSchool(){

        Intent intent = new Intent(this,MeInfoChangeSchoolActivity.class);
        startActivityForResult(intent,RET_SCHOOL);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case RET_AVATAR:{
                if (resultCode == 1){
                    if (data != null){
                        String avatar = data.getStringExtra("avatar");
                        if (avatar != null){
                            MeInfoItemBean itemBean = infoAdapter.getItem(RET_AVATAR);
                            itemBean.imgUrl = avatar;
                            infoAdapter.setData(RET_AVATAR,itemBean);
                        }
                    }
                }
                break;
            }
            case RET_NICK:{
                if (resultCode == 1){
                    if (data != null) {
                        String name = data.getStringExtra("name");
                        if (name != null) {
                            MeInfoItemBean itemBean = infoAdapter.getItem(RET_NICK);
                            itemBean.value = name;
                            infoAdapter.setData(RET_NICK, itemBean);
                        }
                    }
                }
                break;
            }
            case RET_PHONE:{
                if (resultCode == 1){
                    if (data != null) {
                        String name = data.getStringExtra("phone");
                        if (name != null) {
                            MeInfoItemBean itemBean = infoAdapter.getItem(RET_PHONE);
                            itemBean.value = name;
                            infoAdapter.setData(RET_PHONE, itemBean);
                        }
                    }
                }
                break;
            }
            case RET_PASSWORD:{
                if (resultCode == 1){
                    Toast.makeText(this,"修改密码成功",Toast.LENGTH_SHORT);
                }
                break;
            }
            case RET_SCHOOL:{
                if (resultCode == 1){
                    if (data != null) {
                        String name = data.getStringExtra("school");
                        if (name != null) {
                            MeInfoItemBean itemBean = infoAdapter.getItem(RET_SCHOOL);
                            itemBean.value = name;
                            infoAdapter.setData(RET_SCHOOL, itemBean);
                        }
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    private List<MeInfoItemBean> makeData(){

        AccountManager accountManager = YGApplication.accountManager;
        UserInfoBean userInfoBean = accountManager.getUserInfoBean();

        List<MeInfoItemBean> itemBeans = new ArrayList<>(5);
        //avatar
        MeInfoItemBean itemBean = new MeInfoItemBean();
        itemBean.name = "头像";
        itemBean.imgUrl = userInfoBean.avatar;

        itemBeans.add(itemBean);

        //nickname
        itemBean = new MeInfoItemBean();
        itemBean.name = "昵称";
        itemBean.value = userInfoBean.username;
        itemBeans.add(itemBean);

        //手机号
        itemBean = new MeInfoItemBean();
        itemBean.name = "手机号";
        itemBean.value = userInfoBean.phone;
        itemBeans.add(itemBean);

        //密码
        itemBean = new MeInfoItemBean();
        itemBean.name = "密码";
        itemBean.value = "修改";
        itemBeans.add(itemBean);

        //学校资料
        itemBean = new MeInfoItemBean();
        itemBean.name = "学校资料";
        itemBean.value = userInfoBean.school;
        itemBeans.add(itemBean);

        return itemBeans;
    }
}
