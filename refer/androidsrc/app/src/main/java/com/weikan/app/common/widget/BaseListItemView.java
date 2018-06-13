package com.weikan.app.common.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/20
 */
public abstract class BaseListItemView<T> extends FrameLayout implements IListItemView<T>, IHasLayoutResource {

    @Nullable
    protected T item;

    public BaseListItemView(Context context) {
        super(context);
        init();
    }

    @Nullable
    public T get() {
        return item;
    }

    public void set(@Nullable T item) {
        this.item = item;
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(layoutResourceId(), this);
        initViews();
    }

    protected abstract void initViews();
}
