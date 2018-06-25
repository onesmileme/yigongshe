package com.ygs.android.yigongshe.ui.profile.focus;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MeFocusBean;
import com.ygs.android.yigongshe.utils.ImageLoadUtil;

public class MeFocusAdapter extends BaseQuickAdapter<MeFocusBean,BaseViewHolder> {

    Context mContext;

    MeFocusAdapter(Context context){
        super(R.layout.item_me_focus,null);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, MeFocusBean item) {

        ImageView avatarImgView = helper.getView(R.id.me_focus_icon);
        ImageLoadUtil.loadImage(avatarImgView,item.imageUrl);

        helper.setText(R.id.me_focus_name_tv,item.name);

        Button button = helper.getView(R.id.me_focus_follow_btn);


        button.setBackground(mContext.getResources().getDrawable(R.drawable.focus_normal_round_btn_shape));

    }


}
