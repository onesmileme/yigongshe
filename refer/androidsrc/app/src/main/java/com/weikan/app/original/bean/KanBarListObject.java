package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 看吧主列表页结构
 * Created by liujian on 16/4/9.
 */
public class KanBarListObject {
    @JSONField(name = "mine")
    public List<BarObject> mine;
    @JSONField(name = "list")
    public List<BarObject> list;

    public static class BarObject implements Serializable{
        @JSONField(name = "channel_id")
        public String channel_id;
        @JSONField(name = "background_pic")
        public String background_pic;
        @JSONField(name = "head_pic")
        public String head_pic;
        @JSONField(name = "abstract")
        public String content;
        @JSONField(name = "channel_name")
        public String channel_name;
        @JSONField(name = "is_public")
        public int is_public;
        @JSONField(name = "status")
        public String status;
        @JSONField(name = "ctime")
        public long ctime;

        @JSONField(name = "tweet")
        public List<BarTweetObject> tweet = new ArrayList<>();

    }

    public static class BarTweetObject implements Serializable{
        @JSONField(name = "article_id")
        public String article_id;
        @JSONField(name = "title")
        public String title;
        @JSONField(name = "pic")
        public BarTweetPic pic;
    }

    public static class BarTweetPic implements Serializable{
        @JSONField(name = "s")
        public PicObject s;
    }
}
