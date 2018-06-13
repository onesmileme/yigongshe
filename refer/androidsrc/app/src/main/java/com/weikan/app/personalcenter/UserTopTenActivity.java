package com.weikan.app.personalcenter;

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
import com.weikan.app.original.OriginalDetailActivity;
import com.weikan.app.original.adapter.OriginalMainAdapter;
import com.weikan.app.original.bean.OriginalItem;
import platform.http.HttpUtils;

import com.weikan.app.original.bean.OriginalItemData;
import com.weikan.app.util.URLDefine;
import platform.http.responsehandler.JsonResponseHandler;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 某用户荣登十大的列表
 *
 * @author kailun on 16/3/27
 */
public class UserTopTenActivity extends BasePullToRefreshActivity {

    @NonNull
    private String otherUid = "";
    @NonNull
    private String otherNickname = "";

    private OriginalMainAdapter adapter;
    private ArrayList<OriginalItem> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // nickname必须在前面取
        otherUid = getIntent().getExtras().getString(Constants.OTHER_UID, "");
        otherNickname = getIntent().getExtras().getString(Constants.OTHER_NICKNAME, "");

        super.onCreate(savedInstanceState);

        adapter = new OriginalMainAdapter(this);
        adapter.setContent(dataList);
        PullToRefreshListView pullRefreshListView = getPullRefreshListView();
        pullRefreshListView.setAdapter(adapter);

        adapter.setShowTags(false);

        pullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(UserTopTenActivity.this,
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
                String label = DateUtils.formatDateTime(UserTopTenActivity.this,
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
                        intent.setClass(UserTopTenActivity.this, OriginalDetailActivity.class);
                        intent.putExtra("tid", tid);
                        startActivityForResult(intent, Constants.ARTICLE_DETAIL_REQUEST_CODE);

                    }
                });

        sendNewRequest(-1);
    }

    @Override
    protected String getTitleText() {
        return otherNickname + "的十大";
    }

    @Override
    protected BaseAdapter getAdapter() {
        return adapter;
    }

    private void sendNewRequest(long ctime) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.TWEET_USER_TOP);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEW);
        if (ctime != -1) {
            params.put("first_ctime", String.valueOf(ctime));
        }

        params.put(URLDefine.OTHER_UID, otherUid);
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
        builder.encodedPath(URLDefine.TWEET_USER_TOP);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put(URLDefine.OTHER_UID, otherUid);
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
