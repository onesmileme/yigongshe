package com.ygs.android.yigongshe.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 公益记录
 */
public class CharityRecordBean implements Serializable {

    public int page;
    public int perpage;
    public List<CharityRecordItemBean> list;

}
