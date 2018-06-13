package com.weikan.app.common.widget;

import android.support.annotation.Nullable;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/20
 */
public interface IListItemView <T> {

    @Nullable
    T get();

    void set(@Nullable T item);
}
