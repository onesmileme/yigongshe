package com.ygs.android.yigongshe.bean;

import java.io.Serializable;
import java.util.List;

public class FollowPersonDataBean implements Serializable {

    public int page;
    public int perpage;
    public List<FollowPersonItemBean> list;
}
