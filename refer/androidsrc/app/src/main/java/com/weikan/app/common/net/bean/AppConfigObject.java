package com.weikan.app.common.net.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by liujian on 17/1/10.
 */
public class AppConfigObject implements Serializable{

    /**
     * 是否有群组功能
     */
    @JSONField(name = "need_group")
    public int need_group;

    /**
     * 是否有直播礼物
     */
    @JSONField(name = "need_live_gift")
    public int need_live_gift;

    /**
     * 是否有直播
     */
    @JSONField(name = "need_live")
    public int need_live;

}
