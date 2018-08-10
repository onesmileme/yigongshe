package com.ygs.android.yigongshe.bean.response;

import java.io.Serializable;

/**
 * Created by ruichao on 2018/7/10.
 */

public class DynamicDetailResponse implements Serializable {
  public DynamicDetailBean news_info;

  public class DynamicDetailBean {
    public String title;//"第11条",
    public String create_at;//"2018-05-01",创建时间
    public String create_name;//"admin",创建者姓名
    public String content;
    public String share_url;
  }
}
