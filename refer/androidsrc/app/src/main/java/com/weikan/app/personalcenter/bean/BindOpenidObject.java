package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 绑定openid返回数据
 * Created by liujian on 16/3/13.
 */
public class BindOpenidObject {
    @JSONField(name="content")
    public BindContent  content;

    public static class BindContent{
        @JSONField(name="title")
        public String title;

        @JSONField(name="headimgurl")
        public String headimgurl;

        @JSONField(name="content")
        public String content;

        @JSONField(name="url")
        public String url;
    }
}
