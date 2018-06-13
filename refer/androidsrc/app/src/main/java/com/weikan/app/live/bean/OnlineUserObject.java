package com.weikan.app.live.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author kailun on 16/8/31.
 */
public class OnlineUserObject {
    @JSONField(name = "uid")
    public String uid = "";

    @JSONField(name = "nickname")
    public String nickName = "";

    @JSONField(name = "headimgurl")
    public String headImgUrl = "";
}
