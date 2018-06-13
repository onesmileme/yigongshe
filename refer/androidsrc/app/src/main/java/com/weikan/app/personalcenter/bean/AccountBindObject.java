package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 第三方已绑定结果
 * Created by liujian on 16/4/16.
 */
public class AccountBindObject {
    @JSONField(name="weixin")
    public Integer  weixin;
    @JSONField(name="qq")
    public Integer  qq;
}
