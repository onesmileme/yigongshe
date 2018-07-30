package com.ygs.android.yigongshe.ui.profile.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.CommunityItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.CommunityListResponse;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.community.CommunityDetailActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;
import retrofit2.Response;

/**
 * 我的益工圈
 */
public class MeCommunityActivity extends BaseActivity {

    @BindView(R.id.me_community_recycleview)
    RecyclerView mRecycleView;

    @BindView(R.id.layout_titlebar)
    CommonTitleBar mTitleBar;

    @BindView(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;

    MeCommunityAdapter mAdapter;

    LinkCall<BaseResultDataInfo<CommunityListResponse>> mCall;

    @Override
    protected void initIntent(Bundle bundle){

    }

    @Override
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
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                CommunityItemBean itemBean = ((CommunityItemBean) adapter.getItem(position));
                bundle.putInt("pubcircle_id", itemBean.pubcircleid);
                bundle.putSerializable("item", itemBean);
                goToOthersForResult(MeCommunityActivity.class, bundle, 2);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        refresh();
    }

    @Override
    protected  int getLayoutResId(){
        return R.layout.activity_me_community;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2){
            swipeRefreshLayout.setRefreshing(true);
            refresh();
        }
    }

    private void refresh() {

        mAdapter.setEnableLoadMore(false);
        String token = YGApplication.accountManager.getToken();
        mCall = LinkCallHelper.getApiService().getUserCommunityList(token,null);
        mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CommunityListResponse>>() {
            @Override
            public void onResponse(BaseResultDataInfo<CommunityListResponse> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatusInterface.OK) {
                    CommunityListResponse data = entity.data;
                   mAdapter.setNewData(data.list);
                }else{
                    String msg = "请求益工圈失败";
                    if (entity != null){
                        msg += "("+entity.msg+")";
                    }
                    Toast.makeText(MeCommunityActivity.this,msg,Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mAdapter.setEnableLoadMore(true);
    }
}
