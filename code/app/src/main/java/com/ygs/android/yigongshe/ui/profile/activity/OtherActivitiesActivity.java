package com.ygs.android.yigongshe.ui.profile.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.ActivityItemBean;
import com.ygs.android.yigongshe.bean.MyActivityBean;
import com.ygs.android.yigongshe.bean.ShareBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.activity.ActivityDetailActivity;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CDividerItemDecoration;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import com.ygs.android.yigongshe.view.MyDividerItemDecoration;
import com.ygs.android.yigongshe.view.SegmentControlView;

import java.util.List;

import retrofit2.Response;

public class OtherActivitiesActivity extends BaseActivity implements SegmentControlView.OnSegmentChangedListener {

    public static final String ACTIVITY_TYPE_REGISTER = "register"; //报名的活动
    public static final String ACTIVITY_TYPE_LIKE = "like";        //点赞的活动
    public static final String ACTIVITY_TYPE_STORE = "store";      //我收藏的活动
    public static final String ACTIVITY_TYPE_SIGNIN = "signin";    //我签到的活动

    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;

    @BindView(R.id.other_activities_recycleview)
    RecyclerView recyclerView;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.other_activity_segment)
    SegmentControlView segmentControlView;

    @BindView(R.id.other_no_acitvity_tv)
    TextView noActivityTextView;

    List<ActivityItemBean> mActivities;

    MeAcitivityAdapter mActivityAdapter;

    private List<ActivityItemBean> mRegisterActivities;
    private List<ActivityItemBean> mStoredActivities;
    private List<ActivityItemBean> mSignedActivities;

    private int mRegisterPage;
    private int mStoredPage;
    private int mSignedPage;

    private String otherUid;

    @Override
    protected void initIntent(Bundle bundle) {

        otherUid = bundle.getString("otherUid");
        if (TextUtils.isEmpty(otherUid)) {
            Toast.makeText(this, "未获得用户ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void initView() {

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    finish();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CDividerItemDecoration itemDecoration = new CDividerItemDecoration(this,
            CDividerItemDecoration.VERTICAL_LIST, new ColorDrawable(Color.parseColor("#e0e0e0")));
        itemDecoration.setHeight(1);
        recyclerView.addItemDecoration(itemDecoration);

        mActivityAdapter = new MeAcitivityAdapter();
        recyclerView.setAdapter(mActivityAdapter);
        mActivityAdapter.bindToRecyclerView(recyclerView);

        mActivityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ActivityItemBean itemBean;
                switch (segmentControlView.getSelectedIndex()) {
                    case 0: {
                        itemBean = mRegisterActivities.get(position);
                        break;
                    }
                    case 1: {
                        itemBean = mStoredActivities.get(position);
                        break;
                    }
                    case 2: {
                        itemBean = mSignedActivities.get(position);
                        break;
                    }
                    default:
                        return;
                }
                if (itemBean != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("activity_id", itemBean.activityid);
                    bundle.putString("activity_title", itemBean.title);
                    ShareBean shareBean = new ShareBean(itemBean.title, itemBean.desc, itemBean.share_url);
                    bundle.putSerializable("shareBean", shareBean);
                    goToOthers(ActivityDetailActivity.class, bundle);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true);
            }
        });

        segmentControlView.setOnSegmentChangedListener(this);

        onSegmentChanged(0);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_other_activities;
    }

    @Override
    public void onSegmentChanged(int newSelectedIndex) {

        noActivityTextView.setVisibility(View.GONE);
        switch (newSelectedIndex) {
            case 0: {
                if (mRegisterActivities == null) {
                    loadRestisterAcitivities(true);
                }
                mActivityAdapter.setNewData(mRegisterActivities);
            }
            break;
            case 1: {
                if (mStoredActivities == null) {
                    loadStoredActivities(true);
                }
                mActivityAdapter.setNewData(mStoredActivities);
            }
            break;
            case 2: {
                if (mSignedActivities == null) {
                    loadSignedActivities(true);
                }
                mActivityAdapter.setNewData(mSignedActivities);
            }
            break;
            default:
                break;
        }
        try {
            mActivityAdapter.disableLoadMoreIfNotFullPage();
        }catch (Exception e){
            Log.e("OTHER", "onSegmentChanged: "+Log.getStackTraceString(e) );
        }
    }

    private void loadData(boolean isRefresh) {

        noActivityTextView.setVisibility(View.GONE);
        switch (segmentControlView.getSelectedIndex()) {
            case 0: {
                loadRestisterAcitivities(isRefresh);
                break;
            }
            case 1: {
                loadSignedActivities(isRefresh);
                break;
            }
            case 2: {
                loadSignedActivities(isRefresh);
                break;
            }
            default:
                break;
        }
    }

    public void loadRestisterAcitivities(final boolean isRefresh) {

        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<MyActivityBean>> activityCall = LinkCallHelper.getApiService()
            .getOtherActivity(token, otherUid, ACTIVITY_TYPE_SIGNIN);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MyActivityBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<MyActivityBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK) {
                    if (entity.data.activities.size() > 0) {
                        mRegisterActivities = entity.data.activities;
                        if (segmentControlView == null || segmentControlView.getSelectedIndex() == 0) {
                            mActivityAdapter.setNewData(mRegisterActivities);
                        }
                        noActivityTextView.setVisibility(View.GONE);
                    }else{
                        noActivityTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    String msg = "加载失败";
                    if (entity != null) {
                        msg += "(" + entity.msg + ")";
                    }
                    Toast.makeText(OtherActivitiesActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
                mActivityAdapter.disableLoadMoreIfNotFullPage();
            }
        });

    }

    private void loadStoredActivities(final boolean isRefresh) {

        String token = YGApplication.accountManager.getToken();
        //final int pageIndex = isRefresh? 0: mStoredPage+1;
        LinkCall<BaseResultDataInfo<MyActivityBean>> activityCall = LinkCallHelper.getApiService().getOtherActivity(
            token, otherUid, ACTIVITY_TYPE_STORE);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MyActivityBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<MyActivityBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK) {
                    if (entity.data.activities.size() > 0) {
                        mStoredActivities = entity.data.activities;
                        if (segmentControlView.getSelectedIndex() == 1) {
                            mActivityAdapter.setNewData(mStoredActivities);
                        }
                    }
                } else {
                    String msg = "加载失败";
                    if (entity != null) {
                        msg += "(" + entity.msg + ")";
                    }
                    Toast.makeText(OtherActivitiesActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
                mActivityAdapter.disableLoadMoreIfNotFullPage();
            }
        });

    }

    private void loadSignedActivities(final boolean isRefresh) {
        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<MyActivityBean>> activityCall = LinkCallHelper.getApiService().getOtherActivity(
            token, otherUid, ACTIVITY_TYPE_SIGNIN);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MyActivityBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<MyActivityBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK) {
                    if (entity.data.activities.size() > 0) {
                        mSignedActivities = entity.data.activities;
                        if (segmentControlView.getSelectedIndex() == 2) {
                            mActivityAdapter.setNewData(mSignedActivities);
                        }
                    }
                } else {
                    String msg = "加载失败";
                    if (entity != null) {
                        msg += "(" + entity.msg + ")";
                    }
                    Toast.makeText(OtherActivitiesActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
                mActivityAdapter.disableLoadMoreIfNotFullPage();
            }
        });
    }

    public void loadAcitivities(String type) {

        String token = YGApplication.accountManager.getToken();

        LinkCall<BaseResultDataInfo<MyActivityBean>> activityCall =
            LinkCallHelper.getApiService().getOtherActivity(token, otherUid, type);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MyActivityBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<MyActivityBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK) {
                    mActivities = entity.data.activities;
                    mActivityAdapter.setNewData(mActivities);
                } else {
                    String msg = "加载失败";
                    if (entity != null) {
                        msg += "(" + entity.msg + ")";
                    }
                    Toast.makeText(OtherActivitiesActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
