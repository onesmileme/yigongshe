package com.ygs.android.yigongshe.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.CommentItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.base.BaseResultInfo;
import com.ygs.android.yigongshe.bean.response.CommentDeleteResponse;
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
  protected int mType;

  private static int _COUNT = 20; //每页条数
  private int pageCnt = 1;
  protected @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  @BindView(R.id.swipeLayout) SwipeRefreshLayout mSwipeRefreshLayout;
  public @BindView(R.id.titleBar) CommonTitleBar mTitleBar;
  protected CommentAdapter mAdapter;
  private LinkCall<BaseResultDataInfo<CommentListResponse>> mCommentCall;
  private LinkCall<BaseResultInfo> mAddCommendCall;

  protected int mId; //newsId, activityId
  protected String mTitle;
  @BindView(R.id.errorview) protected LinearLayout mErrorView;
  @BindView(R.id.loadingview) LinearLayout mLoadingView;

  @BindView(R.id.input_comment) EditText mEditText;

  @Override protected void initIntent(Bundle bundle) {
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
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    initAdapter();
    addHeaderView();
    mSwipeRefreshLayout.setRefreshing(true);
    mSwipeRefreshLayout.setEnabled(false);
  }

  private void initAdapter() {
    mAdapter = new CommentAdapter();
    mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
      @Override public void onLoadMoreRequested() {
        requestCommentData(mType, false);
      }
    }, mRecyclerView);
    mRecyclerView.setAdapter(mAdapter);
    mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override public void onItemChildClick(final BaseQuickAdapter adapter, final View view,
          final int position) {
        AccountManager accountManager = YGApplication.accountManager;
        if (view.getId() == R.id.delete) {
          CommentItemBean itemBean = (CommentItemBean) adapter.getItem(position);
          LinkCall<BaseResultDataInfo<CommentDeleteResponse>> deleteComment =
              LinkCallHelper.getApiService()
                  .deleteMyComment(mId, accountManager.getToken(), itemBean.commentid);
          deleteComment.enqueue(
              new LinkCallbackAdapter<BaseResultDataInfo<CommentDeleteResponse>>() {
                @Override public void onResponse(BaseResultDataInfo<CommentDeleteResponse> entity,
                    Response<?> response, Throwable throwable) {
                  super.onResponse(entity, response, throwable);
                  if (entity != null) {
                    if (entity.error == 2000) {
                      Toast.makeText(BaseDetailActivity.this, "评论删除成功", Toast.LENGTH_SHORT).show();
                      refresh();
                    } else {
                      Toast.makeText(BaseDetailActivity.this, entity.msg, Toast.LENGTH_SHORT)
                          .show();
                    }
                  }
                }
              });
        }
      }
    });
  }

  //添加很多个headerview

  protected void addHeaderView() {
  }

  //获取评论
  protected void requestCommentData(int type, boolean isRefresh) {
    if (mCommentCall != null) {
      mCommentCall.cancel();
    }
    mType = type;
    if (isRefresh) {
      pageCnt = 1;
    }
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
          pageCnt = data.page;
          ++pageCnt;
          _COUNT = data.perpage;
          setData(true, data.list);
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
    mCommentCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CommentListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<CommentListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          CommentListResponse data = entity.data;
          pageCnt = data.page;
          ++pageCnt;
          setData(false, data.list);
        } else {
          mAdapter.loadMoreFail();
        }
      }
    });
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
      //true：加载完成，底部什么都不显示；false：loadmore的加载完成，底部显示没有更多数据
      mAdapter.loadMoreEnd(isRefresh);
    } else {
      //本地加载结束，底部什么都不显示
      mAdapter.loadMoreComplete();
    }
  }

  @Override protected void onStop() {
    if (mCommentCall != null) {
      mCommentCall.cancel();
    }
    super.onStop();
  }

  @OnClick(R.id.sendText) public void onSendBtnClicked() {
    String sendText = mEditText.getText().toString();
    switch (mType) {
      case TYPE_DYNAMIC:
        mAddCommendCall = LinkCallHelper.getApiService()
            .postNewsComment(mId, sendText, YGApplication.accountManager.getToken());
        break;
      case TYPE_ACTIVITY:
        mAddCommendCall = LinkCallHelper.getApiService()
            .postActivityComment(mId, sendText, YGApplication.accountManager.getToken());
        break;
      case TYPE_COMMUNITY:
        mAddCommendCall = LinkCallHelper.getApiService()
            .postCommunityComment(mId, sendText, YGApplication.accountManager.getToken());
        break;
      default:
        break;
    }

    postCommentData();
  }

  private void postCommentData() {
    mAddCommendCall.enqueue(new LinkCallbackAdapter<BaseResultInfo>() {
      @Override
      public void onResponse(BaseResultInfo entity, Response<?> response, Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          mEditText.getText().clear();
          InputMethodManager imm =
              (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
          // 隐藏软键盘
          imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
          requestCommentData(mType, true);
        } else {
          Toast.makeText(BaseDetailActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  protected void showLoading(boolean show) {
    mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
    if (isErrorShowing()) {
      showError(false);
    }
  }

  protected boolean isLoadShowing() {
    return (mLoadingView.getVisibility() == View.VISIBLE);
  }

  protected void showError(boolean show) {
    mErrorView.setVisibility(show ? View.VISIBLE : View.GONE);
    if (isLoadShowing()) {
      showLoading(false);
    }
  }

  protected boolean isErrorShowing() {
    return (mErrorView.getVisibility() == View.VISIBLE);
  }
}

