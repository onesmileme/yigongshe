package com.ygs.android.yigongshe.bean.response;

import com.ygs.android.yigongshe.bean.DynamicItemBean;
import java.io.Serializable;
import java.util.List;

/**
 * Created by ruichao on 2018/6/17.
 */

public class DynamicListResponse implements Serializable {
  public int page;
  public int perpage;
  public List<DynamicItemBean> news;
}
