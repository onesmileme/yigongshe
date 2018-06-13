package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

/**
 * 看吧用户列表数据
 * Created by liujian on 16/4/9.
 */
public class BarUserObject implements Serializable {


    @JSONField(name = "data")
    public List<BarUser> data;

    public static class BarUser implements Serializable{

        @JSONField(name = "channel_id")
        public String channel_id;

        @JSONField(name = "openid")
        public String openid;

        @JSONField(name = "role_id")
        public int role_id;

        @JSONField(name = "headimgurl")
        public String headimgurl;

        @JSONField(name = "nickname")
        public String nick_name;

        @JSONField(name = "follow_type")
        public int follow_type;

        @JSONField(name = "ctime")
        public long ctime;
    }
}
