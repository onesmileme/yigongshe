package com.ygs.android.yigongshe.ui.profile.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ygs.android.yigongshe.view.SegmentControlView;

import java.util.List;

import butterknife.BindView;
import retrofit2.Response;

public class MeActivitiesActivity extends BaseActivity implements SegmentControlView.OnSegmentChangedListener {

    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;

    @BindView(R.id.my_mactivity_segment)
    SegmentControlView segmentControlView;

    @BindView(R.id.me_activities_recycleview)
    RecyclerView recyclerView;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.my_no_acitvity_tv)
    TextView noActivityTextView;

    private List<ActivityItemBean> mRegisterActivities;
    private List<ActivityItemBean> mStoredActivities;
    private List<ActivityItemBean> mSignedActivities;

    private int mRegisterPage;
    private int mStoredPage;
    private int mSignedPage;

    MeAcitivityAdapter mActivityAdapter;

    @Override
    protected void initIntent(Bundle bundle) {

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
        mActivityAdapter = new MeAcitivityAdapter();
        recyclerView.setAdapter(mActivityAdapter);
        CDividerItemDecoration itemDecoration = new CDividerItemDecoration(this,
            CDividerItemDecoration.VERTICAL_LIST,new ColorDrawable(Color.parseColor("#e0e0e0")));//
        itemDecoration.setHeight(1);
        recyclerView.addItemDecoration(itemDecoration);


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
                MeActivitiesActivity.this.loadData(true);
            }
        });

        mActivityAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                MeActivitiesActivity.this.loadData(false);
            }
        }, recyclerView);

        segmentControlView.setOnSegmentChangedListener(this);

        onSegmentChanged(0);

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_me_activities;
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

        mActivityAdapter.disableLoadMoreIfNotFullPage();

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
        if (isRefresh){
            swipeRefreshLayout.setRefreshing(true);
        }

        noActivityTextView.setVisibility(View.GONE);
        String token = YGApplication.accountManager.getToken();
        final int pageIndex = isRefresh ? 0 : mRegisterPage +1;
        LinkCall<BaseResultDataInfo<MyActivityBean>> activityCall = LinkCallHelper.getApiService()
            .getMyRegisterActivity(token, pageIndex);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MyActivityBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<MyActivityBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK) {
                    if (entity.data.activities.size() > 0) {
                        if (isRefresh) {
                            mRegisterActivities = entity.data.activities;
                        } else {
                            mRegisterActivities.addAll(entity.data.activities);
                        }
                        if (segmentControlView == null || segmentControlView.getSelectedIndex() == 0) {
                            if (isRefresh) {
                                mActivityAdapter.setNewData(mRegisterActivities);
                            } else {
                                mActivityAdapter.addData(entity.data.activities);
                                mActivityAdapter.notifyDataSetChanged();
                            }
                        }

                        mRegisterPage = pageIndex;
                        noActivityTextView.setVisibility(View.GONE);
                    }else{
                        noActivityTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    String msg = "加载失败";
                    if (entity != null) {
                        msg += "(" + entity.msg + ")";
                    }
                    Toast.makeText(MeActivitiesActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
                mActivityAdapter.disableLoadMoreIfNotFullPage();
            }
        });

    }

    private void loadStoredActivities(final boolean isRefresh) {
        if (isRefresh){
            swipeRefreshLayout.setRefreshing(true);
        }

        String token = YGApplication.accountManager.getToken();
        final int pageIndex = isRefresh? 0: mStoredPage+1;
        LinkCall<BaseResultDataInfo<MyActivityBean>> activityCall = LinkCallHelper.getApiService().getMyStoreActivity(
            token, pageIndex);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MyActivityBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<MyActivityBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK) {
                    if (entity.data.activities.size() > 0) {
                        if (isRefresh) {
                            mStoredActivities = entity.data.activities;
                        } else {
                            mSignedActivities.addAll(entity.data.activities);
                        }
                        if (segmentControlView.getSelectedIndex() == 1) {
                            if (isRefresh) {
                                mActivityAdapter.setNewData(mStoredActivities);
                            } else {
                                mActivityAdapter.addData(entity.data.activities);
                                mActivityAdapter.notifyDataSetChanged();
                            }
                        }
                        mStoredPage = pageIndex;
                        noActivityTextView.setVisibility(View.GONE);
                    }else{
                        noActivityTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    String msg = "加载失败";
                    if (entity != null) {
                        msg += "(" + entity.msg + ")";
                    }
                    Toast.makeText(MeActivitiesActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
                mActivityAdapter.disableLoadMoreIfNotFullPage();
            }
        });

    }

    private void loadSignedActivities(final boolean isRefresh) {
        if (isRefresh){
            swipeRefreshLayout.setRefreshing(true);
        }
        String token = YGApplication.accountManager.getToken();
        final int pageIndex = isRefresh?0: mSignedPage+1;
        LinkCall<BaseResultDataInfo<MyActivityBean>> activityCall = LinkCallHelper.getApiService().getMySigninActivity(
            token, pageIndex);
        activityCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MyActivityBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<MyActivityBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK) {
                    if (entity.data.activities.size() > 0) {
                        if (isRefresh) {
                            mSignedActivities = entity.data.activities;
                        }else{
                            mSignedActivities.addAll(entity.data.activities);
                        }
                        if (segmentControlView.getSelectedIndex() == 2) {
                            if (isRefresh) {
                                mActivityAdapter.setNewData(mSignedActivities);
                            }else{
                                mActivityAdapter.addData(entity.data.activities);
                                mActivityAdapter.notifyDataSetChanged();
                            }
                        }
                        mSignedPage = pageIndex;
                        noActivityTextView.setVisibility(View.GONE);
                    }else{
                        noActivityTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    String msg = "加载失败";
                    if (entity != null) {
                        msg += "(" + entity.msg + ")";
                    }
                    Toast.makeText(MeActivitiesActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
                mActivityAdapter.disableLoadMoreIfNotFullPage();
            }
        });
    }
}
