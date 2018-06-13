package com.weikan.app.personalcenter;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
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
import java.util.List;
import java.util.Map;

import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;

/**
 * Created by liujian on 16/5/2.
 */
public class MyFavActivity extends BasePullToRefreshActivity {

    private OriginalMainAdapter mAdapter;
    private List<OriginalItem> mDataList = new ArrayList<>();

    private int curType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.setDivider(new ColorDrawable(MainApplication.getInstance().getResources().getColor(R.color.gray)));
        actualListView.setDividerHeight(DensityUtil.dip2px(MyFavActivity.this, 8));
        setTitleText("我的收藏");

        getPullRefreshListView().setMode(PullToRefreshBase.Mode.BOTH);
        getPullRefreshListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OriginalItem info = (OriginalItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(MyFavActivity.this, OriginalDetailActivity.class);
                intent.putExtra("tid", info.tid);
                startActivity(intent);
            }
        });

        sendNewRequest();
    }

    @Override
    protected String getTitleText() {
        return "";
    }

    @Override
    protected BaseAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new OriginalMainAdapter(MyFavActivity.this);
            mAdapter.setContent(mDataList);
        }
        return mAdapter;
    }

    @Override
    protected void onPullDown() {
        super.onPullDown();
        sendNewRequest();
    }

    @Override
    protected void onPullUp() {
        super.onPullUp();
        if(mDataList.size()>0){
            sendNextRequest(mDataList.get(mDataList.size()-1).actime);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void sendNewRequest() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.FAV_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TYPE, TYPE_NEW);

        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<OriginalItemData>() {
            @Override
            public void success(@NonNull OriginalItemData data) {
                if (data.content != null) {
                    mDataList.clear();
                    mDataList.addAll(data.content);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void end() {
                super.end();
                getPullRefreshListView().onRefreshComplete();
            }
        });
    }


    private void sendNextRequest(long time) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.FAV_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put("last_actime", time+"");

        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<OriginalItemData>() {
            @Override
            public void success(@NonNull OriginalItemData data) {
                if (data.content != null) {
                    int size = mDataList.size();
                    mDataList.addAll(size, data.content);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void end() {
                super.end();
                getPullRefreshListView().onRefreshComplete();
            }

        });
    }

}
