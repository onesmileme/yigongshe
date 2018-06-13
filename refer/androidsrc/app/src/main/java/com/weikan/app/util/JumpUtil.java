package com.weikan.app.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.weikan.app.WebshellActivity;
import com.weikan.app.util.jump.ActionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * abstract of class/interface and so on
 *
 * @author liujian on 15/8/3
 */
public class JumpUtil {
    public static final String schemaHead = "appfac://";
    /**
     *
     * @param schema  schema
     * @param context context
     * @return true or false
     */
    public static boolean executeSchema(String schema, Context context, @Nullable Bundle bundle) {
        if (!TextUtils.isEmpty(schema)) {

            if (schema.startsWith("http://") || schema.startsWith("https://")) {
                Bundle b = new Bundle();
                b.putString(BundleParamKey.URL, schema);
                Intent intent = new Intent();
                intent.setClass(context, WebshellActivity.class);
                intent.putExtras(b);
                context.startActivity(intent);
                return true;
            }

            if (schema.startsWith(schemaHead)) {
                String path = parsePath(schema);
                String kvstring = schema.substring(schemaHead.length());
                Map<String, String> kvs = urlRequest(kvstring);
                if (bundle == null) {
                    bundle = new Bundle();
                }
                //ActionFactory为schema动作的生产工厂，新增schema需进行绑定
                return ActionFactory.createAction(path).execute(context, schema, path, kvs, bundle);
            }
        }
        return false;
    }

    public static boolean executeSchema(String schema, Context context) {
        return executeSchema(schema, context, null);
    }

    private static String parsePath(String schema) {
        if (schema.contains("?")) {
            return schema.substring(schemaHead.length(), schema.indexOf("?"));
        } else {
            return schema.substring(schemaHead.length());
        }
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String truncateUrlPage(String strURL) {
        String strAllParam = null;

        strURL = strURL.trim();

        String[] arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }

        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, String> urlRequest(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();

        String strUrlParam = truncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组 www.2cto.com
        String[] arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = strSplit.split("[=]");

            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            }
        }
        return mapRequest;
    }
}
