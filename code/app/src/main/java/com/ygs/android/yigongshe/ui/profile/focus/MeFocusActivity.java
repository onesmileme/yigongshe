package com.ygs.android.yigongshe.ui.profile.focus;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.EmptyBean;
import com.ygs.android.yigongshe.bean.FollowPersonDataBean;
import com.ygs.android.yigongshe.bean.FollowPersonItemBean;
import com.ygs.android.yigongshe.bean.MeFocusBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiService;
import com.ygs.android.yigongshe.net.ApiStatusInterface;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

import org.w3c.dom.Text;

import butterknife.BindView;
import retrofit2.Response;

/**
 * 我的关注
 */
public class MeFocusActivity extends BaseActivity implements MeFocusFollowListener{

    @BindView(R.id.me_focus_recycleview)
    RecyclerView recyclerView;

    @BindView(R.id.titlebar_text_title)
    TextView titleView;

    @BindView(R.id.titlebar_backward_btn)
    Button backButton;

    MeFocusAdapter focusAdapter;

    @BindView(R.id.me_focus_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private int mPageIndex = 1;

    private LinkCall<BaseResultDataInfo<FollowPersonDataBean>> mCall;

    protected  void initIntent(Bundle bundle){

    }

    protected  void initView(){

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleView.setText(R.string.my_focus);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        focusAdapter = new MeFocusAdapter(this,this);
        recyclerView.setAdapter(focusAdapter);

        focusAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener(){
                                               @Override
                                               public void onLoadMoreRequested() {
                                                   loadMore();
                                               }
                                           }
        ,recyclerView);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true);
            }
        });
    }

    protected  int getLayoutResId(){
        return R.layout.activity_me_focus;
    }

    private void loadData(final boolean isRefresh){

        focusAdapter.setEnableLoadMore(false);
        if (isRefresh){
            mPageIndex= 0;
        }
        String token = YGApplication.accountManager.getToken();
        mCall = LinkCallHelper.getApiService().getFolloPersonList(token,++mPageIndex );
        mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<FollowPersonDataBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<FollowPersonDataBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity.error == ApiStatusInterface.OK){
                    addData(isRefresh,entity.data);
                }
            }
        });
        focusAdapter.setEnableLoadMore(true);
    }

    private void loadMore(){
        loadData(false);
    }

    private void addData(boolean isRefresh , FollowPersonDataBean followPersonDataBean){

        if (isRefresh){
            focusAdapter.setNewData(followPersonDataBean.list);
        }

    }

    @Override
    public void unfollow(final FollowPersonItemBean focusBean) {

        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<EmptyBean>>unfollowCall = LinkCallHelper.getApiService().unFollow(token,focusBean.userid);
        unfollowCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatusInterface.OK){
                    focusBean.unfollowed = true;
                    focusAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void follow(final FollowPersonItemBean focusBean) {

        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<EmptyBean>> followCall = LinkCallHelper.getApiService().doFollow(token,focusBean.userid);
        followCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<EmptyBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<EmptyBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatusInterface.OK){
                    focusBean.unfollowed = false;
                    focusAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}
