package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/8
 */
public class ImageObject implements Serializable {

    @JSONField(name = "url")
    public String url = "";

    @JSONField(name = "w")
    public int w;

    @JSONField(name = "h")
    public int h;
}
