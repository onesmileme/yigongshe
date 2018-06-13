package com.weikan.app.news.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.weikan.app.news.bean.CategoryObject;
import com.weikan.app.util.PrefDefine;
import com.weikan.app.util.SharePrefsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kailun on 16/11/22.
 */
public class CategoryManager {
    private static final String CATEGORIES = "categories";

    private static CategoryManager ourInstance = new CategoryManager();

    public static CategoryManager getInstance() {
        return ourInstance;
    }

    private CategoryManager() {
    }

    public void saveCategoryListData(@NonNull final Context context,
                                     @NonNull final List<CategoryObject> categories) {
        String jsonStr = JSON.toJSONString(categories, false);

        SharedPreferences sp = context.getSharedPreferences(PrefDefine.PREF_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CATEGORIES, jsonStr);
        editor.apply();
    }

    public List<Pair<String, Integer>> readCategoryListData(@NonNull final Context context) {
        SharedPreferences sp = context.getSharedPreferences(PrefDefine.PREF_FILE, Activity.MODE_PRIVATE);
        String categoriesJson = sp.getString(CATEGORIES, "[]");

        List<Pair<String, Integer>> data = new ArrayList<>();
        JSONArray array = JSON.parseArray(categoriesJson);
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                String obj = array.getJSONObject(i).toJSONString();
                CategoryObject t = JSON.parseObject(obj, CategoryObject.class);
                if (t != null) {
                    Pair<String, Integer> pair = new Pair<>(t.cname, t.catid);
                    data.add(pair);
                }
            }
        }
        return data;
    }
}
