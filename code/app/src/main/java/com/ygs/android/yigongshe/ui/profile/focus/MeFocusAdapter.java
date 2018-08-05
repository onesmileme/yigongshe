package com.ygs.android.yigongshe.ui.profile.focus;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.FollowPersonItemBean;
import com.ygs.android.yigongshe.bean.MeFocusBean;
import com.ygs.android.yigongshe.utils.ImageLoadUtil;

public class MeFocusAdapter extends BaseQuickAdapter<FollowPersonItemBean,BaseViewHolder> {

    Context mContext;

    MeFocusFollowListener mFocusFollowListener;

    MeFocusAdapter(Context context,MeFocusFollowListener followListener){
        super(R.layout.item_me_focus,null);
        mContext = context;
        mFocusFollowListener = followListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, final FollowPersonItemBean item) {

        ImageView avatarImgView = helper.getView(R.id.me_focus_icon);
        //Drawable placeholder = avatarImgView.getContext().getResources().getDrawable(R.drawable.defalutavar);
        ImageLoadUtil.loadImage(avatarImgView,item.avatar,R.drawable.defalutavar);

        helper.setText(R.id.me_focus_name_tv,item.name);
        Button button = helper.getView(R.id.me_focus_follow_btn);

        if (item.unfollowed){

        }else{

        }

        button.setBackground(mContext.getResources().getDrawable(R.drawable.focus_normal_round_btn_shape));
        button.setText(item.unfollowed?R.string.followed:R.string.unfollow);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFocusFollowListener != null){
                    if (item.unfollowed){
                        mFocusFollowListener.follow(item);
                    }else {
                        mFocusFollowListener.unfollow(item);
                    }
                }
            }
        });

    }

}
