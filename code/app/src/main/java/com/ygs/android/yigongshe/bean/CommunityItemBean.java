package com.ygs.android.yigongshe.bean;

import java.io.Serializable;

/**
 * Created by ruichao on 2018/6/15.
 */

public class CommunityItemBean implements Serializable {
  public int pubcircleid;
  public String title;
  public String topic;//话题，过个话题“，”分割
  public String pic;//图片url,多个图片“，”分割
  public String content;
  public String create_at;
  public int create_id;
  public String create_name;
  public String create_avatar;
  public int is_follow; //是否关注作者，0：未关注，1，已关注
  public int is_like;
  public int like_num;
}
