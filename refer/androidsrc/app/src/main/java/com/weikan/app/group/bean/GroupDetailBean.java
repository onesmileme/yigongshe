package com.weikan.app.group.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.weikan.app.original.bean.Pic;

import java.io.Serializable;

/**
 * Created by ylp on 2017/1/8.
 */

public class GroupDetailBean implements Serializable {
    @JSONField(name = "group_id")
    public String groupId = "";
    @JSONField(name = "name")
    public String groupName = "";
    @JSONField(name = "avatar")
    public Pic avatar = new Pic();
    @JSONField(name = "intro")
    public String intro = "";
    @JSONField(name = "area")
    public String area = "";
    @JSONField(name = "background_pic")
    public Pic backoundPic;
    @JSONField(name = "is_followed")
    public int isFollowed;
    @JSONField(name = "follow_count")
    public int followCount;
    @JSONField(name = "follow_time")
    public long follow_time;
    @JSONField(name = "ctime")
    public long ctime;
    public int isSelected;
}
