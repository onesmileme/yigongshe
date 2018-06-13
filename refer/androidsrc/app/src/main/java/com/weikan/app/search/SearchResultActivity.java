package com.weikan.app.search;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jakewharton.rxbinding.widget.AdapterViewItemClickEvent;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.weikan.app.Constants;
import com.weikan.app.MainApplication;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.original.OriginalDetailActivity;
import com.weikan.app.original.adapter.OriginalMainAdapter;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.original.bean.OriginalItemData;
import com.weikan.app.util.DensityUtil;
import com.weikan.app.util.URLDefine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;
import platform.http.responsehandler.JsonResponseHandler;
import rx.functions.Action1;

/**
 * 搜索结果页
 *
 *
 */
public class SearchResultActivity extends BasePullToRefreshActivity {

    @NonNull
    private String searchkey = "";

    private String searchFilter = "";
    private String title = "";

    private OriginalMainAdapter adapter;
    private ArrayList<OriginalItem> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.setDivider(new ColorDrawable(MainApplication.getInstance().getResources().getColor(R.color.gray)));
        actualListView.setDividerHeight(DensityUtil.dip2px(SearchResultActivity.this, 8));

        searchkey = getIntent().getExtras().getString(Constants.SEARCH_KEY, "");
        searchFilter = getIntent().getExtras().getString(Constants.SEARCH_FILTER, "");
        title = getIntent().getExtras().getString(Constants.TITLE, "");

        setTitleText(title);

        adapter = new OriginalMainAdapter(this);
        adapter.setContent(dataList);
        PullToRefreshListView pullRefreshListView = getPullRefreshListView();
        pullRefreshListView.setAdapter(adapter);

        adapter.setShowTags(false);

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
                        intent.setClass(SearchResultActivity.this, OriginalDetailActivity.class);
                        intent.putExtra("tid", tid);
                        startActivityForResult(intent, Constants.ARTICLE_DETAIL_REQUEST_CODE);

                    }
                });

        onPullDown();
    }

    @Override
    protected String getTitleText() {
        return "";
    }

    @Override
    protected BaseAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void onPullDown() {
        super.onPullDown();
        if(!TextUtils.isEmpty(searchkey)) {
            sendNewRequest(-1);
        } else if(!TextUtils.isEmpty(searchFilter)){
            sendNewFilterRequest();
        }
    }

    @Override
    protected void onPullUp() {
        super.onPullUp();
        if (dataList.size() != 0) {
            OriginalItem item = dataList.get(dataList.size() - 1);
            if(!TextUtils.isEmpty(searchkey)) {
                sendNextRequest(item.pubtime);
            } else if(!TextUtils.isEmpty(searchFilter)){
                sendNextFilterRequest(item.pubtime);
            }
        }
    }

    private void sendNewRequest(long ctime) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.TWEET_SEARCH);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEW);
        params.put("word", searchkey);
        if (ctime != -1) {
            params.put("first_ctime", String.valueOf(ctime));
        }
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        HttpUtils.get(builder.build().toString(), params, new AmbJsonResponseHandler<OriginalItemData>() {
            @Override
            public void success(@Nullable OriginalItemData data) {

                dataList.clear();
                if(data!=null) {
                    dataList.addAll(data.content);
                }
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
        builder.encodedPath(URLDefine.TWEET_SEARCH);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put("word", searchkey);
        params.put("last_ctime", String.valueOf(ctime));

        HttpUtils.get(builder.build().toString(), params, new AmbJsonResponseHandler<OriginalItemData>() {
            @Override
            public void success(@Nullable OriginalItemData data) {
                if(data!=null) {
                    dataList.addAll(data.content);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                getPullRefreshListView().onRefreshComplete();
            }
        });
    }

    private void sendNewFilterRequest() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.TWEET_MATERIAL);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEW);

        params.put("filter_type", searchFilter);

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

    private void sendNextFilterRequest(long ctime) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.TWEET_MATERIAL);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put("filter_type", searchFilter);
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
