package com.ygs.android.yigongshe.bean.response;

import com.google.gson.annotations.SerializedName;
import com.ygs.android.yigongshe.bean.ActivityItemBean;
import java.io.Serializable;
import java.util.List;

/**
 * Created by ruichao on 2018/6/28.
 */

public class ActivityListResponse implements Serializable {
  public int page;
  public int perpage;
  @SerializedName(value = "activities", alternate = { "activity_list" })
  public List<ActivityItemBean> activities;
}
