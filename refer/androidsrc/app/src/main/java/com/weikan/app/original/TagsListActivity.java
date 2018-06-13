package com.weikan.app.original;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jakewharton.rxbinding.widget.AdapterViewItemClickEvent;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.weikan.app.Constants;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.original.adapter.OriginalMainAdapter;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.original.bean.OriginalItemData;
import com.weikan.app.util.URLDefine;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/3/20
 */
public class TagsListActivity extends BasePullToRefreshActivity {

    public static final String BUNDLE_TAG = "tag";

    @NonNull
    private String tag = "";

    private OriginalMainAdapter adapter;
    private ArrayList<OriginalItem> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tag = getIntent().getExtras().getString(BUNDLE_TAG, "");

        setTitleText("#" + tag + "#");

        adapter = new OriginalMainAdapter(this);
        adapter.setContent(dataList);
        PullToRefreshListView pullRefreshListView = getPullRefreshListView();
        pullRefreshListView.setAdapter(adapter);

        adapter.setShowTags(false);

        pullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(TagsListActivity.this,
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                sendNewRequest(-1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(TagsListActivity.this,
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);

                if (dataList.size() != 0) {
                    OriginalItem item = dataList.get(dataList.size() - 1);
                    sendNextRequest(item.ctime);
                }
            }
        });

        RxAdapterView.itemClickEvents(pullRefreshListView.getRefreshableView())
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<AdapterViewItemClickEvent>() {
                    @Override
                    public void call(AdapterViewItemClickEvent adapterViewItemClickEvent) {
                        OriginalItem originalObject = dataList.get(adapterViewItemClickEvent.position() - 1);
                        if (originalObject == null) {
                            return;
                        }
                        String tid = originalObject.tid;
                        if (TextUtils.isEmpty(tid)) {
                            return;
                        }
                        Intent intent = new Intent();
                        intent.setClass(TagsListActivity.this, OriginalDetailActivity.class);
                        intent.putExtra("tid", tid);
                        startActivityForResult(intent, Constants.ARTICLE_DETAIL_REQUEST_CODE);

                    }
                });

        sendNewRequest(-1);
    }

    @Override
    protected String getTitleText() {
        return "";
    }

    @Override
    protected BaseAdapter getAdapter() {
        return adapter;
    }

    private void sendNewRequest(long ctime) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.TWEET_TAGLIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEW);
        params.put("tag", tag);
        if (ctime != -1) {
            params.put("first_ctime", String.valueOf(ctime));
        }
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<OriginalItemData>() {
            @Override
            public void success(@NonNull OriginalItemData data) {
                dataList.clear();
                dataList.addAll(data.content);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                getPullRefreshListView().onRefreshComplete();
            }
        });
    }

    private void sendNextRequest(long ctime) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.TWEET_TAGLIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put("tag", tag);
        params.put("last_ctime", String.valueOf(ctime));

        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<OriginalItemData>() {
            @Override
            public void success(@NonNull OriginalItemData data) {
                dataList.addAll(data.content);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                getPullRefreshListView().onRefreshComplete();
            }
        });
    }
}
