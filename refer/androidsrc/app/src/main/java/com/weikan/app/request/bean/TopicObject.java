package com.weikan.app.request.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * Created by liujian on 16/8/6.
 */
public class TopicObject {

    @JSONField(name = "data")
    public ArrayList<TopicItem> data;
}
