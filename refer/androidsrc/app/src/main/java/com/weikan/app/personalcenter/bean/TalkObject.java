package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.*;

/**
 * Created by liujian on 16/11/13.
 */
public class TalkObject{

    @JSONField(name = "content")
    public  ArrayList<TalkContent> content = null;
    @JSONField(name = "last_read_mid")
    public String last_read_mid = null;

    public static class TalkContent {
        @JSONField(name = "mid")
         public String  mid = null;
        @JSONField(name = "from_uid")
        public String from_uid = null;
        @JSONField(name = "nickname")
        public  String nickname = null;
        @JSONField(name = "content")
        public  String content= null;
        @JSONField(name = "ctime")
        public   Long ctime = 0l;
        @JSONField(name = "time")
        public   String time = null;
        @JSONField(name = "to_uid")
        public String to_uid  = null;
        @JSONField(name = "headimgurl")
        public    String headimgurl= null;

    }
}