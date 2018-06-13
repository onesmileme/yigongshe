package com.weikan.app.account.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class LoginResult  {
    @JSONField(name = "data")
    public UserInfoContent data;

    public static class UserInfoContent implements Serializable {
        @JSONField(name = "uid")
        public String uid;

        @JSONField(name = "token")
        public String token;

        @JSONField(name = "nickname")
        public String nick_name;

        @JSONField(name = "headimgurl")
        public String headimgurl;

        @JSONField(name = "role")
        public int role;

        @JSONField(name = "need_bind")
        public int need_bind;


    }
}
