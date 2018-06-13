package com.weikan.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An {@link android.widget.ImageView} layout that maintains a consistent width to height aspect ratio.
 */
public class DynamicHeightImageView extends ImageView {

    private double mHeightRatio;
    private double mMaxHeightRatio = Double.MAX_VALUE;
//    private int mHeight;

    public DynamicHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicHeightImageView(Context context) {
        super(context);
    }

    public void setHeightRatio(double ratio) {
        if (ratio != mHeightRatio) {
            mHeightRatio = ratio;
            requestLayout();
        }
    }

    public void setMaxHeightRatio(double ratio) {
        mMaxHeightRatio = ratio;
    }

//    public void setHeight(int height) {
//        if (height != mHeight) {
//            mHeight = height;
//            requestLayout();
//        }
//    }

    public double getHeightRatio() {
        return mHeightRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHeightRatio > 0.0 ) {
            // set the image views size
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width * (mHeightRatio < mMaxHeightRatio ? mHeightRatio : mMaxHeightRatio));
//            setMeasuredDimension(width, mHeight);
            setMeasuredDimension(width, height);
        }
        else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
