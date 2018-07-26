package com.ygs.android.yigongshe.ui.profile.corporation;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.MeCorporationBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
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
    AccountManager accountManager = YGApplication.accountManager;
    mCall = LinkCallHelper.getApiService().getMeCorporationInfo(accountManager.getToken());
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<MeCorporationBean>>() {
      @Override
      public void onResponse(BaseResultDataInfo<MeCorporationBean> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          if (entity.data != null) {
            transData(entity.data);
          }
        }
        mAdapter = new MeCorporationAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
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
