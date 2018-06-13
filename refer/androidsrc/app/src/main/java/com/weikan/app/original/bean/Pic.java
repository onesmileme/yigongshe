package com.weikan.app.original.bean;

import android.support.annotation.NonNull;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

import static android.text.TextUtils.isEmpty;

/**
 * Created by liujian on 17/1/15.
 */

public class Pic implements Serializable {
    @JSONField(name = "s")
    public PicObject s;
    @JSONField(name = "t")
    public PicObject t;
    @JSONField(name = "n")
    public PicObject n;


    @NonNull
    public String getImageUrlLittle() {
        if (s != null && !isEmpty(s.url)) {
            return s.url;
        }
        if (t != null && !isEmpty(t.url)) {
            return t.url;
        }
        if (n != null && !isEmpty(n.url)) {
            return n.url;
        }
        return "";
    }

    @NonNull
    public String getImageUrlBig() {
        if (n != null && !isEmpty(n.url)) {
            return n.url;
        }
        if (s != null && !isEmpty(s.url)) {
            return s.url;
        }
        if (t != null && !isEmpty(t.url)) {
            return t.url;
        }
        return "";
    }
}
