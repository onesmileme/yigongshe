package com.weikan.app.wenyouquan.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.weikan.app.original.bean.Pic;
import com.weikan.app.original.bean.PicObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 文友圈的数据
 * Created by liujian on 16/6/26.
 */
public class WenyouListData implements Serializable {
    @JSONField(name = "content")
    public ArrayList<WenyouListItem> content = new ArrayList<>();
    @JSONField(name = "hot_group")
    public ArrayList<HotGroup> hot_group = new ArrayList<>();

    public static class WenyouListItem implements Serializable {
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
        @JSONField(name = "ctime")
        public long ctime;
        @JSONField(name = "role")
        public int role;

        @JSONField(name = "company")
        public String company;
        @JSONField(name = "schema")
        public String schema = "";
        @JSONField(name = "post")
        public String post;
        @JSONField(name = "source")
        public String source = "";
        @JSONField(name = "pic")
        public Pic pic = new Pic();

        @JSONField(name = "item_list")
        public ArrayList<Pic> item_list;
        @JSONField(name = "comment")
        public Comment comment;
        @JSONField(name = "praise")
        public Praise praise;
        @JSONField(name = "share")
        public Share share;

        @JSONField(name = "topic_info")
        public ArrayList<TopicInfo> topicInfo;
        @JSONField(name = "group_info")
        public ArrayList<GroupInfo> groupInfo;

        public boolean commentState = false;    //评论的折叠还是展开状态,默认为false处于折叠状态

        public boolean zanState = false;       //赞的折叠还是展开状态，默认为false处于折叠状态

        @JSONField(name = "label_info")
        public ArrayList<LabelInfoObject> label_info;
    }

    public static class LabelInfoObject implements Serializable {
        @JSONField(name = "label_background_color")
        public String bgColor;
        @JSONField(name = "label_font_color")
        public String textColor;
        @JSONField(name = "name")
        public String name;
        @JSONField(name = "schema")
        public String schema;

    }

    public static class Praise implements Serializable {
        @JSONField(name = "num")
        public int num;
        @JSONField(name = "flag")
        public boolean flag;
        @JSONField(name = "user_list")
        public ArrayList<PraiseItem> user_list = new ArrayList<>();
    }

    public static class PraiseItem implements Serializable {
        @JSONField(name = "uid")
        public String uid;
        @JSONField(name = "nickname")
        public String nickname;
        @JSONField(name = "headimgurl")
        public String headimgurl;
    }


    public static class Comment implements Serializable {
        @JSONField(name = "num")
        public int num;
        @JSONField(name = "comment_list")
        public ArrayList<CommentItem> comment_list = new ArrayList<>();


        @JSONField(name = "top_num")
        public int top_num;
        @JSONField(name = "top")
        public ArrayList<CommentItem> top = new ArrayList<>();
    }

    public static class CommentItem implements Serializable {
        @JSONField(name = "cid")
        public String cid;
        @JSONField(name = "uid")
        public String uid;
        @JSONField(name = "tid")
        public String tid;
        @JSONField(name = "content")
        public String content;
        @JSONField(name = "ctime")
        public long ctime;
        @JSONField(name = "reply_uid")
        public String reply_uid;
        @JSONField(name = "reply_cid")
        public String reply_cid;
        @JSONField(name = "reply_sname")
        public String reply_sname;
        @JSONField(name = "sname")
        public String sname;
        @JSONField(name = "avatar")
        public String avatar;

        @JSONField(name = "role")
        public int role;
        @JSONField(name = "reply_role")
        public int reply_role;
        @JSONField(name = "is_zan")
        public int is_zan;
        @JSONField(name = "zan_num")
        public int zan_num;
    }

    public static class TopicInfo implements Serializable{

        @JSONField(name = "id")
        public String id;
        @JSONField(name = "name")
        public String name;
    }

    public static class GroupInfo implements Serializable{

        @JSONField(name = "group_id")
        public String group_id;
        @JSONField(name = "name")
        public String name;
        @JSONField(name = "label_color")
        public String label_color;
    }

    public static class Share implements Serializable {

        @JSONField(name = "url")
        public String url;
        @JSONField(name = "title")
        public String title;
        @JSONField(name = "desc")
        public String desc;
        @JSONField(name = "pic")
        public PicObject pic;
        @JSONField(name = "icon")
        public String icon;
    }

    public static class HotGroup  implements Serializable{

        @JSONField(name = "group_id")
        public String group_id;
        @JSONField(name = "name")
        public String name;
        @JSONField(name = "follow_count")
        public int follow_count;
        @JSONField(name = "hot_background_pic")
        public Pic hot_background_pic;
    }

}
