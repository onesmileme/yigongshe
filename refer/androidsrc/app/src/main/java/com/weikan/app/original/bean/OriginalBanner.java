package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * Created by yuzhiboyou on 15-12-22.
 */
public class OriginalBanner {
    @JSONField(name = "data")
    public Data data;

    public static class Data {
        @JSONField(name = "content")
        public ArrayList<Content> contentList;
    }

    public static class Content {

        @JSONField(name = "bid")
        public String bid;

        @JSONField(name = "title")
        public String title;

        @JSONField(name = "url")
        public String url;

        @JSONField(name = "go_url")
        public String redirect_url;
    }
}
