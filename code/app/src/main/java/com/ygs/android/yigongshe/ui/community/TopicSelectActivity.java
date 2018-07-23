package com.ygs.android.yigongshe.ui.community;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.TopicListResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.base.BaseDetailActivity;
import com.ygs.android.yigongshe.utils.NetworkUtils;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import java.util.List;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/27.
 */

public class TopicSelectActivity extends BaseActivity {
  @BindView(R.id.titleBar) CommonTitleBar mTitleBar;
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  private String mSelected;//被选中的字串
  private int mSelectedPos;//被选中的位置
  private int mId;//话题/城市 0,1
  private LinkCall<BaseResultDataInfo<TopicListResponse>> mCall;
  private TopicSelectAdapter mAdapter;
  private View errorView;

  @Override protected void initIntent(Bundle bundle) {
    mId = bundle.getInt("id");
  }

  @Override protected void initView() {
    errorView =
        getLayoutInflater().inflate(R.layout.error_view, (ViewGroup) mRecyclerView.getParent(),
            false);

    errorView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        refresh();
      }
    });
    mTitleBar.getCenterTextView().setText("话题选择");
    mTitleBar.getRightTextView().setText(getResources().getString(R.string.done));
    mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_LEFT_BUTTON
            || action == CommonTitleBar.ACTION_RIGHT_TEXT) {
          Bundle bundle = new Bundle();
          bundle.putString("key", mSelected);
          bundle.putInt("id", mId);
          backForResult(BaseDetailActivity.class, bundle, 100);
          finish();
        }
      }
    });
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    initAdapter();
    refresh();
  }

  private void initAdapter() {
    //final List<String> data =
    //    Arrays.asList(getResources().getStringArray(R.array.community_topic_select_list));
    mAdapter = new TopicSelectAdapter();
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
      @Override public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
        ((TextView) adapter.getViewByPosition(mRecyclerView, mSelectedPos,
            R.id.tv_topic)).setTextColor(getResources().getColor(R.color.gray1));
        mSelectedPos = position;
        ((TextView) adapter.getViewByPosition(mRecyclerView, mSelectedPos,
            R.id.tv_topic)).setTextColor(getResources().getColor(R.color.green));
        String tmp = (String) adapter.getItem(position);
        //if (mId == 0 || mId == 1) {
        //  mSelected = tmp.substring(1, tmp.length() - 1);
        //} else {
        mSelected = tmp;
        //}
      }
    });
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_topic_select;
  }

  private void refresh() {
    if (!NetworkUtils.isConnected(this)) {
      mAdapter.setEmptyView(errorView);
      return;
    }
    mAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecyclerView.getParent());
    mAdapter.setEnableLoadMore(false);
    mCall = LinkCallHelper.getApiService().getTopicList();
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<TopicListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<TopicListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null) {
          if (entity.error == 2000) {
            TopicListResponse data = entity.data;
            setData(true, data.topics);
          } else {
            Toast.makeText(TopicSelectActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
          }
        }
      }
    });
  }

  private void setData(boolean isRefresh, List data) {
    final int size = data == null ? 0 : data.size();
    data.add(0, "全部");
    if (isRefresh) {
      mAdapter.setNewData(data);
    } else {
      if (size > 0) {
        mAdapter.addData(data);
      }
    }
    mAdapter.loadMoreEnd(isRefresh);
  }
}
