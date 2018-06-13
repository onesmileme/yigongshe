package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.weikan.app.bean.Voice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kailun on 2016/12/26.
 */
public class OriginalDetailItem implements Serializable {

    @JSONField(name = "tid")
    public String tid;
    @JSONField(name = "author")
    public String author;
    @JSONField(name = "authorid")
    public String authorid;
    @JSONField(name = "headimgurl")
    public String headimgurl;
    @JSONField(name = "oa_nick_name")
    public String oa_nick_name;
    @JSONField(name = "title")
    public String title;
    @JSONField(name = "abstract")
    public String contentAbstract;
    @JSONField(name = "url")
    public String url;
    @JSONField(name = "pic")
    public Pic pic;
    @JSONField(name = "share_pic")
    public Pic sharePic;
    @JSONField(name = "ctime")
    public long ctime;
    @JSONField(name = "forward")
    public Forward forward;
    @JSONField(name = "comment")
    public Comment comment;
    @JSONField(name = "praise")
    public Praise praise;

    @JSONField(name = "content")
    public String content;
    @JSONField(name = "share_content")
    public String share_content;
    @JSONField(name = "category")
    public int category;

    @JSONField(name = "collection")
    public CommonFlag collection = new CommonFlag();
    @JSONField(name = "dislike")
    public CommonFlag dislike = new CommonFlag();

    @JSONField(name = "template_type")
    public String template_type;  // 详情模板类型，web, multi

    @JSONField(name = "item_list")
    public ArrayList<MultiItem> item_list;  // 详情页的正文na分块，每块可以是文字或者图片

    @JSONField(name = "tweet_rel_list")
    public List<TweetRelObject> tweetRelList = new ArrayList<>();

    @JSONField(name = "share")
    public Share share;

    public static class Share implements Serializable {
        @JSONField(name = "desc")
        public String desc;
        @JSONField(name = "icon")
        public String icon;
        @JSONField(name = "title")
        public String title;
        @JSONField(name = "url")
        public String url;

    }


    public static class Pic implements Serializable{
        @JSONField(name = "t")
        public PicObject t;

        @JSONField(name = "s")
        public PicObject s;
    }


    public static class Forward implements Serializable {
        @JSONField(name = "num")
        public int num;
        @JSONField(name = "url")
        public String url;
    }

    public static class Praise implements Serializable {
        @JSONField(name = "num")
        public int num;
        @JSONField(name = "flag")
        public boolean flag;
        @JSONField(name = "user")
        public ArrayList<String> user;
    }

    public static class CommonFlag implements Serializable {
        @JSONField(name = "num")
        public int num;
        @JSONField(name = "flag")
        public boolean flag;
    }


    public static class Comment implements Serializable {
        @JSONField(name = "num")
        public int num;
    }
    /**
     * 详情如果是na展示，采用分块，每块内容为图片或者文字，不会同时存在两者
     */
    public static class MultiItem implements Serializable{
        @JSONField(name = "img")
        public PicObject img;   // item是纯图片时有
        @JSONField(name = "voice")
        public ArrayList<Voice> voice;  // item是纯图片时有

        @JSONField(name = "desc")
        public String desc;  // 这个item是纯文本时有，没有图片
    }

    public enum TemplateType{
        web,  // web模板类型
        multi  // na模板类型
    }


    @Override
    public String toString() {
        return "OriginalObject{" +
                "tid=" + tid +

                ", author='" + author + '\'' +
                ", oaNickName='" + oa_nick_name + '\'' +
                ", contentTitle='" + title + '\'' +
                ", contentAbstract='" + contentAbstract + '\'' +
                ", contentPicUrl='" + pic + '\'' +
                ", createTime=" + ctime +
                ", forwardNum=" + forward +
                ", commentNum=" + comment +
                ", praiseNum=" + praise +
                '}';
    }
}
