package com.weikan.app.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/3/8
 * Time: 13:54
 */
public class Global {
    private static class Holder {
        public static final Global inst = new Global();
    }
    public static Global getInstance(){
        return Holder.inst;
    }

    private Global(){

    }

    public int SCREEN_WIDTH = 480;
    public int SCREEN_HEIGHT = 800;
    public float SCREEN_DENSITY = 1.5f;
    public int SCREEN_DPI = 240;
    private boolean isInited = false;

    public void initScreenParam(Activity context){
        if(!isInited) {
            DisplayMetrics metrics = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            SCREEN_WIDTH = metrics.widthPixels;  // 屏幕宽度（像素）
            SCREEN_HEIGHT = metrics.heightPixels;  // 屏幕高度（像素）
            SCREEN_DENSITY = metrics.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
            SCREEN_DPI = metrics.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
            isInited = true;
        }
    }


    public static int dpToPx(Context context, int dp)
    {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

}
