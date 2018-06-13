package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.weikan.app.original.bean.PicObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaorenhui on 2015/12/24.
 */
public class SystemMessageData implements Serializable {


    @JSONField(name = "content")
    public List<SystemMessage> content = new ArrayList<SystemMessage>();

    public static class SystemMessage implements Serializable {
        private static final long serialVersionUID = 1L;

        @JSONField(name = "sys_msg_id")
        public long sys_msg_id;

        @JSONField(name = "from_user")
        public SysUser from_user;

        @JSONField(name = "action_type")
        public int action_type;   //动作类型 1：关注，2：发表评论，3：回复评论，4：点赞

        @JSONField(name = "action_content")
        public String action_content = "";

        @JSONField(name = "digest")
        public String digest = "";

        @JSONField(name = "tid")
        public String tid;

        @JSONField(name = "ctime")
        public long ctime;

        @JSONField(name = "pic")
        public Pic pic;

    }

    public static class SysUser implements Serializable {
        private static final long serialVersionUID = 1L;

        @JSONField(name = "nickname")
        public String nickname;

        @JSONField(name = "headimgurl")
        public String headimgurl;

        @JSONField(name = "uid")
        public String uid;
    }


    public static class Pic {
        @JSONField(name = "t")
        public PicObject t;
    }
}
