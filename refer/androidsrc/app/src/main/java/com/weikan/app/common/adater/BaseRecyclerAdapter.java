package com.weikan.app.common.adater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.weikan.app.common.widget.BaseListItemView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基本的RecyclerView.Adapter
 *
 * @author kailun on 16/4/20
 */
public class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> {

    @NonNull
    protected Context context;

    @Nullable
    protected Class<?> itemViewType;

    protected Constructor<?> itemViewConstructor;

    @NonNull
    protected List<T> items = new ArrayList<>();

    OnItemClickListener<T> onItemClickListener;

    public interface OnItemClickListener<T> {
        void onItemClick(BaseListItemView<T> itemView);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.onItemClickListener = listener;
    }

    public OnItemClickListener<T> getOnItemClickListener() {
        return this.onItemClickListener;
    }

    protected View.OnClickListener itemClickListener = new View.OnClickListener() {
        @SuppressWarnings("unchecked")
        @Override
        public void onClick(View v) {
            if (onItemClickListener != null && v instanceof BaseListItemView<?>) {
                BaseListItemView<T> view = (BaseListItemView<T>) v;
                onItemClickListener.onItemClick(view);
            }
        }
    };

    public BaseRecyclerAdapter(@NonNull Context context, @Nullable Class<?> itemViewType) {
        this.context = context;
        this.itemViewType = itemViewType;
        initItemViewConstructor(itemViewType);
    }

    @Override
    public BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseListItemView<T> v = makeRecyclerItemView();
        v.setOnClickListener(itemClickListener);

        return new BaseViewHolder<>(v);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(BaseViewHolder<T> holder, int position) {
        T item = items.get(position);
        BaseListItemView<T> itemView = (BaseListItemView<T>) holder.itemView;
        itemView.set(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
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

    private void initItemViewConstructor(@Nullable Class<?> itemViewType) {
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
    private BaseListItemView<T> makeRecyclerItemView() {
        BaseListItemView<T> view = null;

        try {
            view = (BaseListItemView<T>) itemViewConstructor.newInstance(context);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return view;
    }

}
