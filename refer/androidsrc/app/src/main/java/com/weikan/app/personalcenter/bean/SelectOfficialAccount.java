package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by zhaorenhui on 2015/12/6.
 */
public class SelectOfficialAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    @JSONField(name = "avatar")
    public String avatar;

    @JSONField(name = "ofc_account")
    public String ofcAccount;

    @JSONField(name = "ofc_nick_name")
    public String ofcNickname;

    @JSONField(name = "is_sync")
    public int isSync;
}
