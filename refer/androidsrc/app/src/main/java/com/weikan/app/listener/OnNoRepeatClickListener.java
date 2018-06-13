package com.weikan.app.listener;

import android.view.View;

/**
 * 防止多次点击的OnClickListener
 * <p>
 * Created by guhyuan on 2016/11/28,0028.
 */

public abstract class OnNoRepeatClickListener implements View.OnClickListener {

    private long time = System.currentTimeMillis();

    private static final int DEF = 500;

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - time < DEF) {
            return;
        } else {
            onNoRepeatClick(v);
            time = System.currentTimeMillis();
        }
    }

    public abstract void onNoRepeatClick(View v);
}
