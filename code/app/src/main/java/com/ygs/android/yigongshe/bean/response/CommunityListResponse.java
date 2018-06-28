package com.ygs.android.yigongshe.bean.response;

import com.ygs.android.yigongshe.bean.CommunityItemBean;
import java.io.Serializable;
import java.util.List;

/**
 * Created by ruichao on 2018/6/28.
 */

public class CommunityListResponse implements Serializable {
  public int page;
  public int perpage;
  public List<CommunityItemBean> list;
}
