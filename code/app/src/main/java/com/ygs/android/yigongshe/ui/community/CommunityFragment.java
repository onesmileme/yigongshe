package com.ygs.android.yigongshe.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.CommunityItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.AttentionResponse;
import com.ygs.android.yigongshe.bean.response.CircleDeleteResponse;
import com.ygs.android.yigongshe.bean.response.CommunityListResponse;
import com.ygs.android.yigongshe.bean.response.ListLikeResponse;
import com.ygs.android.yigongshe.bean.response.UnAttentionResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.utils.AppUtils;
import com.ygs.android.yigongshe.utils.NetworkUtils;
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
  private static int _COUNT = 20; //每页条数
  private int pageCnt = 1;
  private CommunityListHeader mCommunityListHeader;
  private LinkCall<BaseResultDataInfo<CommunityListResponse>> mCall;
  private String mType;//type	类型，全部：为空或all; 城市：city; 社团：association, 关注的人：follow
  private static final String T_CITY = "city";
  private static final String T_ASSO = "association";
  private static final String T_FOLLOW = "follow";
  private String[] typeList = new String[] { "", T_ASSO, T_FOLLOW };
  private final int TOPIC_CITY_SELECT = 0;
  private final int PUBLISH_COMMUNITY = 1;
  private final int COMMUNITY_DETAIL = 2;
  private View errorView;
  private SparseArray<Integer> mAttentionList = new SparseArray();

  @Override protected void initView() {
    errorView =
        getLayoutInflater().inflate(R.layout.error_view, (ViewGroup) mRecyclerView.getParent(),
            false);

    errorView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        refresh();
      }
    });
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
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
    }
    mTitleBarTabView.setCurrentTab(0);

    mTitleBarTabView.addTabCheckListener(new TitleBarTabView.TabCheckListener() {
      @Override public void onTabChecked(int position) {
        if (position == mTitleBarTabView.getCurrentTabPos()) {
          mType = typeList[position];
          refresh();
        }
      }
    });
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
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Bundle bundle = new Bundle();
        CommunityItemBean itemBean = ((CommunityItemBean) adapter.getItem(position));
        bundle.putInt("pubcircle_id", itemBean.pubcircleid);
        if (mAttentionList.get(position) != null) {
          itemBean.is_follow = mAttentionList.get(position);
        }
        bundle.putSerializable("item", itemBean);
        goToOthersForResult(CommunityDetailActivity.class, bundle, COMMUNITY_DETAIL);
      }
    });
    mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override public void onItemChildClick(final BaseQuickAdapter adapter, final View view,
          final int position) {
        AccountManager accountManager = YGApplication.accountManager;
        if (TextUtils.isEmpty(accountManager.getToken())) {
          Toast.makeText(getActivity(), "没有登录", Toast.LENGTH_SHORT).show();
          return;
        }
        if (R.id.attention == view.getId()) {
          int isAttentioned;
          if (mAttentionList.get(position) != null) {
            isAttentioned = mAttentionList.get(position);
          } else {
            CommunityItemBean itemBean = (CommunityItemBean) adapter.getItem(position);
            isAttentioned = itemBean.is_follow;
            mAttentionList.put(position, isAttentioned);
          }

          //0未关注
          if (isAttentioned == 0) {
            LinkCall<BaseResultDataInfo<AttentionResponse>> attention =
                LinkCallHelper.getApiService()
                    .attentionUser(accountManager.getUserid(), accountManager.getToken());
            attention.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<AttentionResponse>>() {
              @Override public void onResponse(BaseResultDataInfo<AttentionResponse> entity,
                  Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == 2000) {
                  Toast.makeText(getActivity(), "关注成功", Toast.LENGTH_SHORT).show();
                  view.setBackgroundResource(R.drawable.bg_attention);
                } else {
                  Toast.makeText(getActivity(), entity.msg, Toast.LENGTH_SHORT).show();
                }
              }
            });
          } else {
            LinkCall<BaseResultDataInfo<UnAttentionResponse>> unattention =
                LinkCallHelper.getApiService()
                    .unAttentionUser(accountManager.getUserid(), accountManager.getToken());
            unattention.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UnAttentionResponse>>() {
              @Override public void onResponse(BaseResultDataInfo<UnAttentionResponse> entity,
                  Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == 2000) {
                  Toast.makeText(getActivity(), "取消关注成功", Toast.LENGTH_SHORT).show();
                  view.setBackgroundResource(R.drawable.bg_unattention);
                }
              }
            });
          }
        } else if (view.getId() == R.id.iv_markgood) {
          CommunityItemBean itemBean = (CommunityItemBean) adapter.getItem(position);
          if (itemBean.is_like == 0) {
            LinkCall<BaseResultDataInfo<ListLikeResponse>> like = LinkCallHelper.getApiService()
                .likeCircle(accountManager.getUserid(), accountManager.getToken());
            like.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ListLikeResponse>>() {
              @Override public void onResponse(BaseResultDataInfo<ListLikeResponse> entity,
                  Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == 2000) {
                  Toast.makeText(getActivity(), "点赞成功", Toast.LENGTH_SHORT).show();
                  view.setBackgroundResource(R.drawable.hasmarkgood);
                } else {
                  Toast.makeText(getActivity(), entity.msg, Toast.LENGTH_SHORT).show();
                }
              }
            });
          }
        } else if (view.getId() == R.id.delete) {
          CommunityItemBean itemBean = (CommunityItemBean) adapter.getItem(position);
          LinkCall<BaseResultDataInfo<CircleDeleteResponse>> deleteCircle =
              LinkCallHelper.getApiService()
                  .deleteCircle(itemBean.pubcircleid, accountManager.getToken());
          deleteCircle.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CircleDeleteResponse>>() {
            @Override public void onResponse(BaseResultDataInfo<CircleDeleteResponse> entity,
                Response<?> response, Throwable throwable) {
              super.onResponse(entity, response, throwable);
              if (entity != null) {
                if (entity.error == 2000) {
                  Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                  refresh();
                } else {
                  Toast.makeText(getActivity(), entity.msg, Toast.LENGTH_SHORT).show();
                }
              }
            }
          });
        }
      }
    });
  }

  @Override public int getLayoutResId() {
    return R.layout.fragment_community;
  }

  private void refresh() {
    if (!NetworkUtils.isConnected(getActivity())) {
      mAdapter.setEmptyView(errorView);
      return;
    }
    //mAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecyclerView.getParent());
    pageCnt = 1;
    mAdapter.setEnableLoadMore(false);
    mCall = LinkCallHelper.getApiService().getCommunityList(pageCnt, _COUNT, mType);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CommunityListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<CommunityListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          CommunityListResponse data = entity.data;
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
    mCall = LinkCallHelper.getApiService().getCommunityList(pageCnt, _COUNT, mType);
    mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CommunityListResponse>>() {
      @Override
      public void onResponse(BaseResultDataInfo<CommunityListResponse> entity, Response<?> response,
          Throwable throwable) {
        super.onResponse(entity, response, throwable);
        if (entity != null && entity.error == 2000) {
          CommunityListResponse data = entity.data;
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
      case COMMUNITY_DETAIL:
        refresh();
        break;
    }
  }

  @OnClick(R.id.publish) public void onBtnClicked() {
    goToOthersForResult(PublishCommunityActivity.class, null, PUBLISH_COMMUNITY);
  }
}