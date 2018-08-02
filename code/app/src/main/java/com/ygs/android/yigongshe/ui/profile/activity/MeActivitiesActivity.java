package com.ygs.android.yigongshe.ui.profile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.ActivityItemBean;
import com.ygs.android.yigongshe.bean.MyActivityBean;
import com.ygs.android.yigongshe.bean.ShareBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.ActivityListResponse;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.activity.ActivityDetailActivity;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import com.ygs.android.yigongshe.view.MyDividerItemDecoration;
import com.ygs.android.yigongshe.view.SegmentControlView;

import java.util.List;

import butterknife.BindView;
import retrofit2.Response;

public class MeActivitiesActivity extends BaseActivity implements SegmentControlView.OnSegmentChangedListener {

    @BindView(R.id.titlebar) CommonTitleBar titleBar;

    @BindView(R.id.my_mactivity_segment) SegmentControlView segmentControlView;

    @BindView(R.id.me_activities_recycleview) RecyclerView recyclerView;

    @BindView(R.id.swipeLayout) SwipeRefreshLayout swipeRefreshLayout;

    List<ActivityItemBean> mRegisterActivities;
    List<ActivityItemBean> mStoredActivities;
    List<ActivityItemBean> mSignedActivities;

    MeAcitivityAdapter mActivityAdapter;

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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
            new MyDividerItemDecoration(this, MyDividerItemDecoration.VERTICAL));

        mActivityAdapter = new MeAcitivityAdapter();
        recyclerView.setAdapter(mActivityAdapter);

        mActivityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ActivityItemBean itemBean;
                switch (segmentControlView.getSelectedIndex()){
                    case 0:{
                        itemBean = mRegisterActivities.get(position);
                        break;
                    }
                    case 1:{
                        itemBean = mStoredActivities.get(position);
                        break;
                    }
                    case 2:{
                        itemBean = mSignedActivities.get(position);
                        break;
                    }
                    default:
                        return;
                }
                if (itemBean != null){
                    Bundle bundle = new Bundle();
                    bundle.putInt("activity_id", itemBean.activityid);
                    bundle.putString("activity_title", itemBean.title);
                    ShareBean shareBean = new ShareBean(itemBean.title, itemBean.desc, itemBean.link);
                    bundle.putSerializable("shareBean", shareBean);
                    goToOthers(ActivityDetailActivity.class, bundle);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (segmentControlView.getSelectedIndex()){
                    case 0:{
                        loadRestisterAcitivities();
                        break;
                    }
                    case 1:{
                        loadSignedActivities();
                        break;
                    }
                    case 2:{
                        loadSignedActivities();
                        break;
                    }
                    default:
                        break;
                }
            }
        });

        segmentControlView.setOnSegmentChangedListener(this);

        onSegmentChanged(0);

    }

    @Override
    protected  int getLayoutResId(){
        return R.layout.activity_me_activities;
    }


    @Override
    public  void onSegmentChanged(int newSelectedIndex){

        switch (newSelectedIndex){
            case 0: {
                if (mRegisterActivities == null) {
                    loadRestisterAcitivities();
                }
                mActivityAdapter.setNewData(mRegisterActivities);
            }
                break;
            case 1:{
                if (mStoredActivities == null){
                    loadStoredActivities();
                }
                mActivityAdapter.setNewData(mStoredActivities);
            }
            break;
            case 2:{
                if (mSignedActivities == null){
                    loadSignedActivities();
                }
                mActivityAdapter.setNewData(mSignedActivities);
            }
            break;
            default:
                break;
        }

    }

    public void loadRestisterAcitivities(){

        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<MyActivityBean>>activityCall = LinkCallHelper.getApiService().getMyRegisterActivity(token);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MyActivityBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<MyActivityBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatusInterface.OK){
                    mRegisterActivities = entity.data.activities;
                    if (segmentControlView == null ||  segmentControlView.getSelectedIndex() == 0){
                        mActivityAdapter.setNewData(mRegisterActivities);
                    }
                }else {
                    String msg = "加载失败";
                    if (entity != null){
                        msg += "("+entity.msg+")";
                    }
                    Toast.makeText(MeActivitiesActivity.this,msg,Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void loadStoredActivities(){

        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<MyActivityBean>> activityCall = LinkCallHelper.getApiService().getMyStoreActivity(token);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MyActivityBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<MyActivityBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatusInterface.OK){
                    mStoredActivities = entity.data.activities;
                    if (segmentControlView.getSelectedIndex() == 1){
                        mActivityAdapter.setNewData(mStoredActivities);
                    }
                }else {
                    String msg = "加载失败";
                    if (entity != null){
                        msg += "("+entity.msg+")";
                    }
                    Toast.makeText(MeActivitiesActivity.this,msg,Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void loadSignedActivities(){
        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<MyActivityBean>> activityCall = LinkCallHelper.getApiService().getMySigninActivity(token);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MyActivityBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<MyActivityBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatusInterface.OK){
                    mSignedActivities = entity.data.activities;
                    if (segmentControlView.getSelectedIndex() == 2){
                        mActivityAdapter.setNewData(mSignedActivities);
                    }
                }else {
                    String msg = "加载失败";
                    if (entity != null){
                        msg += "("+entity.msg+")";
                    }
                    Toast.makeText(MeActivitiesActivity.this,msg,Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
