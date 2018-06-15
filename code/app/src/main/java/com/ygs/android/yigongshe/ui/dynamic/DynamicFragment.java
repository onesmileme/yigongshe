package com.ygs.android.yigongshe.ui.dynamic;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.DynamicItemBean;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.view.TopBannerCard;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruichao on 2018/6/13.
 */

public class DynamicFragment extends BaseFragment {
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  @BindView(R.id.swipeLayout) SwipeRefreshLayout mSwipeRefreshLayout;
  private DynamicAdapter mAdapter;

  @Override protected void initView() {
    mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    initAdapter();
    //addHeadView();
    initRefreshLayout();
    mSwipeRefreshLayout.setRefreshing(true);
    refresh();
  }

  @Override public int getLayoutResId() {
    return R.layout.fragment_dynamic;
  }

  private void initAdapter() {
    mAdapter = new DynamicAdapter();
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

  //private void addHeadView() {
  //  View headView = getLayoutInflater().inflate(R.layout.view_top_banner,
  //      (ViewGroup) mRecyclerView.getParent(), false);
  //
  //  mAdapter.addHeaderView(headView);
  //}

  private void initRefreshLayout() {
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        refresh();
      }
    });
  }

  private void refresh() {
    mAdapter.setEnableLoadMore(false);
    List<DynamicItemBean> data = new ArrayList();
    data.add(new DynamicItemBean("aaa"));
    data.add(new DynamicItemBean("aaa"));
    data.add(new DynamicItemBean("aaa"));
    data.add(new DynamicItemBean("aaa"));
    data.add(new DynamicItemBean("bbb"));
    data.add(new DynamicItemBean("bbb"));
    List<String> urls = new ArrayList<>();
    urls.add("http://img1.imgtn.bdimg.com/it/u=3044191397,2911599132&fm=27&gp=0.jpg");
    urls.add(
        "http://img.zcool.cn/community/01f09e577b85450000012e7e182cf0.jpg@1280w_1l_2o_100sh.jpg");
    setTopBanner(urls);
    setData(true, data);
    mAdapter.setEnableLoadMore(true);
    mSwipeRefreshLayout.setRefreshing(false);
  }

  private void loadMore() {
    List<DynamicItemBean> data = new ArrayList();
    data.add(new DynamicItemBean("ccc"));
    data.add(new DynamicItemBean("ddd"));
    setData(false, data);
  }

  private void setTopBanner(List<String> list) {
    TopBannerCard bannerCard = new TopBannerCard(getActivity(), mRecyclerView);
    bannerCard.initViewWithData(list);
    mAdapter.addHeaderView(bannerCard.getView());
  }

  private void setData(boolean isRefresh, List data) {
    final int size = data == null ? 0 : data.size();
    if (isRefresh) {
      mAdapter.setNewData(data);
    } else {
      if (size > 0) {
        mAdapter.addData(data);
      }
    }
    if (size < 6) {
      //第一页如果不够一页就不显示没有更多数据布局
      mAdapter.loadMoreEnd(isRefresh);
    } else {
      mAdapter.loadMoreComplete();
    }
  }
}
