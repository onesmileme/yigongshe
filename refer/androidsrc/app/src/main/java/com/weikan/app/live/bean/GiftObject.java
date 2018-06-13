package com.weikan.app.live.bean;

import com.alibaba.fastjson.annotation.JSONField;


/**
 * Created by ylp on 2016/10/14.
 */

public class GiftObject {
    @JSONField(name = "gift")
    public Gift gift;
    @JSONField(name = "timestamp")
    public long timestamp;
    @JSONField(name = "user")
    public User user;

    public static class Gift{
        @JSONField(name = "id")
        public String id;
        @JSONField(name = "name")
        public String name;
        @JSONField(name = "emoney")
        public String emoney;
        @JSONField(name = "url")
        public String url;
        public int status = 0;
    }
    public static class User{
        @JSONField(name = "id")
        public String id;
        @JSONField(name = "openid")
        public String openid;
        @JSONField(name = "unionid")
        public String unionid;
        @JSONField(name = "access_token")
        public String access_token;
        @JSONField(name = "ori_nickname")
        public String ori_nickname;
        @JSONField(name = "nickname")
        public String nickname;
        @JSONField(name = "headimgurl")
        public String headimgurl;
        @JSONField(name = "sex")
        public String sex;
        @JSONField(name = "country")
        public String country;
        @JSONField(name = "province")
        public String province;
        @JSONField(name = "city")
        public String city;
        @JSONField(name = "interest_tag")
        public String interest_tag;
        @JSONField(name = "platform_name")
        public String platform_name;
        @JSONField(name = "counter")
        public String counter;
        @JSONField(name = "ctime")
        public String ctime;
        @JSONField(name = "utime")
        public String utime;
        @JSONField(name = "is_robot")
        public String is_robot;
        @JSONField(name = "role")
        public String role;
        @JSONField(name = "birthday")
        public String birthday;
        @JSONField(name = "autograph")
        public String autograph;
        @JSONField(name = "money")
        public String money;
    }
}
