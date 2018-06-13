package com.weikan.app.news.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.weikan.app.R;


/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/1/13
 */
public class BannerImageView extends FrameLayout {

    private ImageView ivPic;
    private TextView tvTitle;

    public BannerImageView(Context context) {
        super(context);
        initViews();
    }

    public BannerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public BannerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_banner_image_view, this);
        ivPic = (ImageView) findViewById(R.id.iv_pic);
        tvTitle = (TextView) findViewById(R.id.tv_title);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        setLayoutParams(lp);
    }

    public ImageView getImageView() {
        return ivPic;
    }

    public TextView getTextView() {
        return tvTitle;
    }
}
