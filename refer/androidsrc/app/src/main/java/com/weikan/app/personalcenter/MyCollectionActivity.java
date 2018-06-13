package com.weikan.app.personalcenter;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.weikan.app.MainApplication;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.news.adapter.NewsListAdapter;
import com.weikan.app.original.OriginalDetailActivity;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.personalcenter.bean.MyCollectListObject;
import com.weikan.app.util.DensityUtil;
import com.weikan.app.util.URLDefine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;
import platform.http.responsehandler.JsonResponseHandler;

/**
 * Created by ylp on 2016/11/30.
 */

public class MyCollectionActivity extends BaseActivity implements PullToRefreshListView.OnRefreshListener2{
    private NewsListAdapter newsListAdapter;
    private ArrayList<OriginalItem> mDataList = new ArrayList<>();
    private PullToRefreshListView mPullRefreshListView;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_collection);
        TextView titleText = (TextView) findViewById(R.id.tv_titlebar_title);
        titleText.setText("我的收藏");
        findViewById(R.id.iv_titlebar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.base_pull_list_view);
        newsListAdapter = new NewsListAdapter(this);
        newsListAdapter.setItems(mDataList);
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.setDivider(new ColorDrawable(MainApplication.getInstance().getResources().getColor(R.color.gray)));
        actualListView.setDividerHeight(DensityUtil.dip2px(MyCollectionActivity.this, 8));
        mPullRefreshListView.setAdapter(newsListAdapter);
        mPullRefreshListView.setOnRefreshListener(this);
                mPullRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCollectionActivity.this.position = position;
                OriginalItem info = (OriginalItem)parent.getItemAtPosition(position);
                Intent intent = new Intent(MyCollectionActivity.this, OriginalDetailActivity.class);
                intent.putExtra(URLDefine.TID, info.tid);
                startActivity(intent);
            }
        });
        sendNewRequest();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        sendNewRequest();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        long sid = 0;
        if(mDataList.size()>0){
            sid = mDataList.get(mDataList.size()-1).lastTime;
        }
        sendNextRequest(sid);
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

        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<MyCollectListObject>(){
            @Override
            public void success(@Nullable MyCollectListObject  data) {
                mDataList.clear();
                if (data !=null && data.tweet_list!= null) {
                    mDataList.addAll(data.tweet_list);
                }
                newsListAdapter.setItems(mDataList);
                newsListAdapter.notifyDataSetChanged();
//                EventBus.getDefault().post(new ClearMineRedEvent(ClearMineRedEvent.CLEAR_SYSMSG));
            }

            @Override
            public void end() {
                super.end();
                if (mPullRefreshListView != null) {
                    mPullRefreshListView.onRefreshComplete();
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
        params.put("last_time", ctime+"");

        HttpUtils.get(builder.build().toString(), params, new AmbJsonResponseHandler<MyCollectListObject >() {
            @Override
            public void success(@Nullable MyCollectListObject data) {
                if (data !=null && data.tweet_list != null) {
                    mDataList.addAll(data.tweet_list);
                }
                newsListAdapter.notifyDataSetChanged();
            }


            @Override
            public void end() {
                super.end();
                if (mPullRefreshListView != null) {
                    mPullRefreshListView.onRefreshComplete();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 200){
            if(!data.getBooleanExtra("isCollect",false)){

                newsListAdapter.notifyDataSetChanged();
            }
        }
    }
}
