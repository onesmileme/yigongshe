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
public class CityCellView extends LinearLayout {

    private TextView tv;
    private View vSplitter;

    @NonNull
    private String text = "";

    // 文字的两种颜色
    int colorTextWhite = getResources().getColor(R.color.white);
    int colorTextBlack = getResources().getColor(R.color.more_text);

    // 背景的两种颜色
    int colorBgBlue = getResources().getColor(R.color.more_text_red);
    int colorBgWhite = getResources().getColor(R.color.white);

    public CityCellView(Context context) {
        super(context);
        initViews();
    }

    public CityCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public CityCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_city_cell_view, this);
        tv = (TextView) findViewById(R.id.tv);
        vSplitter = findViewById(R.id.v_splitter);

        setOrientation(LinearLayout.VERTICAL);

        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        setLayoutParams(lp);
    }

    @NonNull
    public String getText() {
        return this.text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
        tv.setText(text);
    }

    public void setSplitterVisible(boolean visible) {
        vSplitter.setVisibility(visible? View.VISIBLE: View.GONE);
    }

    public void setChoose(boolean choose) {
        if (choose) {
            tv.setTextColor(colorTextWhite);
            tv.setBackgroundColor(colorBgBlue);
        } else {
            tv.setTextColor(colorTextBlack);
            tv.setBackgroundColor(colorBgWhite);
        }
    }
}
