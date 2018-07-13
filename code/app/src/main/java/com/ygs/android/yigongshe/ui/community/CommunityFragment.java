package com.ygs.android.yigongshe.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.OnClick;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.CommunityItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.CommunityListResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.utils.AppUtils;
import com.ygs.android.yigongshe.view.CommunityListHeader;
import com.ygs.android.yigongshe.view.TitleBarTabView;
import java.util.List;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/13.
 */

public class CommunityFragment extends BaseFragment {
  @BindView(R.id.titleBarTabView) TitleBarTabView mTitleBarTabView;
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  @BindView(R.id.swipeLayout) SwipeRefreshLayout mSwipeRefreshLayout;
  private CommunityAdapter mAdapter;
  private static int PAGE_SIZE = 1;
  private static int _COUNT = 20; //每页条数
  private int pageCnt = 0;
  private CommunityListHeader mCommunityListHeader;
  private LinkCall<BaseResultDataInfo<CommunityListResponse>> mCall;
  private String mType;//type	类型，全部：为空或all; 城市：city; 社团：association, 关注的人：follow
  private static final String T_CITY = "city";
  private static final String T_ASSO = "association";
  private static final String T_FOLLOW = "follow";
  private String[] typeList = new String[] { "", T_ASSO, T_FOLLOW };
  private final int TOPIC_CITY_SELECT = 0;
  private final int PUBLISH_COMMUNITY = 1;

  @Override protected void initView() {
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    initAdapter();
    initTitleBarTabView();
    addHeadView();
    mSwipeRefreshLayout.setRefreshing(true);
    mSwipeRefreshLayout.setEnabled(false);
    refresh();
  }

  private void initTitleBarTabView() {
    int statusBarHeight = AppUtils.getStatusBarHeight(getActivity());
    LinearLayout.LayoutParams params =
        (LinearLayout.LayoutParams) mTitleBarTabView.getLayoutParams();
    params.setMargins(0, statusBarHeight, 0, 0);
    mTitleBarTabView.setLayoutParams(params);
    String[] tabs = getResources().getStringArray(R.array.tab_view);
    for (int i = 0; i < tabs.length; i++) {
      mTitleBarTabView.addTab(tabs[i], i);
      mTitleBarTabView.addTabCheckListener(new TitleBarTabView.TabCheckListener() {
        @Override public void onTabChecked(int var1) {
          if (var1 == mTitleBarTabView.getCurrentTabPos()) {
            mType = typeList[var1];
            refresh();
          }
        }
      });
    }
    mTitleBarTabView.setCurrentTab(0);
  }

  private void addHeadView() {
    mCommunityListHeader = new CommunityListHeader(getActivity(), mRecyclerView,
        new CommunityListHeader.SelectBtnListener() {

          @Override public void onBtnSelected(int id) {
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            Intent intent = null;

            if (id == 0) {
              intent = new Intent(getActivity(), TopicSelectActivity.class);
            } else {
              intent = new Intent(getActivity(), CitySelectActivity.class);
            }

            intent.putExtras(bundle);
            startActivityForResult(intent, TOPIC_CITY_SELECT);
          }
        });
    mAdapter.addHeaderView(mCommunityListHeader.getView());
  }

  private void initAdapter() {
    mAdapter = new CommunityAdapter();
    mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
      @Override public void onLoadMoreRequested() {
        loadMore();
      }
    }, mRecyclerView);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
      @Override public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
        Bundle bundle = new Bundle();
        CommunityItemBean itemBean = ((CommunityItemBean) adapter.getItem(position));
        bundle.putInt("pubcircle_id", itemBean.pubcircleid);
        bundle.putSerializable("item", itemBean);
        goToOthers(CommunityDetailActivity.class, bundle);
      }
    });
  }

  @Override public int getLayoutResId() {
    return R.layout.fragment_community;
  }

  private void refresh() {
    pageCnt = 0;
    mAdapter.setEnableLoadMore(false);
    mCall = LinkCallHelper.getApiService().getCommunityList(pageCnt, _COUNT, mType);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CommunityListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<CommunityListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          CommunityListResponse data = entity.data;
          PAGE_SIZE = data.page;
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
    mCall = LinkCallHelper.getApiService().getCommunityList(pageCnt, _COUNT, mType);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CommunityListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<CommunityListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          CommunityListResponse data = entity.data;
          setData(false, data.list);
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

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case TOPIC_CITY_SELECT:
        if (null != data) {
          Bundle bundle = data.getBundleExtra(BaseActivity.PARAM_INTENT);
          mCommunityListHeader.setViewData(bundle.getInt("id"), bundle.getString("key"));
        }
        break;
      case PUBLISH_COMMUNITY:
        refresh();
        break;
    }
  }

  @OnClick(R.id.publish) public void onBtnClicked() {
    goToOthersForResult(PublishCommunityActivity.class, null, PUBLISH_COMMUNITY);
  }
}