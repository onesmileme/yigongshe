package com.ygs.android.yigongshe.ui.profile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.ActivityItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.ActivityListResponse;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.SegmentControlView;

import java.util.List;

import butterknife.BindView;
import retrofit2.Response;

public class MeActivitiesActivity extends BaseActivity implements SegmentControlView.OnSegmentChangedListener {

    @BindView(R.id.titlebar_text_title)
    TextView titleView;

    @BindView(R.id.my_mactivity_segment)
    SegmentControlView segmentControlView;

    @BindView(R.id.me_activities_recycleview)
    RecyclerView recyclerView;

    List<ActivityItemBean> mRegisterActivities;
    List<ActivityItemBean> mStoredActivities;
    List<ActivityItemBean> mSignedActivities;

    MeAcitivityAdapter mActivityAdapter;

    protected void initIntent(Bundle bundle){

    }

    protected void initView(){

        titleView.setText(R.string.my_activity);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mActivityAdapter = new MeAcitivityAdapter();
        recyclerView.setAdapter(mActivityAdapter);

        segmentControlView.setOnSegmentChangedListener(this);

    }

    protected  int getLayoutResId(){
        return R.layout.activity_me_activities;
    }


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
        LinkCall<BaseResultDataInfo<ActivityListResponse>>activityCall = LinkCallHelper.getApiService().getMyRegisterActivity(token);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ActivityListResponse>>(){
            @Override
            public void onResponse(BaseResultDataInfo<ActivityListResponse> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatusInterface.OK){
                    mRegisterActivities = entity.data.activities;
                    if (segmentControlView.getSelectedIndex() == 0){
                        mActivityAdapter.setNewData(mRegisterActivities);
                    }
                }
            }
        });

    }

    private void loadStoredActivities(){

        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<ActivityListResponse>> activityCall = LinkCallHelper.getApiService().getMyStoreActivity(token);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ActivityListResponse>>(){
            @Override
            public void onResponse(BaseResultDataInfo<ActivityListResponse> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatusInterface.OK){
                    mStoredActivities = entity.data.activities;
                    if (segmentControlView.getSelectedIndex() == 1){
                        mActivityAdapter.setNewData(mStoredActivities);
                    }
                }
            }
        });

    }

    private void loadSignedActivities(){
        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<ActivityListResponse>> activityCall = LinkCallHelper.getApiService().getMySigninActivity(token);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ActivityListResponse>>(){
            @Override
            public void onResponse(BaseResultDataInfo<ActivityListResponse> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatusInterface.OK){
                    mSignedActivities = entity.data.activities;
                    if (segmentControlView.getSelectedIndex() == 2){
                        mActivityAdapter.setNewData(mSignedActivities);
                    }
                }
            }
        });
    }
}
