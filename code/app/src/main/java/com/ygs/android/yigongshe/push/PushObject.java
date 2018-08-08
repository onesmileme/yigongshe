package com.ygs.android.yigongshe.push;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class PushObject implements Serializable {

    @JSONField(name="t")
    public int t;
    @JSONField(name="p")
    public PushParam p;

    public static class PushParam{
        /**
         * t=1时，需要跳转的schema
         */
        @JSONField(name="j")
        public String j;

}
}
