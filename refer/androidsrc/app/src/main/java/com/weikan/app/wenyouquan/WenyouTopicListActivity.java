package com.weikan.app.wenyouquan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.personalcenter.MyMsgActivity;
import com.weikan.app.personalcenter.adapter.MyMsgAdapter;
import com.weikan.app.personalcenter.bean.MyMsgObject;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.adapter.WenyouListAdapter;
import com.weikan.app.wenyouquan.bean.WenyouListData;
import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 话题列表页
 * Created by liujian on 16/8/8.
 */
public class WenyouTopicListActivity extends BasePullToRefreshActivity {

    private WenyouListAdapter mAdapter;
    private List<WenyouListData.WenyouListItem> mDataList = new ArrayList<>();

    String topicId = "";
    String topicName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topicId = getIntent().getStringExtra(URLDefine.TOPIC_ID);
        topicName = getIntent().getStringExtra(URLDefine.TOPIC_NAME);

        if (TextUtils.isEmpty(topicId)) {
            finish();
            return;
        }

        if (!TextUtils.isEmpty(topicName)) {
            setTitleText("#" + topicName);
        }

        getPullRefreshListView().setMode(PullToRefreshBase.Mode.BOTH);
        getPullRefreshListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyMsgObject.ContentObject info = (MyMsgObject.ContentObject)parent.getItemAtPosition(position);
                Intent intent = new Intent(WenyouTopicListActivity.this, WenyouDetailActivity.class);
                intent.putExtra(URLDefine.TID, info.tid);
                startActivity(intent);
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
        if(mDataList.size()>0){
            sid = mDataList.get(mDataList.size()-1).ctime;
        }
        sendNextRequest(sid);
    }

    @Override
    protected String getTitleText() {
        return "";
    }

    @Override
    protected BaseAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new WenyouListAdapter(WenyouTopicListActivity.this, mDataList);
        }
        return mAdapter;
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
        builder.encodedPath(URLDefine.MOMENTS_TOPIC_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEW);
        params.put(URLDefine.TOPIC_ID, topicId);

        HttpUtils.get(builder.build().toString(), params, new AmbJsonResponseHandler<WenyouListData>() {
            @Override
            public void success(@Nullable WenyouListData data) {
                mDataList.clear();
                if (data !=null && data.content != null) {
                    mDataList.addAll(data.content);
                }
                mAdapter.notifyDataSetChanged();
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

    private void sendNextRequest(long lasttime) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.MOMENTS_TOPIC_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put(URLDefine.TOPIC_ID, topicId);
        params.put("last_ctime", lasttime+"");

        HttpUtils.get(builder.build().toString(), params, new AmbJsonResponseHandler<WenyouListData>() {
            @Override
            public void success(@Nullable WenyouListData data) {
                if (data !=null && data.content != null) {
                    mDataList.addAll(data.content);
                }
                mAdapter.notifyDataSetChanged();
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

}
