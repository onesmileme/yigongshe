package com.weikan.app.original.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 长宽比7/4的ImageView，新闻首页多处用到
 *
 * @author kailun on 16/1/11
 */
public class FiveToThreeImageView extends ImageView {

    public FiveToThreeImageView(Context context) {
        super(context);
    }

    public FiveToThreeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FiveToThreeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int defaultHeight = (defaultWidth * 3 / 5);
        setMeasuredDimension(defaultWidth, defaultHeight);
    }
}
