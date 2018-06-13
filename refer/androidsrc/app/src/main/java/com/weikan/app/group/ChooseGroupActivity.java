package com.weikan.app.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.group.adapter.ChooseGroupAdapter;
import com.weikan.app.group.bean.GroupDetailBean;
import com.weikan.app.group.bean.GroupListBean;
import com.weikan.app.listener.OnNoRepeatClickListener;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.LToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ylp on 2017/1/8.
 * 选择群组
 */

public class ChooseGroupActivity extends BasePullToRefreshActivity {
    private String groupId;
    private List<GroupDetailBean> list = new ArrayList<>();
    private ChooseGroupAdapter adapter;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        groupId = getIntent().getStringExtra(BundleParamKey.GROUPID);
        groupName = getIntent().getStringExtra(BundleParamKey.GROUPNAME);
        super.onCreate(savedInstanceState);
        TextView rightBtn = (TextView) findViewById(R.id.tv_titlebar_right);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("保存");
        rightBtn.setOnClickListener(new OnNoRepeatClickListener() {
            @Override
            public void onNoRepeatClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(BundleParamKey.GROUPID, groupId);
                intent.putExtra(BundleParamKey.GROUPNAME, groupName);
                setResult(2000, intent);
                finish();
            }
        });
        getPullRefreshListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupDetailBean groupDetailBean = list.get(position - 1);
                if (groupDetailBean.groupId.equals(groupId)) {
                    groupId = null;
                    groupName = null;
                    adapter.setChooseId(null);
                } else {
                    groupId = groupDetailBean.groupId;
                    groupName = groupDetailBean.groupName;
                    adapter.setChooseId(groupId);
                }
                adapter.notifyDataSetChanged();
            }
        });
        sendNewRequest();
    }

    @Override
    protected String getTitleText() {
        return "选择群组";
    }

    @Override
    protected BaseAdapter getAdapter() {
        if (adapter == null) {
            adapter = new ChooseGroupAdapter(this);
            adapter.setList(list);
        }
        adapter.setChooseId(groupId);
        return adapter;
    }

    @Override
    protected void onPullDown() {
        super.onPullDown();
        sendNewRequest();
    }

    @Override
    protected void onPullUp() {
        super.onPullUp();
        if (list.size() > 0) {
            sendNextRequest();
        } else {
            sendNewRequest();
        }
    }

    int currPage = 0;

    private void sendNewRequest() {
        currPage = 0;
        GroupAgent.chooseGroupList(currPage, new platform.http.responsehandler.AmbJsonResponseHandler<GroupListBean>() {
            @Override
            public void success(@Nullable GroupListBean data) {
                list.clear();
                if (data != null && data.groups != null && data.groups.size() != 0) {
                    list.addAll(data.groups);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                getPullRefreshListView().onRefreshComplete();
            }
        });
    }

    private void sendNextRequest() {
        GroupAgent.chooseGroupList(currPage + 1, new platform.http.responsehandler.AmbJsonResponseHandler<GroupListBean>() {
            @Override
            public void success(@Nullable GroupListBean data) {
                if (data == null || data.groups == null || data.groups.size() == 0) {
                    LToast.showToast("没有更多内容了。");
                    return;
                }
                currPage++;
                list.addAll(data.groups);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                getPullRefreshListView().onRefreshComplete();
            }
        });
    }
}
