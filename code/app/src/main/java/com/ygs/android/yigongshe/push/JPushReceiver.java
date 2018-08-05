package com.ygs.android.yigongshe.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

//*
public class JPushReceiver extends BroadcastReceiver {
    private final String TAG = "JPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

                if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                    String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                    Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                    PushManager.getInstance().setToken(regId);
                } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                    Log.e(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                    String text = "收到消息:" + bundle.toString();
                    String pushTitle = bundle.getString(JPushInterface.EXTRA_TITLE);
                    String pushAlert = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                    String pushText = bundle.getString(JPushInterface.EXTRA_EXTRA);
                    if (!TextUtils.isEmpty(pushText)) {
                        PushObject pushObject = null;
                        try {
                            pushObject = com.alibaba.fastjson.JSONObject.parseObject(pushText, PushObject.class);
                        } catch (com.alibaba.fastjson.JSONException e) {
                            e.printStackTrace();
                        }
                        if (pushObject != null) {
                            switch (pushObject.t) {
                                case 1: {
                                    if (pushObject.p != null) {
                                        String schema = pushObject.p.j;
                                        PushManager.getInstance().executePushJump(context, schema, true, pushTitle, pushAlert, pushObject);
                                    }
                                    break;
                                }
                                case 2: {
                                    PushManager.getInstance().onPushMessage(pushObject);
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        }
                    }
                    Log.d(TAG,text);
                } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                    Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                    int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                    Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
                } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                    Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
                } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                    Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                    boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                    Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
                } else {
                    Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
                }
    }

     //打印所有的 intent extra 数据
        private static String printBundle(Bundle bundle) {
            StringBuilder sb = new StringBuilder();
            for (String key : bundle.keySet()) {
                if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                    sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
                } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                    sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
                } else {
                    sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
                }
            }
            return sb.toString();
        }
}
//*/
