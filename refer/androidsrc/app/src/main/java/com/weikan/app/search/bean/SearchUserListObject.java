package com.weikan.app.search.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * 搜索得到的用户列表
 *
 * @author kailun on 16/3/30
 */
public class SearchUserListObject {

    @JSONField(name = "content")
    public ArrayList<User> content;

    public static class User {

        @JSONField(name = "uid")
        public String uid = "";

        @JSONField(name = "nickname")
        public String nickname = "";

        @JSONField(name = "headimgurl")
        public String headimgurl = "";

        @JSONField(name = "ctime")
        public long ctime;
    }
}
