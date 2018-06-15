package com.ygs.android.yigongshe.bean;

import java.io.Serializable;

/**
 * Created by ruichao on 2018/6/15.
 */

public class CommunityItemBean implements Serializable {
  public CommunityItemBean(String title) {
    this.title = title;
  }

  public String title;
}
