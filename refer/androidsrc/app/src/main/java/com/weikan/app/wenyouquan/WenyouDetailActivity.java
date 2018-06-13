package com.weikan.app.wenyouquan;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.util.LToast;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.adapter.WenyouDetailAdapter;
import com.weikan.app.wenyouquan.adapter.WenyouListAdapter;
import com.weikan.app.wenyouquan.bean.WenyouDetailObject;
import com.weikan.app.wenyouquan.bean.WenyouListData;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liujian on 16/7/31.
 */
public class WenyouDetailActivity extends BasePullToRefreshActivity  {
    String tid;

    WenyouDetailAdapter adapter;

    ArrayList<WenyouListData.WenyouListItem> dataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPullRefreshListView().setMode(PullToRefreshBase.Mode.DISABLED);

        tid = getIntent().getStringExtra(URLDefine.TID);

        if(TextUtils.isEmpty(tid)){
            LToast.showToast("数据有误，请重新再试。");
            finish();
            return;
        }


        sendRequest(tid);
    }

    @Override
    protected String getTitleText() {
        return "详情";
    }

    @Override
    protected BaseAdapter getAdapter() {
        if(adapter==null){
            adapter = new WenyouDetailAdapter(this,dataList);
        }
        return adapter;
    }


    private void sendRequest(String tid) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.WENYOU_DETAIL);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TID, tid);
        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<WenyouDetailObject>() {
            @Override
            public void success(@NonNull WenyouDetailObject data) {
                dataList.clear();
                dataList.add(data.content);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
            }
        });
    }
}
