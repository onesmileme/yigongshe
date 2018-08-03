package com.ygs.android.yigongshe.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;


//import cn.jpush.android.api.JPushInterface;
//
//public class PushManager {
//    private static class Holder {
//        public static final PushManager inst = new PushManager();
//    }
//
//    public static PushManager getInstance() {
//        return Holder.inst;
//    }
//
//    //暂时保存每种类型对应的推送红点
//    private Map<Integer, Integer> pushCacheMap = new HashMap<>();
//
//    private PushManager() {
//    }
//
//    private String token;
//
//    public String getToken(Context context) {
//        if (TextUtils.isEmpty(token)) {
//            return JPushInterface.getRegistrationID(context);
//        } else {
//            return token;
//        }
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
//
//    protected void onPushMessage(PushObject obj) {
//        // 保存推送红点的cache，便于其他界面取
//        if (obj != null) {
//            if (obj.t != PushDefine.PUSH_TYPE_NOTIFICATION)
//                addPushObject(obj);
//        }
//        EventBus.getDefault().post(new PushEventObject(obj));
//    }
//
//
//    public int getPushObject(int type) {
//        if (pushCacheMap.containsKey(type)) {
//            return pushCacheMap.get(type);
//        }
//        return 0;
//    }
//
//    public void removePushObject(int type) {
//        if (pushCacheMap.containsKey(type)) {
//            pushCacheMap.remove(type);
//        }
//    }
//
//    public void addPushObject(PushObject object) {
//        if (!pushCacheMap.containsKey(object.t)) {
//            pushCacheMap.put(object.t, object.p.n);
//        } else {
//            int i = pushCacheMap.get(object.t);
//            pushCacheMap.put(object.t, object.p.n + i);
//        }
//    }
//
//    /**
//     * 解析push调起协议，默认直接跳转
//     *
//     * @param context
//     * @param schema
//     */
//    public void executePushJump(Context context, String schema) {
//        executePushJump(context, schema, false, "蓝鲸", "", null);
//    }
//
//    /**
//     * 解析push调起协议
//     *
//     * @param context
//     * @param schema
//     * @param showNotif
//     */
//    public void executePushJump(Context context, String schema, boolean showNotif, String title, String content, PushObject pushObject) {
//        if (TextUtils.isEmpty(schema)) {
//            return;
//        }
//        if (!schema.startsWith(PushDefine.SCHEMA)) {
//            return;
//        }
//        int pos = schema.indexOf("?");
//        String path = schema.substring((PushDefine.SCHEMA + "://").length(), pos > 0 ? pos : schema.length());
//        /*
//        Map<String, String> params = WebshellActivity.URLRequest(schema);
//        if (PushDefine.PATH_INDEX.equals(path)) {
//            Intent intent = new Intent(context, MainActivity.class);
//            callActivity(context, intent);
//        } else if (PushDefine.PATH_TWEET.equals(path)
//            || PushDefine.PATH_TWEETDETAIL.equals(path)) {
//            String tid = null;
//            if (params != null) {
//                tid = params.get("id");
//            }
//            if (!TextUtils.isEmpty(tid)) {
//                Intent intent = new Intent(context, OriginalDetailActivity.class);
//                intent.putExtra(BundleParamKey.TID, tid);
//                callActivity(context, intent);
//            }
//        } else if (PushDefine.PATH_MOMENTS.equals(path)) {
//            String tid = null;
//            if (params != null) {
//                tid = params.get("id");
//            }
//            if (!TextUtils.isEmpty(tid)) {
//                Intent intent = new Intent(context, WenyouDetailActivity.class);
//                intent.putExtra(URLDefine.TID, tid);
//                callActivity(context, intent);
//            }
//        } else if (PushDefine.PATH_TOPICS.equals(path)) {
//            String tid = null;
//            if (params != null) {
//                tid = params.get("id");
//            }
//            if (!TextUtils.isEmpty(tid)) {
//                Intent intent = new Intent(context, WenyouTopicListActivity.class);
//                intent.putExtra(URLDefine.TOPIC_ID, tid);
//                callActivity(context, intent);
//            }
//        } else if (PushDefine.PATH_LIVE_PLAY.equals(path)) {
//            String tid = null;
//            if (params != null) {
//                tid = params.get(URLDefine.LIVE_ID);
//            }
//            if (!TextUtils.isEmpty(tid)) {
//                Intent intent = new Intent(context, LivePlayActivity.class);
//                intent.putExtra(URLDefine.LIVE_ID, tid);
//                callActivity(context, intent);
//            }
//        } else if (PushDefine.PATH_SMSG.equals(path)) {
//            Intent intent = new Intent(context, MyMsgActivity.class);
//            callActivity(context, intent);
//        } else if (PushDefine.PATH_MYFANS.equals(path)) {
//            Intent intent = new Intent(context, MyAttentionActivity.class);
//            intent.putExtra("bundle_type", 2);
//            callActivity(context, intent);
//        } else if (PushDefine.PATH_TALK.equals(path)) {
//            Intent intent = new Intent(context, TalkActivity.class);
//            if(params!=null && params.containsKey("search_uid")) {
//                intent.putExtra(URLDefine.UID, params.get("search_uid"));
//                String name = "";
//                try {
//                    name = URLDecoder.decode(params.get("nickname"),"UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                intent.putExtra(URLDefine.UNAME, name);
//                callActivity(context, intent);
//            }
//        } else if (PushDefine.PATH_WAP.equals(path)) {
//            String u = null;
//            if (params != null) {
//                u = params.get("url");
//            }
//            if (!TextUtils.isEmpty(u)) {
//                u = URLDecoder.decode(u);
//                Intent intent = new Intent(context, WebshellActivity.class);
//                intent.putExtra(BundleParamKey.URL, u);
//                callActivity(context, intent);
//            }
//        } else if (PushDefine.PATH_USER_VERIFY.equals(path)) {
//            Intent intent = new Intent(context, MainActivity.class);
//            intent.putExtra(MainActivity.BUNDLE_SHOULD_UPDATE_USER_VERIFY, true);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            context.startActivity(intent);
//        }
//        */
//    }
//
//    /**
//     * 发送notif或者直接调起
//     *
//     * @param context
//     */
//    private void callActivity(Context context, Intent intent) {
//        if(! (context instanceof Activity)) {
//            // 外部调起要加flags
//            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        }
//        context.startActivity(intent);
//    }
//}
