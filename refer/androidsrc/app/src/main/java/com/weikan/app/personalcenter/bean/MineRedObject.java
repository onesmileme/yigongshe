package com.weikan.app.personalcenter.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by liujian on 16/3/11.
 */
public class MineRedObject {
    @JSONField(name = "data")
    public RedData data;

    public static class RedData {
        @JSONField(name = "type_1")
        public int type_1;
        @JSONField(name = "type_2")
        public int type_2;
        @JSONField(name = "type_3")
        public int type_3;
    }
}
