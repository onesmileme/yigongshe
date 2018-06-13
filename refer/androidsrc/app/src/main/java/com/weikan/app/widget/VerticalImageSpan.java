package com.weikan.app.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by liujian on 16/11/28.
 */
public class VerticalImageSpan extends ImageSpan {
    public VerticalImageSpan(Drawable drawable) {
        super(drawable);
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = this.getDrawable();
        canvas.save();
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int transY = (y + fm.descent + y + fm.ascent) / 2
                - drawable.getBounds().bottom / 2;
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }
}
