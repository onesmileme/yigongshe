package com.ygs.android.yigongshe.ui.profile.info;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MeInfoItemBean;
import com.ygs.android.yigongshe.utils.ImageLoadUtil;

public class MeInfoAdapter extends BaseQuickAdapter<MeInfoItemBean,BaseViewHolder> {

    MeInfoAdapter(){
        super(R.layout.item_me_info,null);
    }

    @Override
    protected void convert(BaseViewHolder helper, MeInfoItemBean item) {

        TextView valueTextView = helper.getView(R.id.me_info_value_tv);
        ImageView imageView = helper.getView(R.id.me_info_avatar_iv);
        if (item.imgUrl != null){
            ImageLoadUtil.loadImage(imageView,item.imgUrl);
            imageView.setVisibility(View.VISIBLE);
            valueTextView.setVisibility(View.GONE);
        }else{
            valueTextView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            valueTextView.setText(item.value);
        }
        helper.setText(R.id.me_info_name_tv,item.name);
    }
}
