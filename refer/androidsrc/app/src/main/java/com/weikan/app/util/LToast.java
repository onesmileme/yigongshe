package com.weikan.app.util;

import android.widget.Toast;
import com.weikan.app.BuildConfig;
import com.weikan.app.MainApplication;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/3/8
 * Time: 14:21
 */
public class LToast {
    public static void showToast(String text){
        Toast.makeText(MainApplication.getInstance().getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int textRes){
        Toast.makeText(MainApplication.getInstance().getApplicationContext(),textRes,Toast.LENGTH_SHORT).show();
    }

    public static void showDebugToast(String text){
        if(BuildConfig.DEBUG){
            showToast(text);
        }
    }
}
