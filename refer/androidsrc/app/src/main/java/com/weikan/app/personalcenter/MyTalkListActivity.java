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
import com.weikan.app.personalcenter.adapter.MyTalkListAdapter;
import com.weikan.app.personalcenter.bean.ClearMineRedEvent;
import com.weikan.app.personalcenter.bean.MyMsgObject;
import com.weikan.app.personalcenter.bean.MyTalkListObject;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.WenyouDetailActivity;
import de.greenrobot.event.EventBus;
import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;
import platform.http.responsehandler.JsonResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liujian on 16/11/13.
 */
public class MyTalkListActivity extends BasePullToRefreshActivity {

    private MyTalkListAdapter mAdapter;
    private List<MyTalkListObject.MyTalkListContent> mDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPullRefreshListView().setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        getPullRefreshListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyTalkListObject.MyTalkListContent info = (MyTalkListObject.MyTalkListContent)parent.getItemAtPosition(position);
                info.msg_num = 0;
                Intent intent = new Intent(MyTalkListActivity.this, TalkActivity.class);
                intent.putExtra(URLDefine.UID, info.to_uid);
                intent.putExtra(URLDefine.UNAME, info.nickname);
                startActivity(intent);
                mAdapter.notifyDataSetChanged();
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
//        int sid = 0;
//        if(mDataList.size()>0){
//            sid = mDataList.get(mDataList.size()-1).sysMsgId;
//        }
//        sendNextRequest(sid);
    }

    @Override
    protected String getTitleText() {
        return "我的私信";
    }

    @Override
    protected BaseAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new MyTalkListAdapter(MyTalkListActivity.this, mDataList);
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
        builder.encodedPath(URLDefine.MY_TALK_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEW);

        HttpUtils.get(builder.build().toString(), params, new AmbJsonResponseHandler<MyTalkListObject>() {
            @Override
            public void success(@Nullable MyTalkListObject data) {
                mDataList.clear();
                if (data !=null && data.content != null) {
                    mDataList.addAll(data.content);
                }
                mAdapter.notifyDataSetChanged();

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

//    private void sendNextRequest(int sid) {
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme(URLDefine.SCHEME);
//        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
//        builder.encodedPath(URLDefine.MY_TALK_LIST);
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
//        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
//        params.put(URLDefine.TYPE, TYPE_NEXT);
//        params.put("last_sid", sid+"");
//
//        HttpUtils.get(builder.build().toString(), params, new AmbJsonResponseHandler<MyMsgObject>() {
//            @Override
//            public void success(@Nullable MyMsgObject data) {
//                if (data !=null && data.content != null) {
//                    mDataList.addAll(data.content);
//                }
//                mAdapter.notifyDataSetChanged();
//            }
//
//
//            @Override
//            public void end() {
//                super.end();
//                if (getPullRefreshListView() != null) {
//                    getPullRefreshListView().onRefreshComplete();
//                }
//            }
//
//        });
//    }

}
