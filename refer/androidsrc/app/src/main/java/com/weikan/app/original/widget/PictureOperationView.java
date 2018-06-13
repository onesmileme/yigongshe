package com.weikan.app.original.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.weikan.app.original.utils.Location;
import com.weikan.app.original.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/5
 */
public class PictureOperationView extends FrameLayout {

    List<AbstractOverlayView> overlayViews = new ArrayList<>();

    AbstractOverlayView curOverlayView = null;

    @Nullable
    public AbstractOverlayView getCurrentOverlayView() {
        return curOverlayView;
    }

    Location lastLoc0 = null;
    Location lastLoc1 = null;

    double startDistance = -1;
    double startResize = -1;

    // 临时变量，用来计算Location用
    int[] tmpLocation = new int[2];

    OverlayTouchHelper overlayTouchHelper = new OverlayTouchHelper();

    public List<AbstractOverlayView> getOverlayViews() {
        return overlayViews;
    }

    private class OverlayTouchHelper {

        MotionEvent lastEvent; // 上一次命中Overlay的MotionEvent
        Location lastLocation; // 上一次按下时的位置
        AbstractOverlayView lastOverlayView; // 上一次命中的Overlay

        /**
         * 来自{@link AbstractOverlayView}的回调
         * 每一个AbstractOverlayView都会响应{@link MotionEvent#ACTION_DOWN}，但是并不会消费这个MotionEvent
         * 因此AbstractOverlayView不会接收到接下来的{@link MotionEvent#ACTION_UP}，只能在AbstractOverlayView接收
         * @param view view
         * @param event event
         */
        public void onOverlayTouchDown(@NonNull AbstractOverlayView view, @NonNull MotionEvent event) {
            if (this.lastEvent != event) {
                this.lastEvent = event;
                this.lastLocation = locationOfOverlayInView(event.getRawX(), event.getRawY());
                this.lastOverlayView = view;
            }
        }

        public void onOverlayTouchUp(@NonNull MotionEvent event) {
            if (this.lastEvent == null) {
                this.lastEvent = null;
                this.lastLocation = null;
                this.lastOverlayView = null;
                return;
            }

            double lastX = this.lastLocation.x;
            double lastY = this.lastLocation.y;
            double x = event.getX();
            double y = event.getY();
            double distance = MathUtils.distance(x, y, lastX, lastY);

            // 给一个阈值，小于这个阈值，可以认为是一个Tap
            if (distance < 15) {
                if (curOverlayView == this.lastOverlayView) {
                    if (this.lastOverlayView instanceof TextOverlayView &&
                            PictureOperationView.this.pendingInputTextPanelListener != null) {
                        PictureOperationView.this.pendingInputTextPanelListener.pendingInputTextPanel();
                    }
                } else {
                    setCurrentOverlayView(this.lastOverlayView);
                }
            }

            this.lastEvent = null;
            this.lastLocation = null;
            this.lastOverlayView = null;
        }
    }

    // region 准备弹出InputTextPanel的回调
    PendingInputTextPanelListener pendingInputTextPanelListener = null;

    public interface PendingInputTextPanelListener {
        void pendingInputTextPanel();
    }

    public void setOnInputTextPanelPopListener(PendingInputTextPanelListener listener) {
        this.pendingInputTextPanelListener = listener;
    }
    // endregion

    public PictureOperationView(Context context) {
        super(context);
    }

    public PictureOperationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureOperationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        boolean ret = super.onTouchEvent(event);// 是否向下传递事件标志 true为消耗

        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                ret = true;
                onTouchEventActionDown(event);
                break;

