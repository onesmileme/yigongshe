package com.ygs.android.yigongshe.bean.response;

import com.ygs.android.yigongshe.bean.HelpVideoItemBean;
import java.io.Serializable;
import java.util.List;

/**
 * Created by ruichao on 2018/7/7.
 */

public class HelpVideoListResponse implements Serializable {
  public int page;
  public int perpage;
  public List<HelpVideoItemBean> video_lsit;//视频列表
}
