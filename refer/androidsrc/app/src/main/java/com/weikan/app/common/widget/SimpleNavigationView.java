package com.weikan.app.common.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.weikan.app.R;


/**
 * 简单的导航栏View，包含以下子View
 *
 * 左侧的TextView和ImageView：{@link #tvNavigationLeft}、{@link #ivNavigationLeft}
 * 标题位置的TextView和ImageView：{@link #tvNavigationTitle}、{@link #ivNavigationTitle}
 * 右侧的TextView和ImageView：{@link #tvNavigationRight}、{@link #ivNavigationRight}
 *
 * 如果你需要一个普通的导航栏，通常用这个足够了；如果需要更多的可定制型，可以看基类{@link NavigationView}
 *
 * 本类在Xml中可用的属性有：
 *
 * R.styleable.NavigationView_navigationLeftText
 * R.styleable.NavigationView_navigationLeftTextSize
 * R.styleable.NavigationView_navigationLeftTextColor
 * R.styleable.NavigationView_navigationLeftDrawable
 *
 * R.styleable.NavigationView_navigationTitleText
 * R.styleable.NavigationView_navigationTitleTextSize
 * R.styleable.NavigationView_navigationTitleTextColor
 * R.styleable.NavigationView_navigationTitleDrawable
 *
 * R.styleable.NavigationView_navigationRightText
 * R.styleable.NavigationView_navigationRightTextSize
 * R.styleable.NavigationView_navigationRightTextColor
 * R.styleable.NavigationView_navigationRightDrawable
 *
 * @author kailun on 16/11/8.
 */
public class SimpleNavigationView extends NavigationView
        implements IHasLayoutResource {

    @Override
    public int layoutResourceId() {
        return R.layout.widget_simple_navigation_view;
    }

    public SimpleNavigationView(Context context) {
        super(context);
        initViews(null);
    }

    public SimpleNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public SimpleNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    /**
     * SimpleNavigationView在构造方法中已经完成了这个事儿
     */
    @Override
    public void initNavigationView() {
        // do nothing
    }

    private void assignAttrs(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NavigationView);

            // 左边的按钮
            String leftText = a.getString(R.styleable.NavigationView_navigationLeftText);
            Drawable leftDrawable = a.getDrawable(R.styleable.NavigationView_navigationLeftDrawable);
            setTextOrDrawable(tvNavigationLeft, ivNavigationLeft, leftText, leftDrawable);
            setTextSize(tvNavigationLeft, a, R.styleable.NavigationView_navigationLeftTextSize);
            setTextColor(tvNavigationLeft, a, R.styleable.NavigationView_navigationLeftTextColor);

            // 标题
            String titleText = a.getString(R.styleable.NavigationView_navigationTitleText);
            Drawable titleDrawable = a.getDrawable(R.styleable.NavigationView_navigationTitleDrawable);
            setTextOrDrawable(tvNavigationTitle, ivNavigationTitle, titleText, titleDrawable);
            setTextSize(tvNavigationTitle, a, R.styleable.NavigationView_navigationTitleTextSize);
            setTextColor(tvNavigationTitle, a, R.styleable.NavigationView_navigationTitleTextColor);

            // 右边的按钮
            String rightText = a.getString(R.styleable.NavigationView_navigationRightText);
            Drawable rightDrawable = a.getDrawable(R.styleable.NavigationView_navigationRightDrawable);
            setTextOrDrawable(tvNavigationRight, ivNavigationRight, rightText, rightDrawable);
            setTextSize(tvNavigationRight, a, R.styleable.NavigationView_navigationRightTextSize);
            setTextColor(tvNavigationRight, a, R.styleable.NavigationView_navigationRightTextColor);

            a.recycle();
        }
    }

    private void initViews(@Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(layoutResourceId(), this);
        assignViews();
        assignAttrs(attrs);
    }

    private void enableTintMode() {
        if (!isInEditMode()) {
            Activity activity = (Activity) getContext();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Window window = activity.getWindow();

                window.clearFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
                window.setStatusBarColor(activity.getResources().getColor(R.color.news_titlebar_bg));
            }
        }
    }
}
