package com.ygs.android.yigongshe.bean;

import java.io.Serializable;

/**
 * Created by ruichao on 2018/6/14.
 */

public class DynamicItemBean implements Serializable {
  public DynamicItemBean(String title) {
    this.title = title;
  }

  public int newsid;
  public String title;//"第11条",
  public String desc;//"高大上发大水",
  public String pic;//"",
  public String link;//"",
  public String create_at;//"2018-05-01",
  public int create_id;//10000,
  public String create_name;//"admin"
}
