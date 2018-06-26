package com.ygs.android.yigongshe.ui.dynamic;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.CommentListResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.comment.CommentAdapter;
import java.util.List;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/25.
 */

public class DynamicDetailActivity extends BaseActivity {
  private static final int PAGE_SIZE = 1; //total page size
  private static final int _COUNT = 20; //每页条数
  private int pageCnt = 0;
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  @BindView(R.id.swipeLayout) SwipeRefreshLayout mSwipeRefreshLayout;
  @BindView(R.id.titlebar_text_title) TextView mTitle;
  @BindView(R.id.titlebar_right_btn) Button mShare;

  private CommentAdapter mAdapter;
  private LinkCall<BaseResultDataInfo<CommentListResponse>> mCommentCall;

  private int newsId;
  private String newsTitle;

  @Override protected void initIntent(Bundle bundle) {
    newsId = bundle.getInt("news_id");
    newsTitle = bundle.getString("news_title");
  }

  @Override protected void initView() {
    mTitle.setText(newsTitle);
    //mShare.setBackgroundResource(getResources().getDrawable(R.drawable.), null);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    initAdapter();
    mSwipeRefreshLayout.setEnabled(false);
    refresh();
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

  private void requestData() {

  }

  private void refresh() {
    mAdapter.setEnableLoadMore(false);
    mCommentCall = LinkCallHelper.getApiService().getDynamicCommentLists(newsId, pageCnt, _COUNT);
    mCommentCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CommentListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<CommentListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          CommentListResponse data = entity.data;
          pageCnt++;
          setData(true, data.list);
          mAdapter.setEnableLoadMore(true);
          mSwipeRefreshLayout.setRefreshing(false);
        }
      }
    });
  }

  private void loadMore() {
    mCommentCall = LinkCallHelper.getApiService().getDynamicCommentLists(newsId, pageCnt, _COUNT);
    mCommentCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CommentListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<CommentListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          CommentListResponse data = entity.data;
          pageCnt++;
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

  @Override protected int getLayoutResId() {
    return R.layout.activity_dynamic_detail;
  }

  @OnClick(R.id.titlebar_right_btn) public void onShare() {

  }
}
