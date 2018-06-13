package com.weikan.app.personalcenter.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.weikan.app.common.adater.BaseListAdapter;
import com.weikan.app.personalcenter.bean.MyAttentionObject;
import com.weikan.app.personalcenter.widget.MyAttentionItemView;
import rx.functions.Action1;

/**
 * Created by zhaorenhui on 2015/12/6.
 */
public class MyAttentionAdapter extends BaseListAdapter<MyAttentionObject> {

    public Action1<MyAttentionItemView> actionAttentionClick;

    public MyAttentionAdapter(@NonNull Context context) {
        super(context, MyAttentionItemView.class);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (view instanceof MyAttentionItemView) {
            ((MyAttentionItemView) view).actionAttentionClick = this.actionAttentionClick;
        }
        return view;
    }
}

