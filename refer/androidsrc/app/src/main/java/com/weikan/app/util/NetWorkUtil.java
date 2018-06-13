package com.weikan.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.weikan.app.MainApplication;

/**
 * Created by yuzhiboyou on 16-1-17.
 */
public class NetWorkUtil {
    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) MainApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}
