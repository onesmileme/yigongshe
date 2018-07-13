package com.ygs.android.yigongshe.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.HelpVideoItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.HelpVideoListResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import java.util.List;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/7/7.
 */

public class HelpVideoListActivity extends BaseActivity {
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  @BindView(R.id.swipeLayout) SwipeRefreshLayout mSwipeRefreshLayout;
  @BindView(R.id.titleBar) CommonTitleBar mTitleBar;
  private static int PAGE_SIZE = 1;
  private static int _COUNT = 20; //每页条数
  private int pageCnt = 0;
  private HelpVideoListAdapter mAdapter;
  private LinkCall<BaseResultDataInfo<HelpVideoListResponse>> mCall;
  private int mActivityId;

  protected boolean openTranslucentStatus() {
    return true;
  }

  @Override protected void initIntent(Bundle bundle) {
    mActivityId = bundle.getInt("activity_id");
  }

  @Override protected void initView() {
    mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
          finish();
        }
      }
    });
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    initAdapter();
    mSwipeRefreshLayout.setRefreshing(true);
    mSwipeRefreshLayout.setEnabled(false);
    refresh();
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_help_video_list;
  }

  private void initAdapter() {
    mAdapter = new HelpVideoListAdapter();
    mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
      @Override public void onLoadMoreRequested() {
        loadMore();
      }
    }, mRecyclerView);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
      @Override public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
        Bundle bundle = new Bundle();
        HelpVideoItemBean itemBean = ((HelpVideoItemBean) adapter.getItem(position));
        bundle.putString("src", itemBean.src);
        goToOthers(HelpVideoDetailActivity.class, bundle);
      }
    });
  }

  private void refresh() {
    pageCnt = 0;
    mAdapter.setEnableLoadMore(false);
    mCall = LinkCallHelper.getApiService().getHelpVideoList(pageCnt, _COUNT, mActivityId);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<HelpVideoListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<HelpVideoListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          HelpVideoListResponse data = entity.data;
          PAGE_SIZE = data.page;
          _COUNT = data.perpage;
          setData(true, data.video_list);
          mAdapter.setEnableLoadMore(true);
          mSwipeRefreshLayout.setRefreshing(false);
        } else {
          mAdapter.setEnableLoadMore(true);
          mSwipeRefreshLayout.setRefreshing(false);
          Toast.makeText(HelpVideoListActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private void loadMore() {
    mCall = LinkCallHelper.getApiService().getHelpVideoList(pageCnt, _COUNT, mActivityId);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<HelpVideoListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<HelpVideoListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          HelpVideoListResponse data = entity.data;
          setData(false, data.video_list);
        } else {
          mAdapter.loadMoreFail();
        }
      }
    });
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

  @Override protected void onStop() {
    if (mCall != null) {
      mCall.cancel();
    }
    super.onStop();
  }
}
