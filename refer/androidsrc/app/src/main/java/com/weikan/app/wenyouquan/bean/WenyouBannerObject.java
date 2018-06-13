package com.weikan.app.wenyouquan.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.weikan.app.original.bean.OriginalBanner;

import java.util.ArrayList;

/**
 * Created by liujian on 16/8/9.
 */
public class WenyouBannerObject {

    @JSONField(name = "data")
    public ArrayList<Content> data;

    public static class Content {

        @JSONField(name = "title")
        public String title;

        @JSONField(name = "jump")
        public String jump;

        @JSONField(name = "img")
        public String img;
    }
}
