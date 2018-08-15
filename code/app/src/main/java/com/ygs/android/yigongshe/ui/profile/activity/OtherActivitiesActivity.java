package com.ygs.android.yigongshe.ui.profile.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
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
import java.util.List;
import retrofit2.Response;

public class OtherActivitiesActivity extends BaseActivity {

  @BindView(R.id.titlebar) CommonTitleBar titleBar;

  @BindView(R.id.other_activities_recycleview) RecyclerView recyclerView;

  @BindView(R.id.swipeLayout) SwipeRefreshLayout swipeRefreshLayout;

  List<ActivityItemBean> mActivities;

  MeAcitivityAdapter mActivityAdapter;

  private String otherUid;

  @Override protected void initIntent(Bundle bundle) {

    otherUid = bundle.getString("otherUid");
    if (TextUtils.isEmpty(otherUid)) {
      Toast.makeText(this, "未获得用户ID", Toast.LENGTH_SHORT).show();
      finish();
    }
  }

  @Override protected void initView() {

    titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
          finish();
        }
      }
    });

    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    CDividerItemDecoration itemDecoration = new CDividerItemDecoration(this,
        CDividerItemDecoration.VERTICAL_LIST,new ColorDrawable(Color.parseColor("#e0e0e0")));//
    itemDecoration.setHeight(1);
    recyclerView.addItemDecoration(itemDecoration);

    mActivityAdapter = new MeAcitivityAdapter();
    recyclerView.setAdapter(mActivityAdapter);

    mActivityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ActivityItemBean itemBean = mActivities.get(position);

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
      @Override public void onRefresh() {
        loadAcitivities();
      }
    });

    loadAcitivities();
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_other_activities;
  }

  public void loadAcitivities() {

    String token = YGApplication.accountManager.getToken();

    LinkCall<BaseResultDataInfo<MyActivityBean>> activityCall =
        LinkCallHelper.getApiService().getOtherActivity(token, otherUid, "all");
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
