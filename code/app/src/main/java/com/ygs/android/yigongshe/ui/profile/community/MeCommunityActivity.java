package com.ygs.android.yigongshe.ui.profile.community;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.CommunityListResponse;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;
import retrofit2.Response;

public class MeCommunityActivity extends BaseActivity {

    @BindView(R.id.me_community_recycleview)
    RecyclerView mRecycleView;

    @BindView(R.id.layout_titlebar)
    CommonTitleBar mTitleBar;

    MeCommunityAdapter mAdapter;

    LinkCall<BaseResultDataInfo<CommunityListResponse>> mCall;

    protected void initIntent(Bundle bundle){

    }

    protected void initView(){

        mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    MeCommunityActivity.this.finish();
                }
            }
        });

        mAdapter = new MeCommunityAdapter();
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mAdapter);

        refresh();
    }

    protected  int getLayoutResId(){
        return R.layout.activity_me_community;
    }

    private void refresh() {

        mAdapter.setEnableLoadMore(false);
        String token = YGApplication.accountManager.getToken();
        mCall = LinkCallHelper.getApiService().getMyCommunityList(token);
        mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CommunityListResponse>>() {
            @Override
            public void onResponse(BaseResultDataInfo<CommunityListResponse> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatusInterface.OK) {
                    CommunityListResponse data = entity.data;
                   mAdapter.setNewData(data.list);
                }
            }
        });
        mAdapter.setEnableLoadMore(true);
//        mSwipeRefreshLayout.setRefreshing(false);
    }
}
