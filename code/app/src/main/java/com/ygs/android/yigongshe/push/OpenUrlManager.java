package com.ygs.android.yigongshe.push;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.ui.activity.ActivityDetailActivity;
import com.ygs.android.yigongshe.ui.dynamic.DynamicDetailActivity;
import com.ygs.android.yigongshe.ui.profile.message.MsgTalkActivity;
import com.ygs.android.yigongshe.webview.WebViewActivity;

import java.net.URI;

public class OpenUrlManager {
    
    private static OpenUrlManager manager = new OpenUrlManager();
    
    public static OpenUrlManager manager(){
        return manager;
    }
    
    public static void handle(@NonNull Uri uri){

        String scheme = uri.getScheme().toLowerCase();
        
        if (scheme.equals("yigongshe")){
            manager.handleYigongshe(uri);
        }else if (scheme.startsWith("http")){
            manager.openWebview(uri);
        }
    }
    
    private void handleYigongshe(Uri uri){
        String detailId = "";
        if (uri != null) {
            //1..........

            detailId = uri.getQueryParameter("dynamicdetailid");
            if (!TextUtils.isEmpty(detailId)) {
                try {
                    int mId = Integer.parseInt(detailId);
                    Bundle bundle = new Bundle();
                    bundle.putInt("news_id", mId);
                    openActivity(DynamicDetailActivity.class, bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //2.............
            detailId = uri.getQueryParameter("activitydetailid");
            if (!TextUtils.isEmpty(detailId)) {
                try {
                    int mId = Integer.parseInt(detailId);
                    Bundle bundle = new Bundle();
                    bundle.putInt("activity_id", mId);
                    openActivity(ActivityDetailActivity.class, bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //3.............
            detailId = uri.getQueryParameter("messageid");
            if (!TextUtils.isEmpty(detailId)) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("otherUid", detailId);
                    bundle.putString("type", "message");
                    openActivity(MsgTalkActivity.class, bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //4.............
            detailId = uri.getQueryParameter("noticeid");
            if (!TextUtils.isEmpty(detailId)) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("otherUid", detailId);
                    bundle.putString("type", "notice");
                    openActivity(MsgTalkActivity.class, bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void openWebview(Uri uri){

        Bundle bundle = new Bundle();
        bundle.putString(WebViewActivity.URL_KEY,uri.toString());
        openActivity(WebViewActivity.class,bundle);
        
    }
    
    private void openActivity(Class<? extends Activity> activityClass , Bundle bundle){

        Intent intent = new Intent(YGApplication.mApplication,activityClass);
        YGApplication.mApplication.startActivity(intent,bundle);
        
    }
    
    
    
}
