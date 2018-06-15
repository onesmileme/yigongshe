package com.ygs.android.yigongshe.ui.community;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.CommunityItemBean;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.view.TitleBarTabView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruichao on 2018/6/13.
 */

public class CommunityFragment extends BaseFragment {
  @BindView(R.id.titleBarTabView) TitleBarTabView mTitleBarTabView;
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  @BindView(R.id.swipeLayout) SwipeRefreshLayout mSwipeRefreshLayout;
  private CommunityAdapter mAdapter;
  private static final int PAGE_SIZE = 6;
  private int pageCnt = 1;

  @Override protected void initView() {
    //mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    initAdapter();
    initTitleBarTabView();
    addHeadView();
    //initRefreshLayout();
    //mSwipeRefreshLayout.setRefreshing(false);
    mSwipeRefreshLayout.setEnabled(false);
    refresh();
  }

  private void initTitleBarTabView() {
    String[] tabs = getResources().getStringArray(R.array.tab_view);
    for (int i = 0; i < tabs.length; i++) {
      mTitleBarTabView.addTab(tabs[i], i);
      mTitleBarTabView.addTabCheckListener(new TitleBarTabView.TabCheckListener() {
        @Override public void onTabChecked(int var1) {
          if (var1 == mTitleBarTabView.getCurrentTabPos()) {
            refresh();
          }
        }
      });
    }
    mTitleBarTabView.setCurrentTab(0);
  }

  private void addHeadView() {

  }

  private void initAdapter() {
    mAdapter = new CommunityAdapter();
    mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
      @Override public void onLoadMoreRequested() {
        loadMore();
      }
    }, mRecyclerView);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
      @Override public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

      }
    });
  }

  @Override public int getLayoutResId() {
    return R.layout.fragment_community;
  }

  private void refresh() {
    mAdapter.setEnableLoadMore(false);
    List<CommunityItemBean> data = new ArrayList();
    data.add(new CommunityItemBean("aaa"));
    data.add(new CommunityItemBean("aaa"));
    data.add(new CommunityItemBean("aaa"));
    data.add(new CommunityItemBean("aaa"));
    data.add(new CommunityItemBean("bbb"));
    data.add(new CommunityItemBean("bbb"));
    setData(true, data);
    mAdapter.setEnableLoadMore(true);
    mSwipeRefreshLayout.setRefreshing(false);
  }

  private void loadMore() {
    List<CommunityItemBean> data = new ArrayList();
    if (PAGE_SIZE > pageCnt) {
      data.add(new CommunityItemBean("ccc"));
      data.add(new CommunityItemBean("ccc"));
      data.add(new CommunityItemBean("ccc"));
      data.add(new CommunityItemBean("ddd"));
      data.add(new CommunityItemBean("ddd"));
    }

    data.add(new CommunityItemBean("ddd"));
    setData(false, data);
  }

  private void setData(boolean isRefresh, List data) {
    pageCnt++;
    final int size = data == null ? 0 : data.size();
    if (isRefresh) {
      mAdapter.setNewData(data);
    } else {
      if (size > 0) {
        mAdapter.addData(data);
      }
    }
    if (size < PAGE_SIZE) {
      //第一页如果不够一页就不显示没有更多数据布局
      mAdapter.loadMoreEnd(isRefresh);
    } else {
      mAdapter.loadMoreComplete();
    }
  }
}