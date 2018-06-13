package com.weikan.app.original.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liujian on 16/3/21.
 */
public abstract class AbsDetailHeaderProvider<T> {
    public abstract View getDetailHeaderView(Context context, View originalView, ViewGroup parent, T object);

    public void onPause(View view){

    }

    public void onResume(View view){

    }
}
