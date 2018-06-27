package com.ygs.android.yigongshe.ui.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.webkit.WebView;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.CommentListResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.comment.CommentAdapter;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import java.util.List;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/27.
 */

public abstract class BaseDetailActivity extends BaseActivity {
  protected static final int TYPE_DYNAMIC = 1;
  protected static final int TYPE_ACTIVITY = 2;
  protected static final int TYPE_COMMUNITY = 3;
  private static final int PAGE_SIZE = 1; //total page size
  private static final int _COUNT = 20; //每页条数
  private int pageCnt = 0;
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  @BindView(R.id.swipeLayout) SwipeRefreshLayout mSwipeRefreshLayout;
  public @BindView(R.id.titleBar) CommonTitleBar mTitleBar;

  private CommentAdapter mAdapter;
  private LinkCall<BaseResultDataInfo<CommentListResponse>> mCommentCall;

  protected int mId; //newsId, activityId
  protected String mTitle;
  private WebView mWebView;

  @Override protected void initIntent(Bundle bundle) {
  }

  @Override protected void initView() {
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    initAdapter();
    addHeaderView();
    mSwipeRefreshLayout.setEnabled(false);
  }

  private void initAdapter() {
    mAdapter = new CommentAdapter();
    mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
      @Override public void onLoadMoreRequested() {
        loadMore();
      }
    }, mRecyclerView);
    mRecyclerView.setAdapter(mAdapter);
  }

  //添加很多个headerview
  protected void addHeaderView() {
    mWebView = new WebView(this);
    mAdapter.addHeaderView(mWebView);
  }

  protected void requestData(int type, boolean isRefresh) {
    switch (type) {
      case TYPE_DYNAMIC:
        mCommentCall = LinkCallHelper.getApiService().getDynamicCommentLists(mId, pageCnt, _COUNT);
        break;
      case TYPE_ACTIVITY:
        mCommentCall = LinkCallHelper.getApiService().getActivityCommentLists(mId, pageCnt, _COUNT);
        break;
      case TYPE_COMMUNITY:
        mCommentCall =
            LinkCallHelper.getApiService().getCommunityCommentLists(mId, pageCnt, _COUNT);
        break;
      default:
        break;
    }
    if (isRefresh) {
      refresh();
    } else {
      loadMore();
    }
  }

  private void refresh() {
    mAdapter.setEnableLoadMore(false);
    mCommentCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CommentListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<CommentListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          CommentListResponse data = entity.data;
          setData(true, data.list);
          mAdapter.setEnableLoadMore(true);
          mSwipeRefreshLayout.setRefreshing(false);
        }
      }
    });
  }

  private void loadMore() {
    mCommentCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CommentListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<CommentListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          CommentListResponse data = entity.data;
          setData(false, data.list);
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
    if (size < PAGE_SIZE) {
      //第一页如果不够一页就不显示没有更多数据布局
      mAdapter.loadMoreEnd(isRefresh);
    } else {
      mAdapter.loadMoreComplete();
    }
  }
}
