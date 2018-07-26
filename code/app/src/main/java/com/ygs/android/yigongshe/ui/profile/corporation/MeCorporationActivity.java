package com.ygs.android.yigongshe.ui.profile.corporation;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.ActivityItemBean;
import com.ygs.android.yigongshe.bean.MeCorporationBean;
import com.ygs.android.yigongshe.bean.ShareBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.ActivityListResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.activity.ActivityDetailActivity;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/7/26.
 */

public class MeCorporationActivity extends BaseActivity {
  @BindView(R.id.titleBar) CommonTitleBar mTitleBar;
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  private MeCorporationAdapter mAdapter;
  private LinkCall<BaseResultDataInfo<MeCorporationBean>> mCall;
  private LinkCall<BaseResultDataInfo<ActivityListResponse>> mCall2;
  private List<MultiItemEntity> mList;

  @Override protected void initIntent(Bundle bundle) {

  }

  @Override protected void initView() {
    mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
          finish();
        }
      }
    });
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    requestData();
  }

  private void requestData() {
    final AccountManager accountManager = YGApplication.accountManager;
    mCall = LinkCallHelper.getApiService().getMeCorporationInfo(accountManager.getToken());
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MeCorporationBean>>() {
      @Override
      public void onResponse(BaseResultDataInfo<MeCorporationBean> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          transData(entity.data);
          mCall2 =
              LinkCallHelper.getApiService().getMeCorporationActivities(accountManager.getToken());
          mCall2.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ActivityListResponse>>() {
            @Override public void onResponse(BaseResultDataInfo<ActivityListResponse> entity,
                Response<?> response, Throwable throwable) {
              super.onResponse(entity, response, throwable);
              if (entity != null && entity.error == 2000) {
                if (entity.data != null) {
                  transActivityData(entity.data.activities);
                  mAdapter = new MeCorporationAdapter(mList);
                  mRecyclerView.setAdapter(mAdapter);
                  mAdapter.setOnItemChildClickListener(
                      new BaseQuickAdapter.OnItemChildClickListener() {
                        @Override public void onItemChildClick(BaseQuickAdapter adapter, View view,
                            int position) {
                          Bundle bundle = new Bundle();
                          MeCorporationBean.MeCorporationTransItemBean4 itemBean =
                              ((MeCorporationBean.MeCorporationTransItemBean4) adapter.getItem(
                                  position));
                          bundle.putInt("activity_id", itemBean.activityid);
                          bundle.putString("activity_title", itemBean.title);
                          ShareBean shareBean =
                              new ShareBean(itemBean.title, itemBean.desc, itemBean.link);
                          bundle.putSerializable("shareBean", shareBean);
                          goToOthers(ActivityDetailActivity.class, bundle);
                        }
                      });
                }
              }
            }
          });
        }
      }
    });
  }

  private void transData(MeCorporationBean data) {
    if (data != null) {
      mList = new ArrayList<>();
      mList.add(new MeCorporationBean.MeCorporationTransItemBean2("社团信息", data.assciation_desc));
      mList.add(new MeCorporationBean.MeCorporationTransItemBean2("社团名称", data.assciation_name));
      mList.add(new MeCorporationBean.MeCorporationTransItemBean0());
      mList.add(new MeCorporationBean.MeCorporationTransItemBean1("社员信息"));
      mList.add(new MeCorporationBean.MeCorporationTransItemBean3("姓名", "学校", "职位"));
      for (MeCorporationBean.UserInfo userInfo : data.user_list) {
        mList.add(new MeCorporationBean.MeCorporationTransItemBean3(userInfo));
      }
      mList.add(new MeCorporationBean.MeCorporationTransItemBean0());
      mList.add(new MeCorporationBean.MeCorporationTransItemBean1("社团活动"));
    }
  }

  private void transActivityData(List<ActivityItemBean> datas) {
    if (datas != null && datas.size() > 0) {
      for (ActivityItemBean bean : datas) {
        mList.add(new MeCorporationBean.MeCorporationTransItemBean4(bean));
      }
    }
  }

  @Override protected int getLayoutResId() {
    return R.layout.me_activity_corporation;
  }

  @Override protected void onStop() {
    if (!mCall.isCanceled()) {
      mCall.cancel();
    }
    super.onStop();
  }
}
