package com.weikan.app.original.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.weikan.app.R;
import com.weikan.app.original.bean.OverlayObject;
import com.weikan.app.original.utils.Location;
import com.weikan.app.original.utils.MathUtils;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/9
 */
public abstract class AbstractOverlayView extends FrameLayout {

    public static final int PADDING = 20;
    public static final String TYPE_PICTURE = "picture";
    public static final String TYPE_TEXT = "text";

    View vBg;
    ImageView ivOverlay;
    ImageView ivClose;
    ImageView ivZoom;

    // 原始的OverlayObject
    OverlayObject overlayObject;

    // 原始的显示尺寸
    double oriDisplayWidth = 0.0;
    double oriDisplayHeight = 0.0;
    double oriResize = 1.0;

    // 底图的scale
    double oriScale = 1.0;
    double overlayScale = 1.0;

    // 图片和一些属性
    Bitmap bitmap = null;
    double rotation = 0.0;

    double resize = 1.0;

    boolean isActive = false;
    // 一些辅助的变量
    Rect zoomHitRect = new Rect(); // Zoom 按钮的Hit区域

    Location cornerLocation = new Location(0, 0); // 在假定的原始大小下，正着放的Overlay的右下角坐标
    View.OnClickListener closeButtonClickListener;

    public void setOnCloseButtonClickListener(@Nullable View.OnClickListener listener) {
        this.closeButtonClickListener = listener;
    }

    public double getResizeValue() {
        return resize;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public interface TouchDownEventListener {
        void onTouchDownEvent(@NonNull AbstractOverlayView view, @NonNull MotionEvent event);
    }

    TouchDownEventListener touchDownEventListener;

    public void setOnTouchDownEventListener(@Nullable TouchDownEventListener listener) {
        this.touchDownEventListener = listener;
    }

    public AbstractOverlayView(Context context) {
        super(context);
        initViews();
    }

    public AbstractOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public AbstractOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        boolean ret = false;

        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                ret = onTouchEventDown(event);
                break;

            case MotionEvent.ACTION_MOVE:
                ret = onTouchEventMove(event);
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;

            default:
                break;
        }
        return ret;
    }

    private boolean onTouchEventDown(@NonNull MotionEvent event) {
        boolean ret = false;

        // 判断是否点中了Zoom按钮，如果是，那么就要手动处理后续的Move事件
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());
        ivZoom.getHitRect(zoomHitRect);
        if (zoomHitRect.contains(x, y)) {

            double centerX = (getLeft() + getRight()) / 2.0;
            double centerY = (getTop() + getBottom()) / 2.0;

            cornerLocation = new Location(
                    centerX + oriDisplayWidth / 2.0 + PADDING / 2.0,
                    centerY + oriDisplayHeight / 2.0 + PADDING / 2.0);

            ret = true;
        }

        // 否则就走一般的TouchDown处理流程
        // 后续用来判断点击选中
        if (touchDownEventListener != null) {
            touchDownEventListener.onTouchDownEvent(this, event);
        }
        return ret;
    }

    private boolean onTouchEventMove(@NonNull MotionEvent event) {
        double centerX = (getLeft() + getRight()) / 2.0;
        double centerY = (getTop() + getBottom()) / 2.0;
                Location center = new Location(centerX, centerY);

        // 当前点坐标
        PictureOperationView view = (PictureOperationView) getParent();
        Location current = view.locationOfOverlayInView(event.getRawX(), event.getRawY());

        double[] result = calculateResizeParams(center, cornerLocation, current);

        // 执行旋转和缩放
        resizeOverlay(result[0]);
        rotateOverlay(result[1]);

        return true;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;

        if (isActive) {
            ivClose.setVisibility(View.VISIBLE);
            ivZoom.setVisibility(View.VISIBLE);
            vBg.setBackgroundResource(R.drawable.shape_picture_overlay_bg);
        } else {
            ivClose.setVisibility(View.GONE);
            ivZoom.setVisibility(View.GONE);
            vBg.setBackgroundResource(0);
        }
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_picture_overlay_view, this);
        vBg = findViewById(R.id.v_bg);
        ivOverlay = (ImageView) findViewById(R.id.iv_overlay);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeButtonClickListener != null) {
                    closeButtonClickListener.onClick(AbstractOverlayView.this);
                }
            }
        });
        ivZoom = (ImageView) findViewById(R.id.iv_zoom);
    }

    public void setOriScala(double scale) {
        this.oriScale = scale;
    }

    public void setOverlayScale(double scale) {
        this.overlayScale = scale;
    }

    public abstract void initWithOverlayObject(OverlayObject obj);

    public abstract OverlayObject makeOverlayObject();

    /**
     * 缩放Overlay
     * @param resize resize
     */
    public void resizeOverlay(double resize) {
        this.resize = resize;

        double density = getResources().getDisplayMetrics().density;
        double halfWidth = oriDisplayWidth * resize / 2.0 + PADDING * density;
        double halfHeight = oriDisplayHeight * resize/ 2.0 + PADDING * density;

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        double centerX = lp.leftMargin + lp.width / 2.0;
        double centerY = lp.topMargin + lp.height / 2.0;

        lp.leftMargin = (int) Math.round(centerX - halfWidth);
        lp.topMargin = (int) Math.round(centerY - halfHeight);

        lp.width = (int) Math.round(halfWidth * 2);
        lp.height = (int) Math.round(halfHeight * 2);
        setLayoutParams(lp);
    }

    /**
     * 旋转Overlay
     * @param rotation rotate
     */
    public void rotateOverlay(double rotation) {
        this.rotation = rotation;
        this.setRotation((float)(rotation * 180.0 / Math.PI));
    }

    /**
     * 平移Overlay
     * @param dx dx
     * @param dy dy
     */
    public void moveOverlay(double dx, double dy) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)this.getLayoutParams();
        lp.leftMargin += dx;
        lp.topMargin += dy;
        setLayoutParams(lp);
    }

    protected void resetOverlay() {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)this.getLayoutParams();
        lp.leftMargin = - (int)Math.round(lp.width / 2.0);
        lp.topMargin = - (int)Math.round(lp.height / 2.0);
        setLayoutParams(lp);
    }

    public double coordOverlay2Screen(double src) {
        return src * overlayScale;
    }

    public double coordOri2Screen(double src) {
        return src * oriScale;
    }

    public double[] calculateResizeParams(Location origin, Location start, Location current) {
        double sx = start.x, sy = start.y;
        double ox = origin.x, oy = origin.y;
        double cx = current.x, cy = current.y;

        // 先算旋转的角度
        double sAngle = MathUtils.angle(ox, oy, sx, sy);
        double cAngle = MathUtils.angle(ox, oy, cx, cy);

        double rotate = cAngle - sAngle;

        // 再算距离
        double sDistance = MathUtils.distance(ox, oy, sx, sy);
        double cDistance = MathUtils.distance(ox, oy, cx, cy);

        double resize = cDistance / sDistance;

        return new double[] {resize, rotate};
    }
}
