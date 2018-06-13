package com.weikan.app.bean;


import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/2/3
 * Time: 13:35
 */
public class PushObject  {
    /**
     *  1是首页小红点，2是讨论区小红点，3是推送通知， 4私信小红点， 5新的好友小红点
     */
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

//        /**
//         * t=6时，私信小红点，发信人的uid
//         */
//        public String uid;

        /**
         * 小红点数字，在不用显示数字的地方，这个值为-1
         */
        @JSONField(name="n")
        public int n;


//        /**
//         * t=6时候有效，2表示好友通知红点，3表示私信红点
//         */
//        public int l;
    }
}
