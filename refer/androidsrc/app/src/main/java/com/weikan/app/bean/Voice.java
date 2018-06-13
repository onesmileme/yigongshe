package com.weikan.app.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by liujian on 16/3/23.
 */
public class Voice implements Serializable {

    @JSONField(name = "url")
    public String url = "";

    @JSONField(name = "duration")
    public int duration;
}
