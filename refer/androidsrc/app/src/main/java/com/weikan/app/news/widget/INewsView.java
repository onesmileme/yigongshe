package com.weikan.app.news.widget;

import android.view.View;

import com.weikan.app.original.bean.OriginalItem;


/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/1/8
 */
public interface INewsView {

    OriginalItem get();

    void set(OriginalItem item);

    void setOnItemClickListener(View.OnClickListener listener);
}
