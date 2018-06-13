package com.weikan.app.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

/**
 * 动态创建各种圆角背景工具
 * Created by liujian on 17/1/8.
 */
public class DrawableUtils {
    public static Drawable makeSolidCornerShape(Context context, int solidColor){
        GradientDrawable gd = new GradientDrawable();//创建drawable
        gd.setColor(solidColor);
        gd.setCornerRadius(Global.dpToPx(context, 15));
        gd.setBounds(Global.dpToPx(context,3),Global.dpToPx(context,5),Global.dpToPx(context,5),Global.dpToPx(context,3));
        return gd;
    }
}
