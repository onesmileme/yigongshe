package com.weikan.app.util.jump.actionImpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.weikan.app.original.OriginalDetailActivity;
import com.weikan.app.util.URLDefine;
import com.weikan.app.util.jump.IAction;

import java.util.Map;

/**
 * Created by Lee on 2016/12/26.
 */
public class OriginalDetailAction implements IAction {
    @Override
    public boolean execute(Context context, String schema, String path, Map<String, String> kvs, Bundle bundle) {
        if (kvs != null && kvs.containsKey("id")) {
            Intent intent = new Intent(context, OriginalDetailActivity.class);
            intent.putExtra(URLDefine.TID, kvs.get("id"));
            if (context instanceof Activity) {
                int code = bundle.getInt("requestCode");
                if (code != 0) {
                    ((Activity) context).startActivityForResult(intent, code);
                } else {
                    context.startActivity(intent);
                }
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
            return true;
        }
        return false;
    }
}