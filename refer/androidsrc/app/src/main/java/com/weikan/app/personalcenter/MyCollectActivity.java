package com.weikan.app.personalcenter;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.weikan.app.MainApplication;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.common.adater.BaseListAdapter;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.news.adapter.NewsListAdapter;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.personalcenter.bean.MyCollectListObject;
import com.weikan.app.util.DensityUtil;
import com.weikan.app.util.JumpUtil;
import com.weikan.app.util.URLDefine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;

/**
 * Created by ylp on 2016/11/29.
 */

public class MyCollectActivity extends BasePullToRefreshActivity {
    private NewsListAdapter newsListAdapter;
    private ArrayList<OriginalItem> mDataList = new ArrayList<>();
    private OriginalItem info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.setDivider(new ColorDrawable(MainApplication.getInstance().getResources().getColor(R.color.gray)));
        actualListView.setDividerHeight(DensityUtil.dip2px(MyCollectActivity.this, 8));
        getPullRefreshListView().setMode(PullToRefreshBase.Mode.BOTH);
        getPullRefreshListView().setAdapter(newsListAdapter);
        newsListAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener<OriginalItem>() {
            @Override
            public void onItemClick(BaseListItemView<OriginalItem> itemView) {
                info = itemView.get();
                if (info != null && !TextUtils.isEmpty(info.schema)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("requestCode", 1000);
                    JumpUtil.executeSchema(info.schema, MyCollectActivity.this, bundle);
                }
            }
        });
        sendNewRequest();
    }

    @Override
    protected void onPullDown() {
        super.onPullDown();
        sendNewRequest();
    }

    @Override
    protected void onPullUp() {
        super.onPullUp();
        long sid = 0;
        if (mDataList.size() > 0) {
            sid = mDataList.get(mDataList.size() - 1).lastTime;
        }
        sendNextRequest(sid);
    }

    @Override
    protected String getTitleText() {
        return "我的收藏";
    }

    @Override
    protected BaseAdapter getAdapter() {
        if (newsListAdapter == null) {
            newsListAdapter = new NewsListAdapter(this);
        }
        return newsListAdapter;
    }

    private void sendNewRequest() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.MY_COLLECTION);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEW);

        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<MyCollectListObject>() {
            @Override
            public void success(@Nullable MyCollectListObject data) {
                mDataList.clear();
                if (data != null && data.tweet_list != null) {
                    for (OriginalItem item : data.tweet_list) {
                        item.templateType = "news_single";
                    }
                    mDataList.addAll(data.tweet_list);
                    newsListAdapter.setItems(data.tweet_list);
                }
                newsListAdapter.notifyDataSetChanged();
                getPullRefreshListView().setAdapter(newsListAdapter);
//                EventBus.getDefault().post(new ClearMineRedEvent(ClearMineRedEvent.CLEAR_SYSMSG));
            }

            @Override
            public void end() {
                super.end();
                if (getPullRefreshListView() != null) {
                    getPullRefreshListView().onRefreshComplete();
                }
            }
        });
    }

    private void sendNextRequest(long ctime) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.MY_COLLECTION);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put("last_time", ctime + "");

        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<MyCollectListObject>() {
            @Override
            public void success(@Nullable MyCollectListObject data) {
                if (data != null && data.tweet_list != null) {
                    for (OriginalItem item : data.tweet_list) {
                        item.templateType = "news_single";
                    }
                    mDataList.addAll(data.tweet_list);
                    newsListAdapter.addItems(data.tweet_list);
                }
                newsListAdapter.notifyDataSetChanged();
            }


            @Override
            public void end() {
                super.end();
                if (getPullRefreshListView() != null) {
                    getPullRefreshListView().onRefreshComplete();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            if (!data.getBooleanExtra("isCollect", false)) {
                newsListAdapter.getItems().remove(info);
                newsListAdapter.notifyDataSetChanged();
            }
        }
    }
}
