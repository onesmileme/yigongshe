package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * Created by liujian on 16/5/22.
 */
public class TagObject {
    @JSONField(name = "content")
    public ArrayList<TagContent> content;

    public static class TagContent{
        @JSONField(name = "title")
        public String title;
        @JSONField(name = "items")
        public ArrayList<String> items;
    }
}
