package com.weikan.app.search.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * 搜索得到的用户列表
 *
 * @author kailun on 16/3/30
 */
public class SearchContentListObject {

    @JSONField(name = "content")
    public ArrayList<Content> content;

    public static class Content {

        @JSONField(name = "tid")
        public String tid = "";

        @JSONField(name = "title")
        public String title = "";

        @JSONField(name = "pic")
        public Pic pic = new Pic();

        @JSONField(name = "ctime")
        public long ctime;
    }

    public static class Pic {

        @JSONField(name = "s")
        public S s = new S();
    }

    public static class S {

        @JSONField(name = "url")
        public String url = "";

        @JSONField(name = "h")
        public int h;

        @JSONField(name = "w")
        public int w;
    }
}
