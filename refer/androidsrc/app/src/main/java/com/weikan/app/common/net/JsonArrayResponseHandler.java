package com.weikan.app.common.net;

import android.support.annotation.NonNull;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import platform.http.responsehandler.AbstractJsonResponseHandler;
import platform.http.result.IResult;
import platform.http.result.JsonParseFailedResult;
import platform.http.result.ProcessResult;
import platform.http.result.SucceedResult;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 数组形式Json Response处理器
 * 用于处理只包括errno, msg和data的Json，并且data结点是一个数组
 * 它的抽象方法success返回的是data结点解析后得到的对象
 * {
 *     "errno": 0,
 *     "msg": "",
 *     "data": [
 *     ]
 * }
 * @author kailun on 16/8/31.
 */
public abstract class JsonArrayResponseHandler<T> extends AbstractJsonResponseHandler {

    @Override
    protected IResult handleProcessResult(@NonNull ProcessResult processResult) {
        ParameterizedType superType = (ParameterizedType)getClass().getGenericSuperclass();
        Type[] params = superType.getActualTypeArguments();

        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) params[0];

        ArrayList<T> data;
        if (processResult.rootObject.data == null) {
            data = new ArrayList<>();
            SucceedResult r = new SucceedResult();
            r.data = data;
            return r;
        }

        try {
            data = (ArrayList<T>) JSON.parseArray(processResult.rootObject.data, type);
        } catch (JSONException e) {
            JsonParseFailedResult r = new JsonParseFailedResult();

            r.content = processResult.content;
            r.exception = e;
            return r;
        }

        if (data == null) {
            JsonParseFailedResult r = new JsonParseFailedResult();
            r.content = processResult.content;
            r.exception = new JSONException("cant parse string to RootObject: " +
                    processResult.rootObject.data);
            return r;
        }

        SucceedResult r= new SucceedResult();
        r.data = data;
        return r;
    }

    @Override
    public void postProcess(@NonNull IResult result) {
        if (result.type() != ProcessResult.SUCCEED) {
            super.postProcess(result);
        } else {
            @SuppressWarnings("unchecked")
            SucceedResult r = (SucceedResult) result;

            @SuppressWarnings("unchecked")
            ArrayList<T> data = (ArrayList<T>) r.data;

            success(data);
            end(); // 一定不能忘了回调finish方法
        }

    }

    public abstract void success(@NonNull ArrayList<T> data);
}
