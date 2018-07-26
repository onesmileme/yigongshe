package com.ygs.android.yigongshe.bean;

import java.io.Serializable;
import java.util.List;

public class RunListBean implements Serializable {

    public int page;
    public int perpage;
    public RunItemBean user_info;
    public List<RunItemBean> rank_list;
}
