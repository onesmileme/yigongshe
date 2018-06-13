package com.weikan.app.common.adater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.listener.OnNoRepeatClickListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基本的Adapter
 * PullToRefresh有一个问题，就是当包含的Header超过一屏，并且getCount() == 0的时
 * 往上拉时不能看到完整的Header，而会除非上拉的动画
 * 解决方法就是给Adapter添加一个空View，实现兼容
 * 这个类已经实现了这个兼容，详见{@link #getCount()}, {@link #getItem(int)}, {@link #getItemViewType(int)} 等
 *
 * @author kailun on 16/4/20
 */
public class BaseListAdapter<T> extends BaseAdapter {

    @NonNull
    protected Context context;

    @Nullable
    protected Class<?> itemViewType;

    protected Constructor<?> itemViewConstructor;

    @NonNull
    protected List<T> items = new ArrayList<>();

    OnItemClickListener<T> onItemClickListener;

    @Nullable
    protected View emptyView;

    protected View.OnClickListener itemClickListener = new OnNoRepeatClickListener() {
        @Override
        public void onNoRepeatClick(View v) {
            if (onItemClickListener != null && v instanceof BaseListItemView<?>) {
                BaseListItemView<T> view = (BaseListItemView<T>) v;
                onItemClickListener.onItemClick(view);
            }
        }
    };

    public BaseListAdapter(@NonNull Context context, @Nullable Class<?> itemViewType) {
        this.context = context;
        this.itemViewType = itemViewType;
        initItemViewConstructor(itemViewType);
    }
    public BaseListAdapter(@NonNull Context context, @Nullable Class<?> itemViewType,boolean click) {
        this.context = context;
        this.itemViewType = itemViewType;
        initItemViewConstructor(itemViewType);
        this.click = click;
    }
    @Override
    public int getCount() {
        return items.size() + 1; // +1，为了兼容
    }

    @Override
    public Object getItem(int position) {
        if (position < items.size()) {
            return items.get(position);
        }
        return null; // +1，为了兼容
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == 0) { // +1，为了兼容
            if (emptyView == null) {
                emptyView = new EmptyView(context);
            }
            return emptyView;
        }
        else if (itemViewType == 1) {
            BaseListItemView<T> v = makeListItemView(convertView);
            if(click ){
                v.setOnClickListener(itemClickListener);
            }
            T item = (T) getItem(position);
            v.set(item);
            return v;
        }

        throw new RuntimeException("unknown itemViewType: " + itemViewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == items.size()){
            return 0; // +1，为了兼容
        }
        else if (position < items.size()) {
            return 1;
        }

        throw new RuntimeException("position(" + position + ") is large than items.size("
                + items.size() + ")");
    }

    @Override
    public int getViewTypeCount() {
        return 2; // 0 表示普通的item，1表示空项目
        // +1，为了兼容
    }

    protected void initItemViewConstructor(@Nullable Class<?> itemViewType) {
        if (itemViewType == null) {
            return;
        }

        try {
            itemViewConstructor = itemViewType.getConstructor(Context.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(itemViewType.getName() + "has no constructor with param Context");
        }
    }

    @SuppressWarnings({"unchecked", "TryWithIdenticalCatches"})
    public BaseListItemView<T> makeListItemView(View convertView) {
        BaseListItemView<T> view = null;
        if (convertView != null) {
            view = (BaseListItemView<T>) convertView;
        }

        if (view == null) {
            try {
                view = (BaseListItemView<T>) itemViewConstructor.newInstance(context);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return view;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(BaseListItemView<T> itemView);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.onItemClickListener = listener;
    }

    public OnItemClickListener<T> getOnItemClickListener() {
        return this.onItemClickListener;
    }

    @NonNull
    public List<T> getItems() {
        return this.items;
    }

    public void setItems(@NonNull List<T> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public void addItems(@NonNull List<T> items) {
        this.items.addAll(items);
    }

    @Nullable
    public T first() {
        if (this.items.size() != 0) {
            return this.items.get(0);
        }
        return null;
    }

    @Nullable
    public T last() {
        if (this.items.size() != 0) {
            return this.items.get(this.items.size() - 1);
        }
        return null;
    }

    public void clear() {
        this.items.clear();
    }

    /**
     * 空白的View，用于兼容PullToRefresh的Bug
     */
    public static class EmptyView extends View {

        public EmptyView(Context context) {
            super(context);
            init();
        }

        public EmptyView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0);
            setLayoutParams(lp);
        }
    }
    private boolean click = true;
}
