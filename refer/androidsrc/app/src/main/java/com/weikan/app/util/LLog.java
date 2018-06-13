package com.weikan.app.util;

import android.util.Log;
import com.weikan.app.BuildConfig;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/3/8
 * Time: 14:23
 */
public class LLog {
    private static final String TAG = "kankan";
    public static void i(String s){
        if(BuildConfig.DEBUG){
            Log.i(TAG,s);
        }
    }
    public static void d(String s){
         if(BuildConfig.DEBUG){
             Log.d(TAG,s);
         }
    }
    public static void e(String s){
        if(BuildConfig.DEBUG){
            Log.e(TAG,s);
        }
    }
}
