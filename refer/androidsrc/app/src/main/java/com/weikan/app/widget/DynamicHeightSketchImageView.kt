package com.weikan.app.widget

import android.content.Context
import android.util.AttributeSet
import me.xiaopan.sketch.SketchImageView

/**
 * Created by liujian on 16/12/22.
 */
class DynamicHeightSketchImageView : SketchImageView {

    private var mHeightRatio: Double = 0.toDouble()
    private var mMaxHeightRatio = java.lang.Double.MAX_VALUE

    constructor(context: Context) : super(context) {

    }
    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {

    }
    constructor(context: Context, attributes: AttributeSet, defStyle: Int) : super(context, attributes, defStyle) {

    }

    fun setHeightRatio(ratio: Double) {
        if (ratio != mHeightRatio) {
            mHeightRatio = ratio
            requestLayout()
        }
    }

    fun setMaxHeightRatio(ratio: Double) {
        mMaxHeightRatio = ratio
    }

    fun getHeightRatio(): Double {
        return mHeightRatio
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mHeightRatio > 0.0) {
            // set the image views size
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (width * if (mHeightRatio < mMaxHeightRatio) mHeightRatio else mMaxHeightRatio).toInt()
            //            setMeasuredDimension(width, mHeight);
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}