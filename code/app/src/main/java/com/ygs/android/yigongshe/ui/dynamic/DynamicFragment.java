package com.ygs.android.yigongshe.ui.dynamic;

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
import android.widget.LinearLayout;

import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
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
import com.ygs.android.yigongshe.view.CDividerItemDecoration;
import com.ygs.android.yigongshe.view.MyDividerItemDecoration;
import com.ygs.android.yigongshe.view.TopBannerCard;
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
  @BindView(R.id.dynamic_banner_layout) LinearLayout mBannerLayout;
  private DynamicAdapter mAdapter;
  private TopBannerCard mBannerCard;
  private LinkCall<BaseResultDataInfo<DynamicListResponse>> mCall;
  private LinkCall<BaseResultDataInfo<ScrollPicResponse>> mScrollPicCall;
  private View errorView;
  private View noDataView;
  AccountManager accountManager = YGApplication.accountManager;

  @Override protected void initView() {
    errorView =
        getLayoutInflater().inflate(R.layout.error_view, (ViewGroup) mRecyclerView.getParent(),
            false);
    noDataView =
        getLayoutInflater().inflate(R.layout.view_nodata, (ViewGroup) mRecyclerView.getParent(),
            false);
    errorView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        refresh();
      }
    });
    //mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    CDividerItemDecoration itemDecoration = new CDividerItemDecoration(this.getActivity(),
        CDividerItemDecoration.VERTICAL_LIST, new ColorDrawable(Color.parseColor("#e0e0e0")));//
    itemDecoration.setHeight(1);
    mRecyclerView.addItemDecoration(itemDecoration);

    //mRecyclerView.addItemDecoration(
        //new MyDividerItemDecoration(getActivity(), MyDividerItemDecoration.VERTICAL));
    initAdapter();
    addHeadView();
    initRefreshLayout();
    mSwipeRefreshLayout.setRefreshing(true);
    mSwipeRefreshLayout.setEnabled(true);
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
        ShareBean shareBean = new ShareBean(itemBean.title, itemBean.desc, null);
        bundle.putSerializable("shareBean", shareBean);
        goToOthers(DynamicDetailActivity.class, bundle);
      }
    });
  }

  private void addHeadView() {
    mBannerCard = new TopBannerCard(getActivity(), mRecyclerView, 0);
    //mAdapter.addHeaderView(mBannerCard.getView());
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    mBannerLayout.addView(mBannerCard.getView(),layoutParams);
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
    pageCnt = 1;
    mAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecyclerView.getParent());
    mAdapter.setEnableLoadMore(false);
    if (mScrollPicCall != null) {
      mScrollPicCall.cancel();
    }
    mScrollPicCall = LinkCallHelper.getApiService().getScrollPicList("动态");
    mScrollPicCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ScrollPicResponse>>() {
      public void onResponse(BaseResultDataInfo<ScrollPicResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          ((FrameLayout) mAdapter.getEmptyView()).removeAllViews();
          ScrollPicResponse data = entity.data;
          List<ScrollPicBean> list = data.slide_list;
          //List<String> urls = new ArrayList<>();
          //for (ScrollPicBean item : list) {
          //  urls.add(item.pic);
          //}
          setTopBanner(list);
        } else {
          //mAdapter.setEmptyView(errorView);
        }
      }
    });

    if (TextUtils.isEmpty(accountManager.getToken())) {
      return;
    }

    if (mCall != null) {
      mCall.cancel();
    }
    mCall =
        LinkCallHelper.getApiService().getDynamicLists(pageCnt, _COUNT, accountManager.getToken());

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
          mAdapter.setEmptyView(errorView);
          mAdapter.setEnableLoadMore(true);
          mSwipeRefreshLayout.setRefreshing(false);
        }
      }
    });
  }

  private void loadMore() {
    mCall =
        LinkCallHelper.getApiService().getDynamicLists(pageCnt, _COUNT, accountManager.getToken());
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

  private void setTopBanner(List<ScrollPicBean> list) {
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

  @Override public void onStop() {

    if (mCall != null) {
      mCall.cancel();
    }
    if (mScrollPicCall != null) {
      mScrollPicCall.cancel();
    }
    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(false);
    }
    super.onStop();
  }
}
