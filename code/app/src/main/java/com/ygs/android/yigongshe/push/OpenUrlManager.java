package com.ygs.android.yigongshe.push;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.ui.activity.ActivityDetailActivity;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.dynamic.DynamicDetailActivity;
import com.ygs.android.yigongshe.ui.otherhomepage.OtherHomePageActivity;
import com.ygs.android.yigongshe.ui.profile.message.MsgTalkActivity;
import com.ygs.android.yigongshe.webview.WebViewActivity;

public class OpenUrlManager {

    private static OpenUrlManager manager = new OpenUrlManager();

  /*
    yigongshe://dynamicdetail?newId=xxx  动态详情
    yigongshe://acitivitydetail?inewsId=xx 活动详情
    yigongshe://message?otherId=xx 消息
    yigongshe://notice?otherId=xx  通知
   */

    public static final String SCHEME = "yigongshe";
    public static final String GOTO_OTHER_HOMEPAGE = "otherpage";
    public static final String DYNAMIC_DETAIL = "dynamicdetail";
    public static final String ACTIVITY_DETAIL = "activitydetail";
    public static final String MESSAGE_DETAIL = "message";
    public static final String NOTICE_DETAIL = "notice";

    public static OpenUrlManager manager() {
        return manager;
    }

    public static void handle(@NonNull Uri uri) {

        String scheme = uri.getScheme().toLowerCase();
        if (TextUtils.isEmpty(scheme)){
            return;
        }
        if (SCHEME.equals(scheme)) {
            manager.handleYigongshe(uri,false,0);
        } else if (scheme.startsWith("http")) {
            manager.openWebview(uri);
        }
    }
    public static void handleUrlForResult(@NonNull Uri uri , int resultCode ){

        String scheme = uri.getScheme().toLowerCase();
        if (TextUtils.isEmpty(scheme)){
            return;
        }
        if (SCHEME.equals(scheme)) {
            manager.handleYigongshe(uri,true,resultCode);
        } else if (scheme.startsWith("http")) {
            manager.openWebview(uri);
        }

    }

    public static Uri makeUri(String host , String query){

        if (!TextUtils.isEmpty(host)){
            return null;
        }

        StringBuilder sb = new StringBuilder(SCHEME);
        sb.append("://");
        sb.append(host);
        if (!TextUtils.isEmpty(query)) {
            sb.append("?");
            sb.append(query);
        }
        return Uri.parse(sb.toString());
    }

    private void handleYigongshe(Uri uri,boolean forResultCode , int resultCode) {
        String detailId = "";
        if (uri != null) {
            String host = uri.getHost();
            if (TextUtils.isEmpty(host)) {
                return;
            }
            if (GOTO_OTHER_HOMEPAGE.equals(host)) {
                String uid = uri.getQueryParameter("uid");
                Bundle bundle = new Bundle();
                bundle.putString("userid", uid);
                openActivity(OtherHomePageActivity.class, bundle,forResultCode,resultCode);
                return;
            } else if (DYNAMIC_DETAIL.equals(host)) {
                detailId = uri.getQueryParameter("newsId");
                if (!TextUtils.isEmpty(detailId)) {
                    try {
                        int mId = Integer.parseInt(detailId);
                        Bundle bundle = new Bundle();
                        bundle.putInt("news_id", mId);
                        openActivity(DynamicDetailActivity.class, bundle,forResultCode,resultCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (ACTIVITY_DETAIL.equals(host)) {
                detailId = uri.getQueryParameter("inewsId");
                if (detailId == null) {
                    detailId = uri.getQueryParameter("newsId");
                }
                if (!TextUtils.isEmpty(detailId)) {
                    try {
                        int mId = Integer.parseInt(detailId);
                        Bundle bundle = new Bundle();
                        bundle.putInt("activity_id", mId);
                        openActivity(ActivityDetailActivity.class, bundle,forResultCode,resultCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (MESSAGE_DETAIL.equals(host)) {
                detailId = uri.getQueryParameter("messageid");
                if (!TextUtils.isEmpty(detailId)) {
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putString("otherUid", detailId);
                        bundle.putString("type", "message");
                        openActivity(MsgTalkActivity.class, bundle,forResultCode,resultCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (NOTICE_DETAIL.equals(host)) {
                detailId = uri.getQueryParameter("noticeid");
                if (!TextUtils.isEmpty(detailId)) {
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putString("otherUid", detailId);
                        bundle.putString("type", "notice");
                        openActivity(MsgTalkActivity.class, bundle,forResultCode,resultCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void openWebview(Uri uri) {
        Bundle bundle = new Bundle();
        bundle.putString(WebViewActivity.URL_KEY, uri.toString());
        openActivity(WebViewActivity.class, bundle,false,0);
    }



    private void openActivity(Class<? extends Activity> activityClass, Bundle bundle,boolean forResultCode , int resultCode) {

        Intent intent = new Intent(YGApplication.mApplication, activityClass);
        intent.putExtra(BaseActivity.PARAM_INTENT, bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (forResultCode){
            YGApplication.mMainActivity.startActivityForResult(intent,resultCode);
        }else {
            YGApplication.mApplication.startActivity(intent, bundle);
        }
    }
}
