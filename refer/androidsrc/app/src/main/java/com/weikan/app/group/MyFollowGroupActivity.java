package com.weikan.app.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.group.adapter.FollowGroupAdapter;
import com.weikan.app.group.bean.GroupDetailBean;
import com.weikan.app.group.bean.GroupListBean;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.LToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ylp on 2017/1/8.
 * 我的群组
 */

public class MyFollowGroupActivity extends BasePullToRefreshActivity {
    private List<GroupDetailBean> list = new ArrayList<>();
    private FollowGroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPullRefreshListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupDetailBean groupDetailBean = list.get(position - 1);
                Intent intent = new Intent(MyFollowGroupActivity.this, GroupDetailActivity.class);
                intent.putExtra(BundleParamKey.GROUPID, groupDetailBean.groupId);
                intent.putExtra(BundleParamKey.GROUPNAME, groupDetailBean.groupName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendNewRequest();
    }

    @Override
    protected String getTitleText() {
        return "我的群组";
    }

    @Override
    protected BaseAdapter getAdapter() {
        if (adapter == null) {
            adapter = new FollowGroupAdapter(this);
            adapter.setList(list);
        }
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
        long sid = 0;
        if (list.size() > 0) {
            sid = list.get(list.size() - 1).follow_time;
        }
        sendNextRequest(sid);
    }

    private void sendNewRequest() {
        GroupAgent.followGroupList(new platform.http.responsehandler.AmbJsonResponseHandler<GroupListBean>() {
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

    private void sendNextRequest(long sid) {
        GroupAgent.followGroupListMore(String.valueOf(sid), new platform.http.responsehandler.AmbJsonResponseHandler<GroupListBean>() {
            @Override
            public void success(@Nullable GroupListBean data) {
                if (data == null || data.groups == null || data.groups.size() == 0) {
                    LToast.showToast("没有更多内容了。");
                    return;
                }
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
