package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * Created by liujian on 16/11/13.
 */
public class MyTalkListObject {

    @JSONField(name = "content")
    public ArrayList<MyTalkListContent> content;

    public static class MyTalkListContent {
        @JSONField(name = "nickname")
        public String nickname;
        @JSONField(name = "content")
        public String content;
        @JSONField(name = "ctime")
        public String ctime;
        @JSONField(name = "headimgurl")
        public String headimgurl;
        @JSONField(name = "to_uid")
        public String to_uid;
        @JSONField(name = "msg_num")
        public int msg_num;
    }
}