            case MotionEvent.ACTION_MOVE:
                ret = true;
                onTouchEventActionMove(event);
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ret = false;
                onTouchEventActionUp(event);
                break;
        }
        return ret;
    }

    public void addOverlayView(AbstractOverlayView view) {
        addView(view);

        overlayViews.add(view);
        view.setOnTouchDownEventListener(new AbstractOverlayView.TouchDownEventListener() {
            @Override
            public void onTouchDownEvent(@NonNull AbstractOverlayView view, @NonNull MotionEvent event) {
                overlayTouchHelper.onOverlayTouchDown(view, event);
            }
        });
        view.setOnCloseButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseButtonClick(v);
            }
        });
    }

    private void onCloseButtonClick(View v) {
        if (!(v instanceof AbstractOverlayView)) {
            return;
        }

        AbstractOverlayView view = (AbstractOverlayView) v;
        overlayViews.remove(view);
        removeView(view);

        if (overlayViews.size() != 0) {
            AbstractOverlayView lastView = overlayViews.get(overlayViews.size() - 1);
            setCurrentOverlayView(lastView);
        }
    }

    public Location locationOfOverlayInView(double rawX, double rawY) {
        // 取得PictureOperationView在Screen中的位置
        tmpLocation[0] = 0;
        tmpLocation[1] = 0;
        getLocationOnScreen(tmpLocation);

        double x = rawX - tmpLocation[0];
        double y = rawY - tmpLocation[1];

        return new Location(x, y);
    }

    public void setCurrentOverlayView(@Nullable AbstractOverlayView view) {
        this.curOverlayView = view;

        for (AbstractOverlayView v: overlayViews) {
            v.setIsActive(v == view);
        }
    }

    private void onTouchEventActionDown(@NonNull MotionEvent event) {
        Log.e(this.getClass().getSimpleName(), "onTouchEventActionDown:" + event.getPointerCount());

        if (curOverlayView == null) {
            return;
        }

        lastLoc0 = new Location(event.getX(0), event.getY(0));
        if (event.getPointerCount() == 1) {
            lastLoc1 = null;
        }
        else if (event.getPointerCount() == 2) {
            lastLoc1 = new Location(event.getX(1), event.getY(1));;
        }
    }

    private void onTouchEventActionMove(@NonNull MotionEvent event) {
        Log.e(this.getClass().getSimpleName(), "onTouchEventActionMove:" + event.getPointerCount());
        if (curOverlayView == null) {
            return;
        }

        // 单指拖动Overlay
        if (event.getPointerCount() == 1) {
            singleTouchMove(curOverlayView, event);
        }
        // 双指缩放/旋转Overlay
        else if (event.getPointerCount() == 2) {
            doubleTouchMove(curOverlayView, event);
        }
    }

    private void singleTouchMove(AbstractOverlayView overlayView, MotionEvent event) {
        Location l = new Location(event.getX(), event.getY());

        if (lastLoc0 != null && lastLoc1 == null) {
            double dx = l.x - lastLoc0.x;
            double dy = l.y - lastLoc0.y;
            overlayView.moveOverlay(dx, dy);
        }

        lastLoc0 = l;
        lastLoc1 = null;
    }

    private void doubleTouchMove(AbstractOverlayView overlayView, MotionEvent event) {

        // 第一个touch，对应的当前位置和移动前的位置
        // 第二个touch，对应的当前位置和移动前的位置
        Location l0 = new Location(event.getX(0), event.getY(0));
        Location l1 = new Location(event.getX(1), event.getY(1));

        if (lastLoc0 != null && lastLoc1 != null) {
            if (startDistance == -1) {
                startDistance = MathUtils.distance(l1, l0);
                startResize = overlayView.getResizeValue();
            }

            // 计算旋转
            double angle = MathUtils.angle(l1, l0);
            double pAngle = MathUtils.angle(lastLoc1, lastLoc0);
            double rotation = Math.PI * overlayView.getRotation() / 180.0 + angle - pAngle;

            // 计算距离
            double distance = MathUtils.distance(l1, l0);
            double resize = startResize * distance / startDistance;

            overlayView.resizeOverlay(resize);
            overlayView.rotateOverlay(rotation);

            Log.e(this.getClass().getSimpleName(), "onTouchEventActionMove: rotate: " + rotation);
        }

        lastLoc0 = l0;
        lastLoc1 = l1;
    }

    private void onTouchEventActionUp(@NonNull MotionEvent event) {
        // 重置
        lastLoc0 = null;
        lastLoc1 = null;
        startDistance = -1;
        startResize = -1;

        overlayTouchHelper.onOverlayTouchUp(event);
    }
}
