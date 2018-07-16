package com.ygs.android.yigongshe.bean;

import java.io.Serializable;

/**
 * Created by ruichao on 2018/6/26.
 */

public class CommentItemBean implements Serializable {
  public int commentid;
  public String content; //留言内容
  public String pic;
  public String create_at; //创建日期
  public int create_id;
  public String create_name;
  public String create_avatar;
  public int is_mine; //是否是自己发表的 0：不是，1
}
