package com.weikan.app.news.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 长宽比3/2的ImageView，新闻首页多处用到
 *
 * @author kailun on 16/1/11
 */
public class ThreeToTwoImageView extends ImageView {

    public ThreeToTwoImageView(Context context) {
        super(context);
    }

    public ThreeToTwoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeToTwoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int defaultHeight = (defaultWidth * 2 / 3);
        setMeasuredDimension(defaultWidth, defaultHeight);
    }
}
