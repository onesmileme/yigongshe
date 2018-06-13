package com.weikan.app.original.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.weikan.app.original.bean.OverlayObject;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/9
 */
public class TextOverlayView extends AbstractOverlayView {

    String content = "";
    String colorText = "";

    public TextOverlayView(Context context) {
        super(context);
    }

    public TextOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initWithOverlayObject(OverlayObject obj) {
        OverlayObject.Text text = obj.text;
        if (text == null) {
            return;
        }

        this.overlayObject = obj;

        Bitmap bitmap = drawText(obj.text);
        this.content = text.content;
        this.colorText = text.color;

        this.bitmap = bitmap;
        ivOverlay.setImageBitmap(bitmap);

        // 设置文字的实际大小
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        double density = getResources().getDisplayMetrics().density;
        this.oriResize = obj.resize;
        this.oriDisplayWidth = width * this.oriResize * this.oriScale;
        this.oriDisplayHeight = height * this.oriResize * this.oriScale;

        int actualWidth = (int) Math.round(this.oriDisplayWidth + 2 * PADDING * density);
        int actualHeight = (int) Math.round(this.oriDisplayHeight + 2 * PADDING * density);

        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.width = actualWidth;
        lp.height = actualHeight;
        setLayoutParams(lp);

        resetOverlay();
        moveOverlay(obj.x * this.oriScale, obj.y * this.oriScale);
        rotateOverlay(obj.rotation);
        resizeOverlay(1);
    }

    @Override
    public OverlayObject makeOverlayObject() {
        OverlayObject.Text text = overlayObject.text;
        if (text == null) {
            return null;
        }

        OverlayObject obj = new OverlayObject();
        obj.type = TYPE_TEXT;
        obj.text = text;
        obj.picture = null;

        OverlayObject.Text newText = new OverlayObject.Text();
        newText.content = content;
        newText.color = text.color;
        newText.size = text.size;

        // 图片中心点
        double centerx = (getLeft() + getRight()) / 2.0;
        double centery = (getTop() + getBottom()) / 2.0;

        obj.x = centerx / this.oriScale;
        obj.y = centery / this.oriScale;

        obj.resize = this.oriResize * resize;
        obj.rotation = rotation;

        return obj;
    }

    private static Paint getPaint(OverlayObject.Text text) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(text.size);

        int color = Color.WHITE;
        try {
            color = Color.parseColor(text.color);
        } catch (RuntimeException ignored) {
        }
        paint.setColor(color);
        return paint;
    }

    /**
     * 从文本生成一个Bitmap
     * @param text text
     * @return bitmap对象
     */
    private static Bitmap drawText(OverlayObject.Text text) {
        Paint paint = getPaint(text);

        Rect rect = new Rect();
        paint.getTextBounds(text.content, 0, text.content.length(), rect);

        Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text.content, 0, -rect.top, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return bitmap;
    }

    public static Rect calTextRect(OverlayObject.Text text) {
        Paint paint = getPaint(text);

        Rect rect = new Rect();
        paint.getTextBounds(text.content, 0, text.content.length(), rect);
        return rect;
    }

    public String getContent() {
        return content;
    }

    public String getColorText() {
        return colorText;
    }
}
