package com.weikan.app.original.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.weikan.app.R;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/3/27
 */
public class SearchHeaderView extends FrameLayout {

    public SearchHeaderView(Context context) {
        super(context);
        initViews(context);
    }

    public SearchHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public SearchHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_search_header_view, this);
    }
}
