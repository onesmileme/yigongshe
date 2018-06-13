package com.weikan.app.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 用户个人信息结构.
 * User: liujian06
 * Date: 2015/3/8
 * Time: 13:46
 */
public class UserInfoObject implements Serializable {

    @JSONField(name = "content")
    public UserInfoContent content;

    public static class UserInfoContent implements Serializable {

        @JSONField(name = "nickname")
        public String nick_name = "";

        @JSONField(name = "headimgurl")
        public String headimgurl = "";

        @JSONField(name = "tweet_num")
        public int tweet_num;

        @JSONField(name = "praise_num")
        public int praise_num;

        @JSONField(name = "top_num")
        public int top_num;

        @Deprecated
        @JSONField(name = "followee_num")
        public int followee_num;

        @Deprecated
        @JSONField(name = "follower_num")
        public int follower_num;

        @JSONField(name = "follow_type")
        public int follow_type;

        @JSONField(name = "follow_num")
        public int follow_num;

        @JSONField(name = "fans_num")
        public int fans_num;

        @JSONField(name = "role")
        public int role;

        @JSONField(name = "sex")
        public int sex;

        @JSONField(name = "autograph")
        public String autograph = "";

        @JSONField(name = "birthday")
        public int birthday;

        @JSONField(name = "province")
        public String province = "";

        @JSONField(name = "city")
        public String city = "";
        @JSONField(name = "post")
        public String post = "";
        @JSONField(name = "company")
        public String company = "";

        /**
         * 身份认证状态
         * 0:未认证 1:待审核 2:审核拒绝 3:审核通过
         */
        @JSONField(name = "verify_status")
        public int verifyStatus;
    }
}
