package com.weikan.app.news.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.weikan.app.common.adater.BaseListAdapter;
import com.weikan.app.news.widget.INewsView;
import com.weikan.app.news.widget.NewsItemType;
import com.weikan.app.news.widget.NewsViewFactory;
import com.weikan.app.original.bean.OriginalItem;

import java.util.*;


/**
 * 新闻的Adapter
 * 支持新闻的多种样式
 *
 * @author kailun on 16/4/20
 */
public class NewsListAdapter extends BaseListAdapter<OriginalItem> {

    private final Set<String> validTpls = NewsViewFactory.getSupportViewTypeSet();

    public NewsListAdapter(@NonNull Context context) {
        super(context, null);
    }

    @Override
    protected void initItemViewConstructor(@Nullable Class<?> itemViewType) {
        // do nothing
    }

    @SuppressWarnings({"unchecked", "TryWithIdenticalCatches"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsItemType itemViewType = getItemViewTypeEnum(position);

        if (itemViewType == NewsItemType.EMPTY) {
            if (emptyView == null) {
                emptyView = new EmptyView(context);
            }
            return emptyView;
        }

        INewsView v = NewsViewFactory.create(itemViewType, context);
        v.setOnItemClickListener(itemClickListener);

        OriginalItem item = (OriginalItem) getItem(position);
        v.set(item);

        return (View) v;
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewTypeEnum(position).toInt();
    }

    @Override
    public int getViewTypeCount() {
        return validTpls.size() + 1;
    }

    @Override
    public void addItems(@NonNull List<OriginalItem> items) {
        List<OriginalItem> filterResult = filter(items);
        super.addItems(filterResult);
    }

    @Override
    public void setItems(@NonNull List<OriginalItem> items) {
        List<OriginalItem> filterResult = filter(items);
        super.setItems(filterResult);
    }

    /**
     * 过滤不支持的tpl
     *
     * @param items items
     * @return 过滤后的items
     */
    @NonNull
    private List<OriginalItem> filter(@NonNull List<OriginalItem> items) {

        boolean hasInvalidItems = false;

        // 绝大多数情况下，不会有非法的项
        for (OriginalItem o : items) {
            if (!validTpls.contains(o.templateType)) {
                hasInvalidItems = true;
                break;
            }
        }

        if (hasInvalidItems) {
            // 只在有非法项的时候，才创建新列表
            List<OriginalItem> newItems = new ArrayList<>();
            for (OriginalItem o : items) {
                if (validTpls.contains(o.templateType)) {
                    newItems.add(o);
                }
            }
            return newItems;
        }

        return items;
    }

    /**
     * 与上面那个用处一样，只是返回枚举
     */
    private NewsItemType getItemViewTypeEnum(int position) {
        if (position < items.size()) {
            OriginalItem item = (OriginalItem) getItem(position);

            if (item.templateType == null) {
                throw new RuntimeException("unknown itemViewType: null");
            }
            return NewsViewFactory.mapViewType(item.templateType);
        } else if (position == items.size()) {
            return NewsItemType.EMPTY; // +1，为了兼容
        }

        throw new RuntimeException("position(" + position + ") is large than items.size("
                + items.size() + ")");
    }
}
