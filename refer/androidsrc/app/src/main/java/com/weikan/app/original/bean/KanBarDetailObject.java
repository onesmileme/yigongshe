package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 看吧详情接口数据
 * Created by liujian on 16/4/9.
 */
public class KanBarDetailObject {

    @JSONField(name = "info")
    public BarInfo info;
    @JSONField(name = "tweet")
    public List<OriginalItem> tweet;

    public static class BarInfo extends KanBarListObject.BarObject{
        @JSONField(name = "is_master")
        public int is_master;
        @JSONField(name = "is_member")
        public int is_member;
        @JSONField(name = "share_link")
        public String share_link;

    }
}
