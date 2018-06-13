package com.weikan.app.util.jump.actionImpl;

import android.content.Context;
import android.os.Bundle;

import com.weikan.app.util.LToast;
import com.weikan.app.util.jump.IAction;

import java.util.Map;


/**
 * Created by Lee on 2016/9/27.
 */
public class ErrorAction implements IAction {
    @Override
    public boolean execute(Context context, String schema, String path, Map<String, String> kvs, Bundle bundle) {
        LToast.showToast("当前版本过低，请升级至最新版本");
        return false;
    }
}
