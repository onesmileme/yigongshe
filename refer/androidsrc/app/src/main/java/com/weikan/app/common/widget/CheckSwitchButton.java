package com.weikan.app.common.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.CheckBox;
import com.weikan.app.R;

/**
 * @describe 仿iphone开关控件
 * @author https://github.com/Issacw0ng/SwitchButton
 *
 */
public class CheckSwitchButton extends CheckBox {
    /** »­±Ê */
    private Paint mPaint;
    private ViewParent mParent;
    private Bitmap mBottom;
    private Bitmap mCurBtnPic;
    private Bitmap mBtnPressed;
    private Bitmap mBtnNormal;
    /** ÕûÌå¿ò¼Ü */
    private Bitmap mFrame;
    /** ÒõÓ°²ã£¬²»ÄÜµã»÷µÄÊ±ºòÏÔÊ¾ */
    private Bitmap mMask;
    /** ±£´æ²¼¾ÖµÄ¾ØÕó */
    private RectF mSaveLayerRectF;
    /** ²¼¾ÖÖ®¼äµþ²ã£¬ºÃ±È±³¾°ºÍ±³¾°ÉÏµÄÍ¼Æ¬Ð§¹û */
    private PorterDuffXfermode mXfermode;
    /** Ê×´Î°´ÏÂµÄY */
    private float mFirstDownY;
    /** Ê×´Î°´ÏÂµÄX */
    private float mFirstDownX;
    /** Í¼Æ¬µÄ»æÖÆÎ»ÖÃ */
    private float mRealPos;
    /** °´Å¥µÄÎ»ÖÃ */
    private float mBtnPos;
    /** ¿ª¹Ø´ò¿ªµÄÎ»ÖÃ */
    private float mBtnOnPos;
    /** ¿ª¹Ø¹Ø±ÕµÄÎ»ÖÃ */
    private float mBtnOffPos;
    /** ÒõÓ°µÄ¿í¶È */
    private float mMaskWidth;
    /** ÒõÓ°µÄ¸ß¶È */
    private float mMaskHeight;
    /** ¿ª¹ØÔ²ÐÎ°´Å¥µÄ¿í¶È */
    private float mBtnWidth;
    /** ¿ª¹Ø³õÊ¼×ø±ê */
    private float mBtnInitPos;

    private int mClickTimeout;

    private int mTouchSlop;
    /** ×î´óÍ¸Ã÷¶È£¬¾ÍÊÇ²»Í¸Ã÷ */
    private final int MAX_ALPHA = 255;
    /** µ±Ç°Í¸Ã÷¶È£¬ÕâÀïÖ÷ÒªÓÃÓÚÈç¹û¿Ø¼þµÄenableÊôÐÔÎªfalseÊ±ºòÉèÖÃ°ëÍ¸Ã÷ £¬¼´²»¿ÉÒÔµã»÷ */
    private int mAlpha = MAX_ALPHA;

    private boolean mChecked = false;

    private boolean mBroadcasting;

    private boolean mTurningOn;

    private PerformClick mPerformClick;
    /** ¿ª¹Ø×´Ì¬ÇÐ»»¼àÌý½Ó¿Ú */
    private OnCheckedChangeListener mOnCheckedChangeListener;

    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;
    /** ÅÐ¶ÏÊÇ·ñÔÚ½øÐÐ¶¯»­  */
    private boolean mAnimating;

    private final float VELOCITY = 350;
    /** »¬¶¯ËÙ¶È */
    private float mVelocity;

    private final float EXTENDED_OFFSET_Y = 15;
    /** YÖá·½ÏòÀ©´óµÄÇøÓò,Ôö´óµã»÷ÇøÓò */
    private float mExtendOffsetY;

    private float mAnimationPosition;

    private float mAnimatedVelocity;

    public CheckSwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public CheckSwitchButton(Context context) {
        this(context, null);
    }

    public CheckSwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        Resources resources = context.getResources();

