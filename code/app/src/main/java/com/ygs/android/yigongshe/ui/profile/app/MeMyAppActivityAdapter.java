package com.ygs.android.yigongshe.ui.profile.app;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MyAppItemBean;

public class MeMyAppActivityAdapter extends BaseQuickAdapter<MyAppItemBean,BaseViewHolder> {

    public MeMyAppActivityAdapter(){
        super(R.layout.item_me_myapp,null);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyAppItemBean item) {

        helper.setText(R.id.myapp_tv,item.name);

    }
}
