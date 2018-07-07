package com.ygs.android.yigongshe.bean.response;

import com.ygs.android.yigongshe.bean.ActivityItemBean;
import java.io.Serializable;
import java.util.List;

/**
 * Created by ruichao on 2018/6/28.
 */

public class ActivityListResponse implements Serializable {
  public int page;
  public int perpage;
  public List<ActivityItemBean> activities;
}
