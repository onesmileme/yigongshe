package com.weikan.app.live.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * @author kailun on 16/8/16.
 */
public class LiveListObject implements Serializable{

    //    {
    //        uid: "test",
    //        author: "mu",
    //        headimgurl: "http://img2.tigerinsky.com/tweet_kk/2016-08-10/6CE14A4DD63A4C83AB1D67990C9ABE7D.jpg@200h",
    //        live_id: 1,
    //        title: "hhhhh",
    //        cover: "http://img2.tigerinsky.com/tweet_kk/2016-08-10/6CE14A4DD63A4C83AB1D67990C9ABE7D.jpg",
    //        stime: 1471678108,
    //        etime: 1471678888,
    //        status: 1,
    //        watch_num: 88888
    //    }

    @JSONField(name = "uid")
    public String uid = "";
    @JSONField(name = "live_uid")
    public String live_uid = "";
    @JSONField(name = "url")
    public String url = "";

    @JSONField(name = "nickname")
    public String nickname = "";
    @JSONField(name = "author")
    public String author = "";
    @JSONField(name = "live_headimg")
    public String live_headimg = "";
    @JSONField(name = "headimgurl")
    public String headImgUrl = "";
    @JSONField(name = "live_id")
    public long liveId = 0;

    @JSONField(name = "title")
    public String title = "";

    @JSONField(name = "cover")
    public String cover = "";

    @JSONField(name = "stime")
    public long stime;

    @JSONField(name = "etime")
    public long etime;

    @JSONField(name = "status")
    public int status;

    @JSONField(name = "online_num")
    public int online_num;
    @JSONField(name = "watch_num")
    public int watchNum;
    @JSONField(name = "h5_url")
    public String h5Url;

    @JSONField(name = "share")
    public Share share;


//    public List<String> tags = new ArrayList<>();

    public static class LiveURL{

        @JSONField(name = "hls")
        public String hls;

        @JSONField(name = "rtmp")
        public String rtmp;
    }

    public static class Share implements Serializable{

        @JSONField(name = "url")
        public String url;
        @JSONField(name = "title")
        public String title;
        @JSONField(name = "desc")
        public String desc;
        @JSONField(name = "pic")
        public String pic;
        @JSONField(name = "icon")
        public String icon;
    }
}
