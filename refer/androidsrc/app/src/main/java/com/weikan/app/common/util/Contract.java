package com.weikan.app.common.util;

import android.text.TextUtils;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 15/12/10.
 */
public class Contract {

    public static void nonNull(Object a) {
        if (a == null) {
            throw new NullPointerException("param can't be null");
        }
    }

    public static void nonNull(Object a, Object b) {
        nonNull(a);
        nonNull(b);
    }

    public static void nonNull(Object a, Object b, Object c) {
        nonNull(a);
        nonNull(b);
        nonNull(c);
    }

    public static void nonNull(Object a, Object b, Object c, Object d) {
        nonNull(a);
        nonNull(b);
        nonNull(c);
        nonNull(d);
    }

    public static void argumentNotNull(Object arg) {
        if (arg == null) {
            throw new RuntimeException("argument can't be null");
        }
    }

    public static void stringArgumentNotEmpty(String arg) {
        if (TextUtils.isEmpty(arg)) {
            throw new RuntimeException("string argument param can't be null or empty");
        }
    }
}
