package com.ygs.android.yigongshe.ui.profile.info;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.MeInfoItemBean;
import com.ygs.android.yigongshe.bean.UserInfoBean;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.profile.MeSectionDecoration;
import com.ygs.android.yigongshe.view.CommonTitleBar;

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

    protected  void initIntent(Bundle bundle){

    }

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
        MeSectionDecoration decoration = new MeSectionDecoration(showList,this);
        decoration.setHintHight(1);
        mRecycleView.addItemDecoration(decoration);

        infoAdapter = new MeInfoAdapter();
        mRecycleView.setAdapter(infoAdapter);

        infoAdapter.setNewData(makeData());

        infoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });

    }

    protected  int getLayoutResId(){
        return R.layout.activity_me_info;
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
