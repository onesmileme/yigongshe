package com.weikan.app.push;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.weikan.app.MainActivity;
import com.weikan.app.account.AccountManager;
import com.weikan.app.util.SchemaUtil;

import platform.push.MessageConsts;

import java.util.List;

/**
 * Created by liujian on 16/11/6.
 */
public class SvenPushReceiver extends BroadcastReceiver {
    private static final String TAG = "SvenPushReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        Log.d(TAG, "[SvenPushReceiver] onReceive - " + intent.getAction() + ", extras: " + (bundle != null ? bundle.getString(PushDefine.SCHEMA) : ""));
        if(MessageConsts.ACTION_MESSAGE_OPENED.equals(intent.getAction())){
            // 用户点击打开了notification
            String schema = "";
            if(bundle!=null){
                schema = bundle.getString("schema");
            }
            if(!TextUtils.isEmpty(schema)){
                boolean isTop = false;
                // 不在前台要把主界面先调起
                if(!isTopActivy(context)) {
                    Intent mainIntent = new Intent(context, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(mainIntent);
                } else {
                    isTop = true;
                }
                PushManager.getInstance().executePushJump(context, schema);
            }

        } else if(MessageConsts.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())){
            // 接收到了通知
            String schema = "";
            if(bundle != null){
                schema = bundle.getString("schema");
                if (!TextUtils.isEmpty(schema)) {
                    String path = SchemaUtil.parsePath(schema);

                    // 更新用户身份认证信息
                    if (TextUtils.equals(path, PushDefine.PATH_USER_VERIFY)) {
                        AccountManager.getInstance().updateUserInfo();
                    }
                }
            }


            // do nothing
        }
    }

    public static boolean isTopActivy(Context context) {
        String packageName = context.getPackageName();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        String cmpNameTemp = null;
        if (null != runningTaskInfos && runningTaskInfos.size()>0) {
            cmpNameTemp = (runningTaskInfos.get(0).topActivity).toString();
        }
        if (null == cmpNameTemp)
            return false;
        if (cmpNameTemp.indexOf(packageName) != -1)
            return true;
        else
            return false;
    }
}
