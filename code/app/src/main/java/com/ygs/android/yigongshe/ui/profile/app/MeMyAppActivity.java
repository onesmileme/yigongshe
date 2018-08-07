package com.ygs.android.yigongshe.ui.profile.app;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MyAppItemBean;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.profile.run.MeRunActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

public class MeMyAppActivity extends BaseActivity {

    @BindView(R.id.myapp_recycleview)
    RecyclerView mRecycleView;

    @BindView(R.id.titleBar)
    CommonTitleBar titleBar;

    MeMyAppActivityAdapter meMyAppActivityAdapter;

    List<MyAppItemBean> apps ;

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

        meMyAppActivityAdapter = new MeMyAppActivityAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setAdapter(meMyAppActivityAdapter);

        meMyAppActivityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position){
                    case 0:{
                        goToOthers(MeRunActivity.class,null);
                        break;
                    }
                    default:
                        break;
                }
            }
        });

        initApps();
    }

    private void initApps(){
        apps = new LinkedList<>();
        MyAppItemBean itemBean = new MyAppItemBean();
        itemBean.name = "益行走";
        apps.add(itemBean);

        meMyAppActivityAdapter.setNewData(apps);
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_me_myapp;
    }


}
