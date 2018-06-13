package com.weikan.app.original.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class OriginalCommentObject {
    @JSONField(name = "cid")
    public int cid;
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
    @JSONField(name = "is_del")
    public int is_del;
    @JSONField(name = "sname")
    public String sname;
    @JSONField(name = "avatar")
    public String avatar;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public String getSname() {
        return sname;
    }

    public String getAvatar() {
        return avatar;
    }

}
