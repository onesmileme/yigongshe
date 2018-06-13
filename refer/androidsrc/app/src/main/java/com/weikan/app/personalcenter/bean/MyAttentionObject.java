package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by zhaorenhui on 2015/11/7.
 */
public class MyAttentionObject {

    @JSONField(name = "uid")
    public String uid = "";

    @JSONField(name = "utime")
    public int utime;

    @JSONField(name = "birthday")
    public int birthday;

    @JSONField(name = "nickname")
    public String nickname = "";

    @JSONField(name = "role")
    public int role;

    /**
     * -1 表示不用展示关注相关的按钮;
     * 0 显示+关注 按钮;
     * 1 显示 已关注 按钮;
     * 2 显示 互相关注 按钮
     */
    @JSONField(name = "follow_type")
    public int followType;

    @JSONField(name = "headimgurl")
    public String headimgurl = "";

    @JSONField(name = "city")
    public String city = "";
}
