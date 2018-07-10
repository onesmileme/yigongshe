package com.ygs.android.yigongshe.bean;

import java.io.Serializable;

public class FollowPersonItemBean implements Serializable{

    /*
    *   "create_at":"2018-06-20",
                "userid":"10002",
                "name":"glf01",
                "avatar":"http://127.0.0.1:8080/ygs/static/upload/53db31b42b12cd6f/b07ad7c2ded20fcc.jpg"
    * */

    public String create_at;
    public String userid;
    public String name;
    public String avatar;
    public boolean unfollowed; //whether user unfollowd
}
