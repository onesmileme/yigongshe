package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by zhaorenhui on 2015/12/5.
 */
public class AppreciationInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @JSONField(name = "tid")
    public String tId;

    // 赞赏订单id
    @JSONField(name = "alid")
    public long alId;


    // "原创文章标题"
    @JSONField(name = "title")
    public String title;


    // 原创文章头图
    @JSONField(name = "img")
    public String img;


    // 赞赏金额
    @JSONField(name = "totle_price")
    public double totalPrice;


    // 交易时间
    @JSONField(name = "ctime")
    public long cTime;


    // 是否已同步
    @JSONField(name = "is_sync")
    public int isSync;

}