        // get viewConfiguration
        mClickTimeout = ViewConfiguration.getPressedStateDuration()
                + ViewConfiguration.getTapTimeout();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        // get Bitmap
        mBottom = BitmapFactory.decodeResource(resources, R.drawable.checkswitch_bottom);
        mBtnPressed = BitmapFactory.decodeResource(resources,R.drawable.checkswitch_btn_pressed);
        mBtnNormal = BitmapFactory.decodeResource(resources,R.drawable.checkswitch_btn_unpressed);
        mFrame = BitmapFactory.decodeResource(resources, R.drawable.checkswitch_frame);
        mMask = BitmapFactory.decodeResource(resources, R.drawable.checkswitch_mask);
        mCurBtnPic = mBtnNormal;

        mBtnWidth = mBtnPressed.getWidth();
        mMaskWidth = mMask.getWidth();
        mMaskHeight = mMask.getHeight();

        mBtnOffPos = mBtnWidth / 2;
        mBtnOnPos = mMaskWidth - mBtnWidth / 2;
        // ÅÐ¶ÏÆðÊ¼Î»ÖÃ,Èç¹ûÉè¶¨ÁËmCheckedÎªtrue£¬ÆðÊ¼Î»ÖÃÎª mBtnOnPos
        mBtnPos = mChecked ? mBtnOnPos : mBtnOffPos;
        mRealPos = getRealPos(mBtnPos);
        // density ÃÜ¶È
        final float density = getResources().getDisplayMetrics().density;// ·½·¨ÊÇ»ñÈ¡×ÊÔ´ÃÜ¶È£¨Density£©
        mVelocity = (int) (VELOCITY * density + 0.5f);
        mExtendOffsetY = (int) (EXTENDED_OFFSET_Y * density + 0.5f);
        // ´´½¨Ò»¸öÐÂµÄ¾ØÐÎÓëÖ¸¶¨µÄ×ø±ê¡£
        mSaveLayerRectF = new RectF(0, mExtendOffsetY, mMask.getWidth(),
                mMask.getHeight() + mExtendOffsetY);
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);// PorterDuff.Mode.SRC_IN
        // £ºÕâ¸öÊôÐÔ´ú±í
        // È¡Á½²ã»æÖÆ½»¼¯¡£ÏÔÊ¾ÉÏ²ã¡£
    }

    @Override
    public void setEnabled(boolean enabled) {
        mAlpha = enabled ? MAX_ALPHA : MAX_ALPHA / 2;
        super.setEnabled(enabled);
    }

    public boolean isChecked() {
        return mChecked;
    }

    /** ×Ô¶¯ÅÐ¶ÏÇÐ»»ÖÁÏà·´µÄÊôÐÔ : true -->false ;false -->true */
    public void toggle() {
        setChecked(!mChecked);
    }

    /**
     * ÄÚ²¿µ÷ÓÃ´Ë·½·¨ÉèÖÃchecked×´Ì¬£¬´Ë·½·¨»áÑÓ³ÙÖ´ÐÐ¸÷ÖÖ»Øµ÷º¯Êý£¬±£Ö¤¶¯»­µÄÁ÷³©¶È
     *
     * @param checked
     */
    private void setCheckedDelayed(final boolean checked) {
        this.postDelayed(new Runnable() {

            @Override
            public void run() {
                setChecked(checked);
            }
        }, 10);
    }

    /**
     * <p>
     * Changes the checked state of this button.
     * </p>
     *
     * @param checked
     *            true to check the button, false to uncheck it
     */
    public void setChecked(boolean checked) {

        if (mChecked != checked) {
            mChecked = checked;

            mBtnPos = checked ? mBtnOnPos : mBtnOffPos;
            mRealPos = getRealPos(mBtnPos);
            invalidate();

            // Avoid infinite recursions if setChecked() is called from a
            // listener
            if (mBroadcasting) {
                return;
            }

            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(CheckSwitchButton.this,mChecked);
            }
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(CheckSwitchButton.this, mChecked);
            }

            mBroadcasting = false;
        }
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener
     *            the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes. This callback is used for internal purpose only.
     *
     * @param listener
     *            the callback to call on checked state change
     * @hide
     */
    void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeWidgetListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        float deltaX = Math.abs(x - mFirstDownX);
        float deltaY = Math.abs(y - mFirstDownY);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                attemptClaimDrag();
                mFirstDownX = x;
                mFirstDownY = y;
                mCurBtnPic = mBtnPressed;
                mBtnInitPos = mChecked ? mBtnOnPos : mBtnOffPos;
                break;
            case MotionEvent.ACTION_MOVE:
                // ÍÏ¶¯×ÅµÄÊ±¼ä
                float time = event.getEventTime() - event.getDownTime();
                // µ±Ç°°´Å¥µÄÎ»ÖÃ
                mBtnPos = mBtnInitPos + event.getX() - mFirstDownX;
                if (mBtnPos >= mBtnOffPos) {
                    mBtnPos = mBtnOffPos;
                }
                if (mBtnPos <= mBtnOnPos) {
                    mBtnPos = mBtnOnPos;
                }
                mTurningOn = mBtnPos > (mBtnOffPos - mBtnOnPos) / 2 + mBtnOnPos;

                mRealPos = getRealPos(mBtnPos);
                break;
            case MotionEvent.ACTION_UP:
                mCurBtnPic = mBtnNormal;
                time = event.getEventTime() - event.getDownTime();
                if (deltaY < mTouchSlop && deltaX < mTouchSlop
                        && time < mClickTimeout) {
                    if (mPerformClick == null) {
                        mPerformClick = new PerformClick();
                    }
                    if (!post(mPerformClick)) {
                        performClick();
                    }
                } else {
                    startAnimation(!mTurningOn);
                }
                break;
        }

        invalidate();
        return isEnabled();
    }

    private final class PerformClick implements Runnable {
        public void run() {
            performClick();
        }
    }

    @Override
    public boolean performClick() {
        startAnimation(!mChecked);
        return true;
    }

    /**
     * Í¨Öª¸¸Àà²»ÒªÀ¹½ØtouchÊÂ¼þ Tries to claim the user's drag motion, and requests
     * disallowing any ancestors from stealing events in the drag.
     */
    private void attemptClaimDrag() {
        mParent = getParent();
        if (mParent != null) {
            // Í¨Öª¸¸Àà²»ÒªÀ¹½ØtouchÊÂ¼þ
            mParent.requestDisallowInterceptTouchEvent(true);
        }
    }

    /**
     * ½«btnPos×ª»»³ÉRealPos
     *
     * @param btnPos
     * @return
     */
    private float getRealPos(float btnPos) {
        return btnPos - mBtnWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.saveLayerAlpha(mSaveLayerRectF, mAlpha, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        // »æÖÆÃÉ°å
        canvas.drawBitmap(mMask, 0, mExtendOffsetY, mPaint);
        mPaint.setXfermode(mXfermode);

        // »æÖÆµ×²¿Í¼Æ¬
        canvas.drawBitmap(mBottom, mRealPos, mExtendOffsetY, mPaint);
        mPaint.setXfermode(null);
        // »æÖÆ±ß¿ò
        canvas.drawBitmap(mFrame, 0, mExtendOffsetY, mPaint);

        // »æÖÆ°´Å¥
        canvas.drawBitmap(mCurBtnPic, mRealPos, mExtendOffsetY, mPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) mMaskWidth,
                (int) (mMaskHeight + 2 * mExtendOffsetY));
    }

    private void startAnimation(boolean turnOn) {
        mAnimating = true;
        mAnimatedVelocity = turnOn ? -mVelocity : mVelocity;
        mAnimationPosition = mBtnPos;

        new SwitchAnimation().run();
    }

    private void stopAnimation() {
        mAnimating = false;
    }

    private final class SwitchAnimation implements Runnable {

        @Override
        public void run() {
            if (!mAnimating) {
                return;
            }
            doAnimation();
            FrameAnimationController.requestAnimationFrame(this);
        }
    }

    private void doAnimation() {
        mAnimationPosition += mAnimatedVelocity * FrameAnimationController.ANIMATION_FRAME_DURATION / 1000;
        if (mAnimationPosition <= mBtnOnPos) {
            stopAnimation();
            mAnimationPosition = mBtnOnPos;
            setCheckedDelayed(true);
        } else if (mAnimationPosition >= mBtnOffPos) {
            stopAnimation();
            mAnimationPosition = mBtnOffPos;
            setCheckedDelayed(false);
        }
        moveView(mAnimationPosition);
    }

    private void moveView(float position) {
        mBtnPos = position;
        mRealPos = getRealPos(mBtnPos);
        invalidate();
    }
}
