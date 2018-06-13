package com.weikan.app.base;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.weikan.app.R;

/**
 * Created by liujian on 16/2/17.
 */
public abstract class BasePullToRefreshActivity extends BaseActivity {

    protected PullToRefreshListView mPullRefreshListView;

    public static final String TYPE_NEW = "new";
    public static final String TYPE_APPEND = "append";
    public static final String TYPE_NEXT = "next";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_pull_refresh);
        initTitleBar();
        initView();
    }

    protected abstract String getTitleText();

    protected abstract BaseAdapter getAdapter();

    protected View makeHeadView(){return null;}

    protected View makeFooterView(){return null;}

    protected void onPullDown(){}

    protected void onPullUp(){}

    protected PullToRefreshListView getPullRefreshListView(){
        return mPullRefreshListView;
    }

    private void initTitleBar() {
        TextView titleText = (TextView) findViewById(R.id.tv_titlebar_title);
        titleText.setText(getTitleText());
        findViewById(R.id.iv_titlebar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void setTitleText(String title){
        TextView titleText = (TextView) findViewById(R.id.tv_titlebar_title);
        titleText.setText(title);
    }

    private void initView() {
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.base_pull_list_view);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(BasePullToRefreshActivity.this,
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
                String label = DateUtils.formatDateTime(BasePullToRefreshActivity.this,
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                onPullUp();
            }
        });

        ListView actualListView = mPullRefreshListView.getRefreshableView();
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        actualListView.setDivider(null);
        actualListView.setDividerHeight(0);
        View headview = makeHeadView();
        if(headview!=null) {
            actualListView.addHeaderView(headview);
        }
        View footer = makeFooterView();
        if(footer!=null) {
            actualListView.addFooterView(footer);
        }
        actualListView.setAdapter(getAdapter());

    }

    /**
     * 强制下拉刷新
     */
    public void forceRefresh(){
        getPullRefreshListView().setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        getPullRefreshListView().setRefreshing();
        getPullRefreshListView().setMode(PullToRefreshBase.Mode.BOTH);
    }
}