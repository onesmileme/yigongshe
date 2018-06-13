package com.weikan.app.personalcenter.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weikan.app.R;


/**
 * abstract of class/interface and so on
 *
 * @author kailun on 15/12/14
 */
public class CitySectionView extends LinearLayout {

    private TextView tv;
    private View vSplitter;

    public CitySectionView(Context context) {
        super(context);
        initViews();
    }

    public CitySectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public CitySectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_city_section_view, this);
        tv = (TextView) findViewById(R.id.tv);
        vSplitter = findViewById(R.id.v_splitter);

        setOrientation(LinearLayout.VERTICAL);

        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        setLayoutParams(lp);
    }

    public void setText(@NonNull String text) {
        tv.setText(text);
    }
}
