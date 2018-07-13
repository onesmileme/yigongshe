package com.ygs.android.yigongshe.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.ActivityItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.ActivityListResponse;
import com.ygs.android.yigongshe.bean.response.ScrollPicBean;
import com.ygs.android.yigongshe.bean.response.ScrollPicResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.view.ActivityStatusTypeView;
import com.ygs.android.yigongshe.view.TopBannerCard;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/13.
 */

public class ActivityFragment extends BaseFragment
    implements ActivityStatusTypeView.StatusSelectListener,
    ActivityStatusTypeView.TypeSelectListener {
  private static int PAGE_SIZE = 1;
  private static int _COUNT = 20; //每页条数
  private int pageCnt = 0;
  private String cate;//类型，公益、文化、创业等，默认为全部
  private String progress;//状态，进行中、已完成、打call失败
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  @BindView(R.id.swipeLayout) SwipeRefreshLayout mSwipeRefreshLayout;
  private ActivityAdapter mAdapter;
  private TopBannerCard mBannerCard;
  private LinkCall<BaseResultDataInfo<ActivityListResponse>> mCall;
  private LinkCall<BaseResultDataInfo<ScrollPicResponse>> mScrollPicCall;

  @Override protected void initView() {
    //mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    initAdapter();
    addHeadView();
    //initRefreshLayout();
    mSwipeRefreshLayout.setRefreshing(true);
    mSwipeRefreshLayout.setEnabled(false);
    requestBannerData();
    refresh();
  }

  @Override public int getLayoutResId() {
    return R.layout.fragment_activity;
  }

  private void initAdapter() {
    mAdapter = new ActivityAdapter();
    mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
      @Override public void onLoadMoreRequested() {
        loadMore();
      }
    }, mRecyclerView);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
      @Override public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
        Bundle bundle = new Bundle();
        ActivityItemBean itemBean = ((ActivityItemBean) adapter.getItem(position));
        bundle.putInt("activity_id", itemBean.activityid);
        bundle.putString("activity_title", itemBean.title);
        goToOthers(ActivityDetailActivity.class, bundle);
      }
    });
  }

  private void addHeadView() {
    mBannerCard = new TopBannerCard(getActivity(), mRecyclerView);
    mAdapter.addHeaderView(mBannerCard.getView());
    ActivityStatusTypeView activityStatusTypeView =
        new ActivityStatusTypeView(getActivity(), mRecyclerView, this, this);
    mAdapter.addHeaderView(activityStatusTypeView.getView());
  }

  private void refresh() {
    mAdapter.setEnableLoadMore(false);

    mCall = LinkCallHelper.getApiService().getActivityLists(pageCnt, _COUNT, cate, progress);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ActivityListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<ActivityListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          ActivityListResponse data = entity.data;
          PAGE_SIZE = data.page;
          _COUNT = data.perpage;
          setData(true, data.activities);
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
    mCall = LinkCallHelper.getApiService().getActivityLists(pageCnt, _COUNT, cate, progress);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ActivityListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<ActivityListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          ActivityListResponse data = entity.data;
          setData(false, data.activities);
        } else {
          mAdapter.loadMoreFail();
        }
      }
    });
  }

  private void requestBannerData() {
    mScrollPicCall = LinkCallHelper.getApiService().getScrollPicList("活动");
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
    if (size <= _COUNT && PAGE_SIZE == 1) {
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

  @Override public void OnStatusSelected(String item) {
    cate = item;
    refresh();
  }

  @Override public void OnTypeSelected(String item) {
    progress = item;
    refresh();
  }
}
