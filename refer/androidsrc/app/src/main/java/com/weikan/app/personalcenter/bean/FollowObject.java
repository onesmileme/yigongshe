package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by liujian on 16/3/12.
 */
public class FollowObject {
    @JSONField(name = "content")
    public List<FollowContent> content;

    public static class FollowContent{
        @JSONField(name = "follow_uid")
        public String follow_uid;
        @JSONField(name = "type")
        public int type;
    }
}
