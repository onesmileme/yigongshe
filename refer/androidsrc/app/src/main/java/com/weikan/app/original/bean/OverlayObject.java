package com.weikan.app.original.bean;

import android.support.annotation.Nullable;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/9
 */
public class OverlayObject implements Serializable {

    @JSONField(name = "type")
    public String type = "";

    @Nullable
    @JSONField(name = "picture")
    public Picture picture;

    @Nullable
    @JSONField(name = "text")
    public Text text;

    @JSONField(name = "x")
    public double x;

    @JSONField(name = "y")
    public double y;

    @JSONField(name = "resize")
    public double resize;

    @JSONField(name = "rotation")
    public double rotation;


    public static class Picture implements Serializable {

        @JSONField(name = "t")
        public ImageObject n;
    }

    public static class Text implements Serializable {

        @JSONField(name = "content")
        public String content = "";

        @JSONField(name = "size")
        public int size = 0;

        @JSONField(name = "c")
        public String color = "#ffffff";
    }
}

