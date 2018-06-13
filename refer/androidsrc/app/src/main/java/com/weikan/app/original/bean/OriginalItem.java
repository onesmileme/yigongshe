package com.weikan.app.original.bean;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.annotation.JSONField;
import com.weikan.app.bean.Voice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

/**
 * @author kailun on 2016/12/26.
 */
public class OriginalItem implements Serializable {

    @JSONField(name = "tid")
    public String tid = "";
    @JSONField(name = "author")
    public String author = "";
    @JSONField(name = "authorid")
    public String authorid = "";
    @JSONField(name = "headimgurl")
    public String headimgurl = "";
    @JSONField(name = "oa_nick_name")
    public String oa_nick_name = "";
    @JSONField(name = "title")
    public String title = "";
    @JSONField(name = "abstract")
    public String contentAbstract = "";
    @JSONField(name = "pic")
    public Pic pic;

    @JSONField(name = "imgs")
    public ArrayList<Pic> imgs = new ArrayList<>();

    @JSONField(name = "ctime")
    public long ctime;
    @JSONField(name = "pubtime")
    public long pubtime;

    @JSONField(name = "actime")
    public long actime; //收藏时间，用于收藏列表排序
    @JSONField(name = "forward")
    public Forward forward;
    @JSONField(name = "comment")
    public Comment comment;
    @JSONField(name = "praise")
    public Praise praise;
    @JSONField(name = "top")
    public Top top;
    @JSONField(name = "schema")
    public String schema = "";

    @JSONField(name = "share_content")
    public String share_content = "";
    @JSONField(name = "share_pic")
    public SharePic share_pic;

    @JSONField(name = "category")
    public Integer category = 0;

    @JSONField(name = "cycle")
    public String cycle = "";  // 十大的开始文字

    @JSONField(name = "tag")
    public ArrayList<Tag> tag;

    @JSONField(name = "voice")
    public ArrayList<Voice> voice;

    @JSONField(name = "channel_act")
    public Act channel_act;  // 用户操作看吧帖子的数据，目前只有转发s

    @JSONField(name = "read_num")
    public int read_num;  // 文章阅读数

    @JSONField(name = "template_type")
    public String templateType = "";

    @JSONField(name = "last_time")
    public long lastTime;  // 单位是毫秒

    @JSONField(name = "banner_content")
    public ArrayList<BannerContent> bannerContent = new ArrayList<>();

    public static class BannerContent implements Serializable {
        @JSONField(name = "tid")
        public String tid = "";

        @JSONField(name = "title")
        public String title = "";

        @JSONField(name = "schema")
        public String schema = "";

        @JSONField(name = "imgs")
        public List<Pic> imgs = new ArrayList<>();
    }

    public static class Pic implements Serializable {
        @JSONField(name = "s")
        public PicObject s;
        @JSONField(name = "t")
        public PicObject t;

        @NonNull
        public String getImageUrl() {
            if (s != null && !isEmpty(s.url)) {
                return s.url;
            }
            if (t != null && !isEmpty(t.url)) {
                return t.url;
            }
            return "";
        }
    }

    public static class SharePic implements Serializable {
        @JSONField(name = "t")
        public PicObject t;
    }

    public static class Forward implements Serializable {
        @JSONField(name = "num")
        public int num;
        @JSONField(name = "url")
        public String url = "";
    }

    public static class Praise implements Serializable {
        @JSONField(name = "num")
        public int num;
        @JSONField(name = "flag")
        public Boolean flag;
    }


    public static class Comment implements Serializable {
        @JSONField(name = "num")
        public int num;
    }

    public static class Top implements Serializable {
        @JSONField(name = "id")
        public int id;
        @JSONField(name = "rank")
        public int rank;
        @JSONField(name = "time")
        public int time;
    }

    public static class Tag implements Serializable {
        @JSONField(name = "id")
        public int id;
        @JSONField(name = "name")
        public String name = "";
    }

    /**
     * 用户对看吧帖子的操作
     */
    public static class Act implements Serializable {
        @JSONField(name = "forward")
        public ForwardBar forward;
    }

    /**
     * 看吧转发的数据
     */
    public static class ForwardBar implements Serializable {
        @JSONField(name = "uid")
        public String uid = "";
        @JSONField(name = "nickname")
        public String nickname = "";
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
