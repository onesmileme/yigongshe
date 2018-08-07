package com.ygs.android.yigongshe.ui.profile.run;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.RunItemBean;
import com.ygs.android.yigongshe.bean.RunListBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.profile.MeSectionDecoration;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Response;

/**
 * 益行走
 */
public class MeRunActivity extends BaseActivity {

    @BindView(R.id.layout_titlebar)
    CommonTitleBar titleBar;

    @BindView(R.id.me_run_recycleview)
    RecyclerView recyclerView;

    @BindView(R.id.run_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    MeRunAdapter runAdapter;

    LinkCall<BaseResultDataInfo<RunListBean>> mCall;
    private int currentPage = 0;
    private static final int PER_PAGE = 20;

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

        List<Integer> showList = new LinkedList<>();
        showList.add(1);
        MeSectionDecoration decoration = new MeSectionDecoration(showList, this);
        decoration.setHintHight(10);
        recyclerView.addItemDecoration(decoration);

        runAdapter = new MeRunAdapter(this);
        recyclerView.setAdapter(runAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true);
            }
        });

        runAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData(false);
            }
        }, recyclerView);
        runAdapter.setEnableLoadMore(true);
        loadData(true);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_me_run;
    }

    private void loadData(final boolean isRefresh) {

        String token = YGApplication.accountManager.getToken();
        final int page;
        if (isRefresh) {
            page = 0;
        } else {
            page = currentPage + 1;
        }
        mCall = LinkCallHelper.getApiService().getRankList(token, page, PER_PAGE);
        mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<RunListBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<RunListBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity.error == ApiStatus.OK) {
                    currentPage = page;
                    List<RunItemBean> lists = null;
                    if (currentPage == 0) {
                        lists = new ArrayList<>(entity.getData().rank_list.size() + 1);
                        if (entity.getData().user_info != null) {
                            lists.add(0, entity.getData().user_info);
                        }
                        if (entity.getData().rank_list != null) {
                            lists.addAll(entity.getData().rank_list);
                        }
                        runAdapter.setNewData(lists);
                    } else {
                        lists = entity.getData().rank_list;
                        runAdapter.addData(lists);
                        runAdapter.notifyDataSetChanged();
                    }
                }

                if (isRefresh) {
                    refreshLayout.setRefreshing(false);
                }else {
                    runAdapter.loadMoreComplete();
                }
                runAdapter.disableLoadMoreIfNotFullPage();
            }

            @Override
            public void networkError(IOException e, LinkCall call) {
                super.networkError(e, call);
                refreshLayout.setRefreshing(false);
                runAdapter.loadMoreComplete();
                runAdapter.disableLoadMoreIfNotFullPage();
            }
        });
    }

}
