package com.weikan.app.original.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.weikan.app.original.bean.OverlayObject;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/4
 */
public class PictureOverlayView extends AbstractOverlayView implements Target {

    public PictureOverlayView(Context context) {
        super(context);
    }

    public PictureOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        this.bitmap = bitmap;
        ivOverlay.setImageBitmap(bitmap);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }

    @Override
    public void initWithOverlayObject(OverlayObject obj) {
        OverlayObject.Picture picture = obj.picture;
        if (picture == null || picture.n == null) {
            return;
        }

        this.overlayObject = obj;

        String url = picture.n.url;
        Picasso.with(getContext())
                .load(Uri.parse(url))
                .into(this);

        // 设置图片的实际大小
        int width = picture.n.w;
        int height = picture.n.h;

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
        OverlayObject.Picture picture = overlayObject.picture;
        if (picture == null) {
            return null;
        }

        OverlayObject obj = new OverlayObject();
        obj.type = TYPE_PICTURE;
        obj.picture = picture;
        obj.text = null;

        // 图片中心点
        double centerx = (getLeft() + getRight()) / 2.0;
        double centery = (getTop() + getBottom()) / 2.0;

        // 先平移
        obj.x  = centerx / this.oriScale; /* oriWidth * density得到屏幕上的像素宽度 */
        obj.y = centery / this.oriScale; /* 同上 */

        obj.resize = this.oriResize * resize;
        obj.rotation = rotation;

        return obj;
    }
}
