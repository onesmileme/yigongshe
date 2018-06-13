package com.weikan.app.common.adater;

import android.support.v7.widget.RecyclerView;
import com.weikan.app.common.widget.BaseListItemView;

/**
 * @author kailun on 16/8/31.
 */
/* package */ class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    /* package */ BaseViewHolder(BaseListItemView<T> itemView) {
        super(itemView);
    }
}
