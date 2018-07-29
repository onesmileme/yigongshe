package com.ygs.android.yigongshe.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.UserInfoBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.ui.profile.MeProfileAdapter;
import com.ygs.android.yigongshe.ui.profile.MeSectionDecoration;
import com.ygs.android.yigongshe.ui.profile.info.MeInfoActivity;
import com.ygs.android.yigongshe.ui.profile.set.MeSetAcitivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import java.util.LinkedList;
import java.util.List;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/13.
 */

public class MeFragment extends BaseFragment implements View.OnClickListener {

  private static final String TAG = "ME";

  @BindView(R.id.me_main_recycleview) RecyclerView mRecycleView;

  @BindView(R.id.layout_titlebar) CommonTitleBar mTitleBar;

  private MeProfileAdapter mProfileAdapter;

  private BroadcastReceiver mLoginBroadcastReceiver;

  @Override
  public void initView() {

    mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

    mProfileAdapter = new MeProfileAdapter(getContext(),this);
    mProfileAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        click(position);
      }
    });
    mRecycleView.setAdapter(mProfileAdapter);

    List<Integer> showList = new LinkedList<>();
    showList.add(1);
    showList.add(3);
    showList.add(6);
    MeSectionDecoration decoration = new MeSectionDecoration(showList, getContext());
    mRecycleView.addItemDecoration(decoration);


    mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_RIGHT_BUTTON) {
          Intent intent = new Intent(MeFragment.this.getContext(), MeSetAcitivity.class);
          MeFragment.this.getContext().startActivity(intent);
        }
      }
    });

    AccountManager accountManager = YGApplication.accountManager;
    if (accountManager.getUserInfoBean() != null){
      mProfileAdapter.updateUserInfo(accountManager.getUserInfoBean());
    }

      Log.e(TAG, "loadUserInfo: token is: "+accountManager.getToken() );
  }

  @Override public int getLayoutResId() {
    return R.layout.fragment_me;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mLoginBroadcastReceiver != null) {
      LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
      broadcastManager.unregisterReceiver(mLoginBroadcastReceiver);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    AccountManager accountManager = YGApplication.accountManager;
    mProfileAdapter.updateUserInfo(accountManager.getUserInfoBean());
  }

  private void click(int position) {

    Class clazz = mProfileAdapter.detailClassAtPosition(position);
    if (clazz != null) {
      Intent intent = new Intent(this.getActivity(), clazz);
      startActivity(intent);
    }
  }

  @Override
  public void onClick(View vew){

    Intent intent = new Intent(getActivity(), MeInfoActivity.class);
    startActivity(intent);
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (mLoginBroadcastReceiver == null) {
      addLoginReceiver(context);
    }
    AccountManager accountManager = YGApplication.accountManager;
    if (accountManager.getToken() != null) {
      loadUserInfo(accountManager.getToken());
    }
  }

  private void addLoginReceiver(Context context) {

    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
    IntentFilter intentFilter = new IntentFilter("com.ygs.android.yigongshe.login");
    mLoginBroadcastReceiver = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
        String token = intent.getStringExtra("token");
        if (token != null) {
          loadUserInfo(token);
        }
      }
    };
    broadcastManager.registerReceiver(mLoginBroadcastReceiver, intentFilter);
  }

  private void loadUserInfo(String token) {

    LinkCall<BaseResultDataInfo<UserInfoBean>> userInfoCall =
        LinkCallHelper.getApiService().getUserInfo(token);
    userInfoCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UserInfoBean>>() {

      @Override
      public void onResponse(BaseResultDataInfo<UserInfoBean> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity == null) {
          Log.e(TAG, "onResponse: load user info failed " + response);
          return;
        }
        if (entity.error == ApiStatusInterface.OK) {
          AccountManager accountManager = YGApplication.accountManager;
          Log.e("ME", "onResponse: " + entity.data);
          if (accountManager != null) {
            accountManager.updateUserInfo(entity.data);
          }
          MeFragment.this.mProfileAdapter.updateUserInfo(entity.data);
        }
      }
    });
  }
}
