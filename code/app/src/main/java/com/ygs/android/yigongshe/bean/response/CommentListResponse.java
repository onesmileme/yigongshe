package com.ygs.android.yigongshe.bean.response;

import com.ygs.android.yigongshe.bean.CommentItemBean;
import java.io.Serializable;
import java.util.List;

/**
 * Created by ruichao on 2018/6/27.
 */

public class CommentListResponse implements Serializable {
  public List<CommentItemBean> list;
}
