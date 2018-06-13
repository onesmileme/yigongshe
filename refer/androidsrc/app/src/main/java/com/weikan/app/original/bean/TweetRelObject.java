package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 详情中的创意列表项
 *
 * @author kailun on 16/5/2
 */
public class TweetRelObject implements Serializable {

    @JSONField(name = "tid")
    public String tid = "";

    @JSONField(name = "pic")
    public ImageNtsObject pic;
}
