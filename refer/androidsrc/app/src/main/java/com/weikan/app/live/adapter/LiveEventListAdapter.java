package com.weikan.app.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import com.weikan.app.common.adater.BaseListAdapter;
import com.weikan.app.live.bean.LiveEventObject;
import com.weikan.app.live.widget.LiveEventItemView;

/**
 * 直播中的事件
 * 例如系统的广播、用户发的评论等等
 * @author kailun on 16/9/3.
 */
public class LiveEventListAdapter extends BaseListAdapter<LiveEventObject> {

    public LiveEventListAdapter(@NonNull Context context) {
        super(context, LiveEventItemView.class,false);
    }

}
