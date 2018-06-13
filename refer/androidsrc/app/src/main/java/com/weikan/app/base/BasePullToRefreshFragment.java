package com.weikan.app.base;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.weikan.app.R;

/**
 * Created by liujian on 16/2/19.
 */
public abstract class BasePullToRefreshFragment extends BaseFragment {

    protected PullToRefreshListView mPullRefreshListView;
    protected ListView actualListView;

    public static final String TYPE_NEW = "new";
    public static final String TYPE_APPEND = "append";
    public static final String TYPE_NEXT = "next";

    protected View headview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_base_pull_refresh, null);
//        mPullRefreshListView = new PullToRefreshListView(getActivity());
        initView(contentView);
        return contentView;
    }

    protected abstract BaseAdapter getAdapter();

    protected View makeHeadView(){return null;}

    protected void onPullDown(){}

    protected void onPullUp(){}

    protected PullToRefreshListView getPullRefreshListView(){
        return mPullRefreshListView;
    }

    protected void initView(View view) {
        view.findViewById(R.id.base_pull_title).setVisibility(View.GONE);
        mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.base_pull_list_view);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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

        actualListView = mPullRefreshListView.getRefreshableView();
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        actualListView.setDivider(null);
        actualListView.setDividerHeight(0);
        headview = makeHeadView();
        if(headview!=null) {
            actualListView.addHeaderView(headview);
        }
        actualListView.setAdapter(getAdapter());

    }
}
