package com.weikan.app.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 1 - 推送通知
 * 2 - 原创tab红点；
 * 3 - 限时tab红点；
 * 4 - 榜单红点；
 * 5 - 新增寻购红点；
 * 6 - 寻购今日待执行红点；
 * 7 - 新增抢购红点；
 * 8 - 抢购今日待执行红点；
 * 9 - 新增合同红点；
 * 10 - 赞赏收入红点；
 * 11 - 广告收入红点；
 * 12 - 赞赏素材红点；
 * 13 - 系统消息红点；
 */
public class MsgNumObject  implements Serializable {
    private static final long serialVersionUID = 1L;

    @JSONField(name = "data")
    public MsgNum data;

    public static class MsgNum implements Serializable {
        private static final long serialVersionUID = 1L;

        @JSONField(name = "type_2")
        public int type_2;

        @JSONField(name = "type_3")
        public int type_3;

        @JSONField(name = "type_4")
        public int type_4;

        @JSONField(name = "type_5")
        public int type_5;

        @JSONField(name = "type_6")
        public int type_6;

        @JSONField(name = "type_7")
        public int type_7;

        @JSONField(name = "type_8")
        public int type_8;

        @JSONField(name = "type_9")
        public int type_9;

        @JSONField(name = "type_10")
        public int type_10;

        @JSONField(name = "type_11")
        public int type_11;

        @JSONField(name = "type_12")
        public int type_12;

        @JSONField(name = "type_13")
        public int type_13;
    }
}
