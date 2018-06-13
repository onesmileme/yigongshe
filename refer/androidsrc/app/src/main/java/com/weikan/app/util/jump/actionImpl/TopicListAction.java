package com.weikan.app.util.jump.actionImpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.weikan.app.util.URLDefine;
import com.weikan.app.util.jump.IAction;
import com.weikan.app.wenyouquan.WenyouTopicListActivity;

import java.util.Map;

/**
 * Created by Lee on 2016/12/26.
 */
public class TopicListAction implements IAction {
    @Override
    public boolean execute(Context context, String schema, String path, Map<String, String> kvs, Bundle bundle) {
        if (kvs != null && kvs.containsKey("id")) {
            Intent intent = new Intent(context, WenyouTopicListActivity.class);
            intent.putExtra(URLDefine.TOPIC_ID, kvs.get("id"));
            if (!(context instanceof Activity)) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            context.startActivity(intent);
            return true;
        }
        return false;
    }
}