package com.ygs.android.yigongshe.ui.profile;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.DynamicItemBean;
import com.ygs.android.yigongshe.bean.MeItemBean;

import java.util.LinkedList;
import java.util.List;

public class MeProfileAdapter extends BaseQuickAdapter<MeItemBean,BaseViewHolder> {

    List<MeItemBean> beans;

    public MeProfileAdapter(){

        super(R.layout.item_me_profile,null);
        initBeans();
        this.setNewData(beans);
    }

    private void initBeans(){

        beans = new LinkedList<>();

        String[] names = {"我的消息"};
        int[] icons = {0};

        for (int i = 0 ; i < names.length ; i++) {
            MeItemBean bean = new MeItemBean();
            bean.title = names[i];
            bean.icon = icons[i];
        }

        MeItemBean bean = beans.get(beans.size() - 1);
        bean.showIndicator = false;


    }

    @Override
    protected void convert(BaseViewHolder helper, MeItemBean item) {
        helper.setText(R.id.me_item_title, item.title);
        helper.setImageResource(R.id.me_item_icon,item.icon);
        helper.setVisible(R.id.me_item_indicator,item.showIndicator);
    }
}
