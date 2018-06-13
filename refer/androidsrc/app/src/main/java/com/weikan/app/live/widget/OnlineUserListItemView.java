package com.weikan.app.live.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.weikan.app.R;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.live.bean.OnlineUserObject;
import com.weikan.app.util.ImageLoaderUtil;

/**
 * @author kailun on 16/8/31.
 */
public class OnlineUserListItemView extends BaseListItemView<OnlineUserObject> {

    private ImageView ivAvatar;

    public OnlineUserListItemView(Context context) {
        super(context);
    }

    @Override
    public int layoutResourceId() {
        return R.layout.widget_live_online_user_list_item;
    }

    @Override
    protected void initViews() {
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        ivAvatar.setImageResource(R.drawable.favtag_7);
    }

    @Override
    public void set(@Nullable OnlineUserObject item) {
        super.set(item);

        if (item != null) {
            ImageLoaderUtil.updateImage(ivAvatar, item.headImgUrl, R.drawable.user_default);
        }
    }
}
