package com.ygs.android.yigongshe.ui.profile.focus;

import com.ygs.android.yigongshe.bean.FollowPersonItemBean;
import com.ygs.android.yigongshe.bean.MeFocusBean;

public interface MeFocusFollowListener {

    public void unfollow(FollowPersonItemBean focusBean);

    public void follow(FollowPersonItemBean focusBean);
}
