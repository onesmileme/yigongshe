package com.ygs.android.yigongshe.bean.response;

import com.ygs.android.yigongshe.bean.RoleInfoBean;
import com.ygs.android.yigongshe.bean.SchoolInfoBean;

import java.io.Serializable;
import java.util.List;

public class SchoolInfoListResponse implements Serializable {

    public List<SchoolInfoBean> schools;
    public List<RoleInfoBean> roles;

}
