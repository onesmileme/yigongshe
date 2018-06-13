package com.weikan.app.util.jump;

import android.content.Context;
import android.os.Bundle;

import java.util.Map;

/**
 * Created by Lee on 2016/9/27.
 */
public interface IAction {
    boolean execute(Context context, String schema, String path, Map<String, String> kvs, Bundle bundle);
}
