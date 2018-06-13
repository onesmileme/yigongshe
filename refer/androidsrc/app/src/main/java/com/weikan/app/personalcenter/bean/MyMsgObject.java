package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * Created by liujian on 16/7/30.
 */
public class MyMsgObject {
    @JSONField(name = "content")
    public ArrayList<ContentObject> content;

    public static class ContentObject{

        @JSONField(name = "action_type")
        public int actionType; // 1 点赞，2 评论， 3 回复评论

        @JSONField(name = "from_user")
        public FromUserObject fromUser;

        @JSONField(name = "comment_content")
        public String commentContent = "";

        @JSONField(name = "reply_user")
        public ReplyUserObject replyUser;

        @JSONField(name = "msg_desc")
        public String msgDesc = "";

        @JSONField(name = "pic")
        public PicObject pic;

        @JSONField(name = "tid")
        public String tid = "";

        @JSONField(name = "ctime")
        public long ctime;

        @JSONField(name = "sys_msg_id")
        public int sysMsgId;

    }


    public static class FromUserObject{

        @JSONField(name = "uid")
        public String uid = "";

        @JSONField(name = "nickname")
        public String nickname = "";

        @JSONField(name = "headimgurl")
        public String headimgurl = "";

    }


    public static class ReplyUserObject{

        @JSONField(name = "uid")
        public String uid = "";

        @JSONField(name = "nickname")
        public String nickname = "";

    }


    public static class PicObject{

        @JSONField(name = "t")
        public TObject t;

    }


    public static class TObject{

        @JSONField(name = "w")
        public int w;

        @JSONField(name = "h")
        public int h;

        @JSONField(name = "url")
        public String url = "";

    }
}
