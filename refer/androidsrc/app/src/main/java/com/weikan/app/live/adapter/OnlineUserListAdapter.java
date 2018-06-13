package com.weikan.app.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import com.weikan.app.common.adater.BaseRecyclerAdapter;
import com.weikan.app.live.bean.OnlineUserObject;
import com.weikan.app.live.widget.OnlineUserListItemView;

/**
 * @author kailun on 16/9/3.
 */
public class OnlineUserListAdapter extends BaseRecyclerAdapter<OnlineUserObject> {
    public OnlineUserListAdapter(@NonNull Context context) {
        super(context, OnlineUserListItemView.class);
    }
}

