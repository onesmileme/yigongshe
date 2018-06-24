package com.ygs.android.yigongshe.ui.profile.charitytime;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MeMedalBean;
import com.ygs.android.yigongshe.utils.ImageLoadUtil;

public class MeCharityMedalAdapter extends BaseQuickAdapter<MeMedalBean,BaseViewHolder> {

    MeCharityMedalAdapter(){
        super(R.layout.item_me_medal,null);
    }

    @Override
    protected void convert(BaseViewHolder helper, MeMedalBean item) {

        helper.setText(R.id.me_medal_name_tv,item.name);
        ImageView imageView = helper.getView(R.id.me_medal_icon_iv);
        ImageLoadUtil.loadImage(imageView,item.imgUrl);
    }

}
