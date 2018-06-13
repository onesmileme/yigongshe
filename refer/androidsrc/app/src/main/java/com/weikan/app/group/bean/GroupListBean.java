package com.weikan.app.group.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by ylp on 2017/1/8.
 */

public class GroupListBean {
        @JSONField(name = "groups")
        public List<GroupDetailBean> groups;
}
