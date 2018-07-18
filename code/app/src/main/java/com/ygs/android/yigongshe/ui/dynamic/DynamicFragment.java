package com.ygs.android.yigongshe.ui.dynamic;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.DynamicItemBean;
import com.ygs.android.yigongshe.bean.ShareBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.DynamicListResponse;
import com.ygs.android.yigongshe.bean.response.ScrollPicBean;
import com.ygs.android.yigongshe.bean.response.ScrollPicResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.utils.NetworkUtils;
import com.ygs.android.yigongshe.view.TopBannerCard;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/13.
 */

public class DynamicFragment extends BaseFragment {
  private static int _COUNT = 20; //每页条数
  private int pageCnt = 1; //第几页
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  @BindView(R.id.swipeLayout) SwipeRefreshLayout mSwipeRefreshLayout;
  private DynamicAdapter mAdapter;
  private TopBannerCard mBannerCard;
  private LinkCall<BaseResultDataInfo<DynamicListResponse>> mCall;
  private LinkCall<BaseResultDataInfo<ScrollPicResponse>> mScrollPicCall;
  private View errorView;

  @Override protected void initView() {
    errorView =
        getLayoutInflater().inflate(R.layout.error_view, (ViewGroup) mRecyclerView.getParent(),
            false);

    errorView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        refresh();
      }
    });
    //mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    initAdapter();
    addHeadView();
    //initRefreshLayout();
    mSwipeRefreshLayout.setRefreshing(true);
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
        Bundle bundle = new Bundle();
        DynamicItemBean itemBean = ((DynamicItemBean) adapter.getItem(position));
        bundle.putInt("news_id", itemBean.newsid);
        bundle.putString("news_title", itemBean.title);
        ShareBean shareBean = new ShareBean(itemBean.title, itemBean.desc, "www.baidu.com");
        bundle.putSerializable("shareBean", shareBean);
        goToOthers(DynamicDetailActivity.class, bundle);
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
    if (!NetworkUtils.isConnected(getActivity())) {
      mAdapter.setEmptyView(errorView);
      return;
    }
    mAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecyclerView.getParent());
    mAdapter.setEnableLoadMore(false);
    mScrollPicCall = LinkCallHelper.getApiService().getScrollPicList("动态");
    mScrollPicCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ScrollPicResponse>>() {
      public void onResponse(BaseResultDataInfo<ScrollPicResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          ScrollPicResponse data = entity.data;
          List<ScrollPicBean> list = data.slide_list;
          List<String> urls = new ArrayList<>();
          for (ScrollPicBean item : list) {
            urls.add(item.pic);
          }
          setTopBanner(urls);
        } else {
          mAdapter.setEmptyView(errorView);
        }
      }
    });
    mCall = LinkCallHelper.getApiService().getDynamicLists(pageCnt, _COUNT);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<DynamicListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<DynamicListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          DynamicListResponse data = entity.data;
          pageCnt = data.page;
          ++pageCnt;
          _COUNT = data.perpage;
          setData(true, data.news);
          mAdapter.setEnableLoadMore(true);
          mSwipeRefreshLayout.setRefreshing(false);
        } else {
          mAdapter.setEnableLoadMore(true);
          mSwipeRefreshLayout.setRefreshing(false);
        }
      }
    });
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
          pageCnt = data.page;
          ++pageCnt;
          setData(false, data.news);
        } else {
          mAdapter.loadMoreFail();
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
    final int size = data == null ? 0 : data.size();
    if (isRefresh) {
      mAdapter.setNewData(data);
    } else {
      if (size > 0) {
        mAdapter.addData(data);
      }
    }
    if (size < _COUNT) {
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
    if (mScrollPicCall != null) {
      mScrollPicCall.cancel();
    }
    super.onDestroyView();
  }
}
