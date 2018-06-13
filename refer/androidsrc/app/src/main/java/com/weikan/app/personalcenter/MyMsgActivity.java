package com.weikan.app.personalcenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.personalcenter.adapter.MyMsgAdapter;
import com.weikan.app.personalcenter.bean.ClearMineRedEvent;
import com.weikan.app.personalcenter.bean.MyMsgObject;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.WenyouDetailActivity;
import de.greenrobot.event.EventBus;
import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的消息界面
 * Created by liujian on 16/7/30.
 */
public class MyMsgActivity extends BasePullToRefreshActivity {

    private MyMsgAdapter mAdapter;
    private List<MyMsgObject.ContentObject> mDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPullRefreshListView().setMode(PullToRefreshBase.Mode.BOTH);
        getPullRefreshListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyMsgObject.ContentObject info = (MyMsgObject.ContentObject)parent.getItemAtPosition(position);
                Intent intent = new Intent(MyMsgActivity.this, WenyouDetailActivity.class);
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
        int sid = 0;
        if(mDataList.size()>0){
            sid = mDataList.get(mDataList.size()-1).sysMsgId;
        }
        sendNextRequest(sid);
    }

    @Override
    protected String getTitleText() {
        return "我的消息";
    }

    @Override
    protected BaseAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new MyMsgAdapter(MyMsgActivity.this, mDataList);
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
        builder.encodedPath(URLDefine.MY_MSG_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEW);

        HttpUtils.get(builder.build().toString(), params, new AmbJsonResponseHandler<MyMsgObject>() {
            @Override
            public void success(@Nullable MyMsgObject data) {
                mDataList.clear();
                if (data !=null && data.content != null) {
                    mDataList.addAll(data.content);
                }
                mAdapter.notifyDataSetChanged();

                EventBus.getDefault().post(new ClearMineRedEvent(ClearMineRedEvent.CLEAR_SYSMSG));
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

    private void sendNextRequest(int sid) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.MY_MSG_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put("last_sid", sid+"");

        HttpUtils.get(builder.build().toString(), params, new AmbJsonResponseHandler<MyMsgObject>() {
            @Override
            public void success(@Nullable MyMsgObject data) {
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
