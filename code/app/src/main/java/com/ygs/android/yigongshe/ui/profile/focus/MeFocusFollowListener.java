package com.ygs.android.yigongshe.ui.profile.focus;

import com.ygs.android.yigongshe.bean.FollowPersonItemBean;
import com.ygs.android.yigongshe.bean.MeFocusBean;

public interface MeFocusFollowListener {

    void unfollow(FollowPersonItemBean focusBean);

    void follow(FollowPersonItemBean focusBean);
}
