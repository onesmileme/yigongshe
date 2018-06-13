package com.weikan.app.news;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.weikan.app.Constants;
import com.weikan.app.MainApplication;
import com.weikan.app.R;
import com.weikan.app.base.BaseFragment;
import com.weikan.app.bean.ClearRedMsgEvent;
import com.weikan.app.common.Model.RedNoticeModel;
import com.weikan.app.common.adater.BaseListAdapter;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.news.adapter.NewsListAdapter;
import com.weikan.app.news.bean.BannerContent;
import com.weikan.app.news.widget.BannerView;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.original.bean.OriginalItemData;
import com.weikan.app.util.DensityUtil;
import com.weikan.app.util.JumpUtil;

import java.util.List;

import de.greenrobot.event.EventBus;
import platform.http.responsehandler.JsonResponseHandler;


/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/29
 */
public class NewsFragment extends BaseFragment {

    // 新闻列表的二级tab id
    public static final String NEWS_SUB_ID = "news_sub_id";
    // 新闻列表的二级tab name
    public static final String NEWS_SUB_NAME = "news_sub_name";

    private int typeId;
    private String typeName = "";

    PullToRefreshListView listView;
    Pair<String, Integer> paramPair;
    NewsListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        typeId = getArguments().getInt(NEWS_SUB_ID);
        typeName = getArguments().getString(NEWS_SUB_NAME);

        View view = inflater.inflate(R.layout.fragment_news, null);
        initViews(view);

        onPullDown(); // 执行一次下拉刷新
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected void initViews(@NonNull View view) {
        adapter = new NewsListAdapter(getActivity());
        adapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener<OriginalItem>() {
            @Override
            public void onItemClick(BaseListItemView<OriginalItem> itemView) {
                onListViewItemClick(itemView);
            }
        });

        listView = (PullToRefreshListView) view.findViewById(R.id.lv);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                onPullDown();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                onPullUp();
            }
        });

        ListView actualListView = listView.getRefreshableView();
        listView.setMode(PullToRefreshBase.Mode.BOTH);

        actualListView.setDivider(new ColorDrawable(MainApplication.getInstance().getResources().getColor(R.color.gray)));
        actualListView.setDividerHeight(DensityUtil.dip2px(getContext(),8));

        listView.setAdapter(adapter);
    }

    public void onListViewItemClick(BaseListItemView<OriginalItem> itemView) {
        OriginalItem item = itemView.get();
        if (item == null) {
            return;
        }

        if (itemView instanceof BannerView) {
            BannerContent currentBannerContent = ((BannerView) itemView).getCurrentBannerContent();
            if (currentBannerContent != null) {
                if (!TextUtils.isEmpty(currentBannerContent.schema)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("requestCode", Constants.ARTICLE_DETAIL_REQUEST_CODE);
                    JumpUtil.executeSchema(currentBannerContent.schema, getContext(), bundle);
                }
            }
        } else {
            if (!TextUtils.isEmpty(item.schema)) {
                Bundle bundle = new Bundle();
                bundle.putInt("requestCode", Constants.ARTICLE_DETAIL_REQUEST_CODE);
                JumpUtil.executeSchema(item.schema, getContext(),bundle);
            }
        }
    }

    public void setParamPair(Pair<String, Integer> pair) {
        this.paramPair = pair;
    }

    /**
     * 以下两个方法时友盟统计相关
     */
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    private void onPullDown() {
        getFeedList();
    }

    private void onPullUp() {
        List<OriginalItem> items = adapter.getItems();
        long lastUpdateTime = 0;
        if (items.size() != 0) {
            lastUpdateTime = items.get(items.size() - 1).lastTime;
        }
        getFeedListForAppend(lastUpdateTime);
    }

    public void refreshData(){
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setRefreshing();
        listView.setMode(PullToRefreshBase.Mode.BOTH);
    }

    public void getFeedList() {
        NewsAgent.getFeedList(typeId, 0, 10, 0, new JsonResponseHandler<OriginalItemData>() {
            @Override
            public void success(@NonNull OriginalItemData data) {

                adapter.setItems(data.content);
                adapter.notifyDataSetChanged();

                RedNoticeModel.saveTweetRefreshTime();
                EventBus.getDefault().post(new ClearRedMsgEvent(ClearRedMsgEvent.CLEAR_TWEET));
            }

            @Override
            public void end() {
                super.end();
                if (listView != null) {
                    listView.onRefreshComplete();
                }
            }

        });
    }

    public void getFeedListForAppend(long lastTime) {
        NewsAgent.getFeedList(typeId, lastTime, 10, 1, new JsonResponseHandler<OriginalItemData>() {
            @Override
            public void success(@NonNull OriginalItemData data) {

                adapter.addItems(data.content);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                super.end();
                if (listView != null) {
                    listView.onRefreshComplete();
                }
            }

        });
    }
}
