package com.weikan.app.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.BaseAdapter;

import com.weikan.app.Constants;
import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.group.adapter.AllGroupAdapter;
import com.weikan.app.group.bean.GroupDetailBean;
import com.weikan.app.group.bean.GroupListBean;
import com.weikan.app.util.LToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lee on 2017/01/09.
 */
public class AllGroupActivity extends BasePullToRefreshActivity {
    private List<GroupDetailBean> list = new ArrayList<>();
    private AllGroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendNewRequest();
    }

    @Override
    protected String getTitleText() {
        return "全部群组";
    }

    @Override
    protected BaseAdapter getAdapter() {
        if (adapter == null) {
            adapter = new AllGroupAdapter(this, list);
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
        if (list.size() > 0) {
            sendNextRequest();
        } else {
            sendNewRequest();
        }
    }

    int currPage = 0;

    private void sendNewRequest() {
        currPage = 0;
        GroupAgent.allGroupList(currPage, new platform.http.responsehandler.AmbJsonResponseHandler<GroupListBean>() {
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
        GroupAgent.allGroupList(currPage + 1, new platform.http.responsehandler.AmbJsonResponseHandler<GroupListBean>() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == GroupDetailActivity.RESULTCODE) {
            GroupDetailBean groupDetailBean = (GroupDetailBean) data.getSerializableExtra(Constants.GROUP_DETAIL);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).groupId.equals(groupDetailBean.groupId)) {
                    list.set(i, groupDetailBean);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }
}
