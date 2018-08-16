package com.ygs.android.yigongshe.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.ui.activity.ActivityDetailActivity;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.dynamic.DynamicDetailActivity;
import com.ygs.android.yigongshe.ui.otherhomepage.OtherHomePageActivity;
import com.ygs.android.yigongshe.ui.profile.message.MsgTalkActivity;
import com.ygs.android.yigongshe.webview.WebViewActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * @author chunhui
 */

public class PushManager {

    public static final String SCHEME = "yigongshe";
    public static final String GOTO_OTHER_HOMEPAGE = "otherpage";
    public static final String DYNAMIC_DETAIL = "dynamicdetail";
    public static final String ACTIVITY_DETAIL = "activitydetail";
    public static final String MESSAGE_DETAIL = "message";
    public static final String NOTICE_DETAIL = "notice";

    private static class Holder {
        public static final PushManager INSTANCE = new PushManager();
    }

    public static PushManager getInstance() {
        return Holder.INSTANCE;
    }

    //暂时保存每种类型对应的推送红点
    private Map<Integer, Integer> pushCacheMap = new HashMap<>();

    private PushManager() {
    }

    private String token;

    public static void handle(@NonNull Uri uri) {

        String scheme = uri.getScheme().toLowerCase();
        if (TextUtils.isEmpty(scheme)) {
            return;
        }
        if (SCHEME.equals(scheme)) {
            getInstance().handleYigongshe(uri, false, 0);
        } else if (scheme.startsWith("http")) {
            getInstance().openWebview(uri);
        }
    }

    public static void handleUrlForResult(@NonNull Uri uri, int resultCode) {

        String scheme = uri.getScheme().toLowerCase();
        if (TextUtils.isEmpty(scheme)) {
            return;
        }
        if (SCHEME.equals(scheme)) {
            getInstance().handleYigongshe(uri, true, resultCode);
        } else if (scheme.startsWith("http")) {
            getInstance().openWebview(uri);
        }
    }


    public static Uri makeUri(String host, String query) {

        if (TextUtils.isEmpty(host)) {
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

    private void handleYigongshe(Uri uri, boolean forResultCode, int resultCode) {
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
                openActivity(OtherHomePageActivity.class, bundle, forResultCode, resultCode);
            } else if (DYNAMIC_DETAIL.equals(host)) {
                detailId = uri.getQueryParameter("newsId");
                if (!TextUtils.isEmpty(detailId)) {
                    try {
                        int mId = Integer.parseInt(detailId);
                        Bundle bundle = new Bundle();
                        bundle.putInt("news_id", mId);
                        openActivity(DynamicDetailActivity.class, bundle, forResultCode, resultCode);
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
                        openActivity(ActivityDetailActivity.class, bundle, forResultCode, resultCode);
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
                        openActivity(MsgTalkActivity.class, bundle, forResultCode, resultCode);
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
                        openActivity(MsgTalkActivity.class, bundle, forResultCode, resultCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public String getToken(Context context) {
        if (TextUtils.isEmpty(token)) {
            return JPushInterface.getRegistrationID(context);
        } else {
            return token;
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    protected void onPushMessage(PushObject obj) {
        // 保存推送红点的cache，便于其他界面取
        if (obj != null) {
            if (obj.t != PushDefine.PUSH_TYPE_NOTIFICATION) {
                addPushObject(obj);
            }
        }
        EventBus.getDefault().post(new PushEventObject(obj));
    }

    public int getPushObject(int type) {
        if (pushCacheMap.containsKey(type)) {
            return pushCacheMap.get(type);
        }
        return 0;
    }

    public void removePushObject(int type) {
        if (pushCacheMap.containsKey(type)) {
            pushCacheMap.remove(type);
        }
    }

    public void addPushObject(PushObject object) {
        if (!pushCacheMap.containsKey(object.t)) {
            // pushCacheMap.put(object.t, object.p.n);
        } else {
            int i = pushCacheMap.get(object.t);
            // pushCacheMap.put(object.t, object.p.n + i);
        }
    }

    /**
     * 解析push调起协议，默认直接跳转
     *
     * @param context
     * @param schema
     */
    public void executePushJump(Context context, String schema) {
        executePushJump(context, schema, false, "青年益工社", "", null);
    }

    /**
     * 解析push调起协议
     *
     * @param context
     * @param url
     * @param showNotif
     */
    public void executePushJump(Context context, String url, boolean showNotif, String title, String content,
                                PushObject pushObject) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        Uri uri = Uri.parse(url);
        if (uri != null) {
            handle(uri);
        }
    }

    private void openWebview(Uri uri) {
        Bundle bundle = new Bundle();
        bundle.putString(WebViewActivity.URL_KEY, uri.toString());
        openActivity(WebViewActivity.class, bundle, false, 0);
    }

    private void openActivity(Class<? extends Activity> activityClass, Bundle bundle, boolean forResultCode,
                              int resultCode) {

        Intent intent = new Intent(YGApplication.mApplication, activityClass);
        intent.putExtra(BaseActivity.PARAM_INTENT, bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (forResultCode) {
            YGApplication.mMainActivity.startActivityForResult(intent, resultCode);
        } else {
            YGApplication.mApplication.startActivity(intent, bundle);
        }
    }

    /**
     * 发送notif或者直接调起
     *
     * @param context
     */
    private void callActivity(Context context, Intent intent) {
        if (!(context instanceof Activity)) {
            // 外部调起要加flags
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }
}
