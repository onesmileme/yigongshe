package com.ygs.android.yigongshe.ui.profile.community;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.CommunityItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.CommunityListResponse;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CDividerItemDecoration;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import butterknife.BindView;
import retrofit2.Response;

/**
 * 我的益工圈
 */
public class OtherCommunityActivity extends BaseActivity {

    @BindView(R.id.other_community_recycleview)
    RecyclerView mRecycleView;

    @BindView(R.id.layout_titlebar)
    CommonTitleBar mTitleBar;

    @BindView(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;

    MeCommunityAdapter mAdapter;

    LinkCall<BaseResultDataInfo<CommunityListResponse>> mCall;

    private String otherUid;

    @Override
    protected void initIntent(Bundle bundle){

        otherUid = bundle.getString("otherUid");
        if (TextUtils.isEmpty(otherUid)){
            Toast.makeText(this,"未获得用户ID",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void initView(){

        mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    OtherCommunityActivity.this.finish();
                }
            }
        });

        mAdapter = new MeCommunityAdapter();
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mAdapter);
        CDividerItemDecoration itemDecoration = new CDividerItemDecoration(this,
            CDividerItemDecoration.VERTICAL_LIST, new ColorDrawable(Color.parseColor("#e0e0e0")));//
        itemDecoration.setHeight(1);
        mRecycleView.addItemDecoration(itemDecoration);
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
        return R.layout.activity_other_community;
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

        mAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecycleView.getParent());
        mAdapter.setEnableLoadMore(false);
        mAdapter.setEnableLoadMore(false);
        String token = YGApplication.accountManager.getToken();
        mCall = LinkCallHelper.getApiService().getUserCommunityList(token,otherUid);
        mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CommunityListResponse>>() {
            @Override
            public void onResponse(BaseResultDataInfo<CommunityListResponse> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK) {
                    ((FrameLayout)mAdapter.getEmptyView()).removeAllViews();
                    CommunityListResponse data = entity.data;
                   mAdapter.setNewData(data.list);
                   if (data.list == null || data.list.size() == 0){
                       Toast.makeText(OtherCommunityActivity.this,"我的益工圈沒有数据",Toast.LENGTH_SHORT).show();
                   }
                }else{
                    String msg = "请求益工圈失败";
                    if (entity != null){
                        msg += "("+entity.msg+")";
                    }
                    Toast.makeText(OtherCommunityActivity.this,msg,Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mAdapter.setEnableLoadMore(true);
    }
}
