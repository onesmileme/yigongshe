package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by liujian on 16/4/9.
 */
public class PicObject implements Serializable {
    @JSONField(name = "h")
    public int h;
    @JSONField(name = "w")
    public int w;
    @JSONField(name = "url")
    public String url = "";
}