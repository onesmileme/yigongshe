package com.weikan.app.search.logical;

import android.support.annotation.NonNull;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/3/30
 */
public class SearchKeywordProvider {

    private static String sKeyword = "";

    @NonNull
    public static String get() {
        return sKeyword;
    }

    public static void modify(@NonNull String s) {
        sKeyword = s;
    }
}
