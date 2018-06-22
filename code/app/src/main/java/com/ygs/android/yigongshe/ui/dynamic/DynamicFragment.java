package com.ygs.android.yigongshe.ui.dynamic;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.DynamicListResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.view.TopBannerCard;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/13.
 */

public class DynamicFragment extends BaseFragment {
  private static final int PAGE_SIZE = 1;
  private static final int _COUNT = 20; //每页条数
  private int pageCnt = 0;
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  @BindView(R.id.swipeLayout) SwipeRefreshLayout mSwipeRefreshLayout;
  private DynamicAdapter mAdapter;
  private TopBannerCard mBannerCard;
  private LinkCall<BaseResultDataInfo<DynamicListResponse>> mCall;

  @Override protected void initView() {
    //mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    initAdapter();
    addHeadView();
    //initRefreshLayout();
    //mSwipeRefreshLayout.setRefreshing(false);
    mSwipeRefreshLayout.setEnabled(false);
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

  private void addHeadView() {
    mBannerCard = new TopBannerCard(getActivity(), mRecyclerView);
    mAdapter.addHeaderView(mBannerCard.getView());
  }

  private void initRefreshLayout() {
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        refresh();
      }
    });
  }

  private void refresh() {
    mAdapter.setEnableLoadMore(false);
    mCall = LinkCallHelper.getApiService().getDynamicLists(pageCnt, _COUNT);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<DynamicListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<DynamicListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          DynamicListResponse data = entity.data;
          pageCnt++;
          setData(true, data.news);
        }
      }
    });
    List<String> urls = new ArrayList<>();
    urls.add("http://img1.imgtn.bdimg.com/it/u=3044191397,2911599132&fm=27&gp=0.jpg");
    urls.add(
        "http://img.zcool.cn/community/01f09e577b85450000012e7e182cf0.jpg@1280w_1l_2o_100sh.jpg");
    setTopBanner(urls);
    mAdapter.setEnableLoadMore(true);
    mSwipeRefreshLayout.setRefreshing(false);
  }

  private void loadMore() {
    mCall = LinkCallHelper.getApiService().getDynamicLists(pageCnt, _COUNT);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<DynamicListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<DynamicListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          DynamicListResponse data = entity.data;
          pageCnt++;
          setData(false, data.news);
        }
      }
    });
  }

  private void setTopBanner(List<String> list) {
    if (null != mBannerCard) {
      mBannerCard.initViewWithData(list);
    }
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

  @Override public void onDestroyView() {

    if (mCall != null) {
      mCall.cancel();
    }
    super.onDestroyView();
  }
}
