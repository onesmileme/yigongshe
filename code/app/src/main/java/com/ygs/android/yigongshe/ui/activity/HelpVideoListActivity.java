package com.ygs.android.yigongshe.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.HelpVideoItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.HelpVideoListResponse;
import com.ygs.android.yigongshe.bean.response.HelpVideoResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.utils.VideoUtils;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import com.ygs.android.yigongshe.view.MyDecoration;
import java.io.File;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/7/7.
 */

public class HelpVideoListActivity extends BaseActivity {
  private static int _COUNT = 20; //每页条数
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  @BindView(R.id.swipeLayout) SwipeRefreshLayout mSwipeRefreshLayout;
  @BindView(R.id.titleBar) CommonTitleBar mTitleBar;
  private int pageCnt = 1; //第几页
  private HelpVideoListAdapter mAdapter;
  private LinkCall<BaseResultDataInfo<HelpVideoListResponse>> mCall;
  private int mActivityId;
  private final int REQUEST_VIDEO_CAPTURE = 0;
  private String mToken;

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
        } else if (action == CommonTitleBar.ACTION_RIGHT_BUTTON) {
          AccountManager accountManager = YGApplication.accountManager;
          mToken = accountManager.getToken();
          if (TextUtils.isEmpty(mToken)) {
            Toast.makeText(HelpVideoListActivity.this, "没有登录", Toast.LENGTH_SHORT).show();
            return;
          }
          Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
          if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
          }
        }
      }
    });
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.addItemDecoration(
        new MyDecoration(this, MyDecoration.VERTICAL_LIST, R.drawable.divider));
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
    pageCnt = 1;
    mAdapter.setEnableLoadMore(false);
    mCall = LinkCallHelper.getApiService().getHelpVideoList(pageCnt, _COUNT, mActivityId);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<HelpVideoListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<HelpVideoListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          HelpVideoListResponse data = entity.data;
          pageCnt = data.page;
          ++pageCnt;
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
          pageCnt = data.page;
          ++pageCnt;
          setData(false, data.video_list);
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

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_VIDEO_CAPTURE) {
      Uri videoUri = data.getData();
      String path = VideoUtils.getPath(this, videoUri);
      File file = new File(path);
      RequestBody requestFile =
          RequestBody.create(MediaType.parse("application/octet-stream"), file);

      // MultipartBody.Part  和后端约定好Key，这里的partName是用image
      //MultipartBody.Part body =
      //    MultipartBody.Part.createFormData("video_path", file.getName(), requestFile);
      MultipartBody.Part body1 = MultipartBody.Part.create(requestFile);
      // 添加描述
      String descriptionString = "hello, 这是文件描述";
      RequestBody description =
          RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
      LinkCall<BaseResultDataInfo<HelpVideoResponse>> upload =
          LinkCallHelper.getApiService().uploadHelpVideo(description, body1, mActivityId, mToken);
      upload.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<HelpVideoResponse>>() {
        @Override public void onResponse(final BaseResultDataInfo<HelpVideoResponse> entity,
            Response<?> response, Throwable throwable) {
          if (entity != null && entity.error == 2000) {
            refresh();
          } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
              @Override public void run() {
                Toast.makeText(HelpVideoListActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
              }
            }, 2000);
          }
        }
      });
    }
  }
}
