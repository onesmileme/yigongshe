package com.weikan.app.request.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 热门话题列表数据
 * Created by liujian on 16/8/6.
 */
public class TopicItem {

    @JSONField(name = "id")
    public String id;

    @JSONField(name = "name")
    public String name;
}
