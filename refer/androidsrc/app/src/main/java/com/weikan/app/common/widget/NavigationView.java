package com.weikan.app.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.R;
import com.weikan.app.listener.OnNoRepeatClickListener;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

/**
 * 未指定布局的导航栏，包括并不限于以下子View
 *
 * 左侧的TextView和ImageView：{@link #tvNavigationLeft}、{@link #ivNavigationLeft}
 * 标题位置的TextView和ImageView：{@link #tvNavigationTitle}、{@link #ivNavigationTitle}
 * 右侧的TextView和ImageView：{@link #tvNavigationRight}、{@link #ivNavigationRight}
 *
 * 以上，每一组TextView和ImageView都是互斥的。
 * 比如说，如果左侧显示了一个返回的图标（ImageView），那么文字（TextView）就是GONE的；
 * 反之，如果显示了返回两个字，那么图标就是GONE的。
 *
 * 上面每一个子View，都可以是缺失的（null），本类中的所有逻辑都对他们做了判空操作；
 *
 * 如果你需要一个普通的导航栏，只包括以上6个子View（或者更少），你只需要使用它的子类{@link SimpleNavigationView}，
 * 通常这个足够了。
 *
 * 如果你需要一个定制化程度较高的导航栏，那么你需要在布局Xml中像普通ViewGroup那样使用本类，并根据你的需求添加子View。
 * 当需要用到上述6个子View之一时，建议使用以下名称，以便于重用本类的方法：
 * tv_navigation_left, iv_navigation_left
 * tv_navigation_title, iv_navigation_title
 * tv_navigation_right, iv_navigation_right
 *
 * @author kailun on 16/11/8.
 */
public class NavigationView extends RelativeLayout {

    ImageView ivNavigationLeft = null;
    TextView tvNavigationLeft = null;
    ImageView ivNavigationTitle = null;
    TextView tvNavigationTitle = null;
    ImageView ivNavigationRight = null;
    TextView tvNavigationRight = null;

    OnClickListener leftOnClickListener = null;
    OnClickListener rightOnClickListener = null;

    // region 左侧的ImageView和TextView

    @Nullable
    public TextView getLeftTextView() {
        return tvNavigationLeft;
    }

    @Nullable
    public ImageView getLeftImageView() {
        return ivNavigationLeft;
    }

    public void setLeftText(@Nullable String text) {
        setTextOrDrawable(tvNavigationLeft, ivNavigationLeft, text, null);
    }

    public void setLeftDrawable(@Nullable Drawable drawable) {
        setTextOrDrawable(tvNavigationLeft, ivNavigationLeft, null, drawable);
    }

    @Nullable
    public OnClickListener getLeftOnClickListener() {
        return leftOnClickListener;
    }

    public void setLeftOnClickListener(@Nullable OnClickListener listener) {
        setLeftOnClickListener(listener, 0);
    }

    public void setLeftOnClickListener(@Nullable final OnClickListener listener, long duration) {
        leftOnClickListener = listener;
        setOnClickListenerWithDuration(tvNavigationLeft, listener, duration);
        setOnClickListenerWithDuration(ivNavigationLeft, listener, duration);
    }

    // endregion

    // region 标题的ImageView和TextView

    @Nullable
    public TextView getTitleTextView() {
        return tvNavigationTitle;
    }

    @Nullable
    public ImageView getTitleImageView() {
        return ivNavigationTitle;
    }

    public void setTitleText(@Nullable String text) {
        setTextOrDrawable(tvNavigationTitle, ivNavigationTitle, text, null);
    }

    public void setTitleDrawable(@Nullable Drawable drawable) {
        setTextOrDrawable(tvNavigationTitle, ivNavigationTitle, null, drawable);
    }

    // endregion

    // region 右侧的ImageView和TextView

    @Nullable
    public TextView getRightTextView() {
        return tvNavigationRight;
    }

    @Nullable
    public ImageView getRightImageView() {
        return ivNavigationRight;
    }

    public void setRightText(@Nullable String text) {
        setTextOrDrawable(tvNavigationRight, ivNavigationRight, text, null);
    }

    public void setRightDrawable(@Nullable Drawable drawable) {
        setTextOrDrawable(tvNavigationRight, ivNavigationRight, null, drawable);
    }

    @Nullable
    public OnClickListener getRightOnClickListener() {
        return rightOnClickListener;
    }

    public void setRightOnClickListener(@Nullable OnClickListener listener) {
        setRightOnClickListener(listener, 0);
    }

    public void setRightOnClickListener(@Nullable final OnClickListener listener, long duration) {
        rightOnClickListener = listener;
        setOnClickListenerWithDuration(tvNavigationRight, listener, duration);
        setOnClickListenerWithDuration(ivNavigationRight, listener, duration);
    }
    public void setRightOnClickListener1(@Nullable final OnClickListener listener) {
        tvNavigationRight.setOnClickListener(new OnNoRepeatClickListener() {
            @Override
            public void onNoRepeatClick(View v) {
                listener.onClick(v);
            }
        });
    }
    // endregion

    public NavigationView(Context context) {
        super(context);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void assignViews() {
        ivNavigationLeft = (ImageView) findViewById(R.id.iv_navigation_left);
        tvNavigationLeft = (TextView) findViewById(R.id.tv_navigation_left);
        ivNavigationTitle = (ImageView) findViewById(R.id.iv_navigation_title);
        tvNavigationTitle = (TextView) findViewById(R.id.tv_navigation_title);
        ivNavigationRight = (ImageView) findViewById(R.id.iv_navigation_right);
        tvNavigationRight = (TextView) findViewById(R.id.tv_navigation_right);
    }

    /**
     * 在导航栏加载到页面上之后
     * 调用这个方法可以初始化各个子View
     */
    public void initNavigationView() {
        assignViews();
    }

    protected void setTextOrDrawable(TextView textView, ImageView imageView, String text, Drawable drawable) {
        if (textView != null) {
            if (!TextUtils.isEmpty(text)) {
                textView.setText(text);
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        if (imageView != null) {
            if (drawable != null) {
                imageView.setImageDrawable(drawable);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setImageDrawable(null);
                imageView.setVisibility(View.GONE);
            }
        }
    }

    protected void setTextSize(TextView textView, TypedArray typedArray, @StyleableRes int styleable) {
        if (textView != null) {
            if (typedArray.hasValue(styleable)) {
                float textSize = typedArray.getDimension(styleable, 0);
                textView.setTextSize(textSize);
            }
        }
    }

    protected void setTextColor(TextView textView, TypedArray typedArray, @StyleableRes int styleable) {
        if (textView != null) {
            if (typedArray.hasValue(styleable)) {
                int color = typedArray.getColor(styleable, 0);
                textView.setTextColor(color);
            }
        }
    }

    protected void setOnClickListenerWithDuration(@Nullable final View view,
                                                  @Nullable final OnClickListener listener,
                                                  long duration) {
        if (view != null) {
            Observable<Void> observable = RxView.clicks(view);
            if (duration > 0) {
                observable.throttleFirst(duration, TimeUnit.MILLISECONDS);
            }
            if (listener != null) {
                observable.subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onClick(tvNavigationLeft);
                    }
                });
            } else {
                observable.subscribe();
            }
        }
    }


}
