package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by zhaorenhui on 2015/11/7.
 */
public class OfficialAccount implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @JSONField(name = "oaid")
    public int oaId;

    @JSONField(name = "oa_nick_name")
    public String oaNickName; //

    @JSONField(name = "category")
    public String category; //

    @JSONField(name = "ofc_account")
    public String ofcAccount; //

    @JSONField(name = "order_num")
    public int orderNum; //

    @JSONField(name = "accumulated_income")
    public float accumulatedIncome; //

    @JSONField(name = "ranking")
    public int ranking; //

    @JSONField(name = "isflashsale")
    public boolean isFlashSale;

    @JSONField(name = "pic")
    public String pic;

}
