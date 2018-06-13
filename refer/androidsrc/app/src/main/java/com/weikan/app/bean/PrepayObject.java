package com.weikan.app.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wutong on 1/1/16.
 */
public class PrepayObject {
//    @JSONField(name = "errno")
//    public int errno;
//    @JSONField(name = "errmsg")
//    public String errmsg;
//    @JSONField(name = "data")
//    public Data data;
//
//    public static class Data {
        @JSONField(name = "content")
        public Content content;

        public static class Content {
            @JSONField(name = "appid")
            public String appid;

            @JSONField(name = "out_trade_no")
            public String out_trade_no;

            @JSONField(name = "code_url")
            public String code_url;

            @JSONField(name = "mch_id")
            public String mch_id;

            @JSONField(name = "nonce_str")
            public String nonce_str;

            @JSONField(name = "prepay_id")
            public String prepay_id;

            @JSONField(name = "result_code")
            public String result_code;

            @JSONField(name = "return_msg")
            public String return_msg;

            @JSONField(name = "sign")
            public String sign;

            @JSONField(name = "trade_type")
            public String trade_type;

            @JSONField(name = "timeStamp")
            public String timeStamp;

            @JSONField(name = "key")
            public String key;
        }
//    }
}
