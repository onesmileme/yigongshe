package com.ygs.android.yigongshe.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.ActivityItemBean;
import com.ygs.android.yigongshe.bean.BaseEvent;
import com.ygs.android.yigongshe.bean.LocationEvent;
import com.ygs.android.yigongshe.bean.ShareBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.ActivityListResponse;
import com.ygs.android.yigongshe.bean.response.ScrollPicBean;
import com.ygs.android.yigongshe.bean.response.ScrollPicResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.ui.community.CitySelectActivity;
import com.ygs.android.yigongshe.utils.NetworkUtils;
import com.ygs.android.yigongshe.view.ActivityStatusTypeView;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import com.ygs.android.yigongshe.view.MyDividerItemDecoration;
import com.ygs.android.yigongshe.view.TopBannerCard;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/13.
 */

public class ActivityFragment extends BaseFragment
    implements ActivityStatusTypeView.StatusSelectListener,
    ActivityStatusTypeView.TypeSelectListener {
  private static int _COUNT = 20; //每页条数
  private int pageCnt = 1;
  private String cate;//类型，公益、文化、创业等，默认为全部
  private String progress;//状态，进行中、已完成、打call失败
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  @BindView(R.id.swipeLayout) SwipeRefreshLayout mSwipeRefreshLayout;
  private ActivityAdapter mAdapter;
  private TopBannerCard mBannerCard;
  private LinkCall<BaseResultDataInfo<ActivityListResponse>> mCall;
  private LinkCall<BaseResultDataInfo<ScrollPicResponse>> mScrollPicCall;
  private View errorView;
  @BindView(R.id.titleBar) CommonTitleBar mTitleBar;
  AccountManager mAccountManager = YGApplication.accountManager;
  //private View noDataView;
  private String mCity;

  @Override protected void initView() {
    EventBus.getDefault().register(this);
    mTitleBar.getLeftCustomView().setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(getActivity(), CitySelectActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
      }
    });

    errorView =
        getLayoutInflater().inflate(R.layout.error_view, (ViewGroup) mRecyclerView.getParent(),
            false);
    //noDataView = getLayoutInflater().inflate(R.layout.view_nodata, mRecyclerView, false);
    errorView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        refresh();
      }
    });
    //mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mRecyclerView.addItemDecoration(
        new MyDividerItemDecoration(getActivity(), MyDividerItemDecoration.VERTICAL));
    initAdapter();
    addHeadView();
    initRefreshLayout();
    mSwipeRefreshLayout.setRefreshing(true);
    mSwipeRefreshLayout.setEnabled(true);
    requestBannerData();
    refresh();
  }

  private void initRefreshLayout() {
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        refresh();
      }
    });
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
        ShareBean shareBean = new ShareBean(itemBean.title, itemBean.desc, itemBean.share_url);
        bundle.putSerializable("shareBean", shareBean);
        goToOthers(ActivityDetailActivity.class, bundle);
      }
    });
  }

  private void addHeadView() {
    mBannerCard = new TopBannerCard(getActivity(), mRecyclerView, 1);
    mAdapter.addHeaderView(mBannerCard.getView());
    ActivityStatusTypeView activityStatusTypeView =
        new ActivityStatusTypeView(getActivity(), mRecyclerView, this, this);
    mAdapter.addHeaderView(activityStatusTypeView.getView());
  }

  private void refresh() {
    if (!NetworkUtils.isConnected(getActivity())) {
      mAdapter.setEmptyView(errorView);
      return;
    }
    pageCnt = 1;
    mAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecyclerView.getParent());
    mAdapter.setEnableLoadMore(false);

    mCall = LinkCallHelper.getApiService()
        .getActivityLists(pageCnt, _COUNT, cate, progress, mAccountManager.getToken(), mCity);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ActivityListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<ActivityListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          ((FrameLayout) mAdapter.getEmptyView()).removeAllViews();
          ActivityListResponse data = entity.data;
          pageCnt = data.page;
          ++pageCnt;
          _COUNT = data.perpage;
          setData(true, data.activities);
          //if (data.activities == null || data.activities.size() == 0) {
          //  mAdapter.setEmptyView(noDataView);
          //}
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
    mCall = LinkCallHelper.getApiService()
        .getActivityLists(pageCnt, _COUNT, cate, progress, mAccountManager.getToken(), mCity);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ActivityListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<ActivityListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          ActivityListResponse data = entity.data;
          pageCnt = data.page;
          ++pageCnt;
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
          //List<String> urls = new ArrayList<>();
          //for (ScrollPicBean item : list) {
          //  urls.add(item.pic);
          //}
          setTopBanner(list);
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
    super.onStop();
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
  }

  @Override public void OnStatusSelected(String item) {
    cate = item;
    refresh();
  }

  @Override public void OnTypeSelected(String item) {
    progress = item;
    refresh();
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void Event(BaseEvent event) {
    if (event instanceof LocationEvent) {
      TextView view = mTitleBar.getLeftCustomView().findViewById(R.id.tv_location);

      view.setText(((LocationEvent) event).getCityname());
      mCity = ((LocationEvent) event).getCityname();
    }
    //else if (event instanceof UpdateEvent && ((UpdateEvent) event).getPage() == 1) {
    //  refresh();
    //}
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case 0:
        if (null != data) {
          Bundle bundle = data.getBundleExtra(BaseActivity.PARAM_INTENT);
          String key = bundle.getString("key");
          TextView view = mTitleBar.getLeftCustomView().findViewById(R.id.tv_location);

          view.setText(key);
          mCity = key;
          refresh();
        }
        break;
    }
  }
}
