package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * @author kailun on 16/8/9.
 */
public class MyAttentionResponseObject {
    @JSONField(name = "data")
    public ArrayList<MyAttentionObject> data = new ArrayList<>();
}
