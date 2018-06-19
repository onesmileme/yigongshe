package com.ygs.android.yigongshe.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.DynamicItemBean;
import com.ygs.android.yigongshe.bean.MeItemBean;

import java.util.LinkedList;
import java.util.List;

public class MeProfileAdapter extends BaseQuickAdapter<MeItemBean,BaseViewHolder> {

    List<MeItemBean> beans;

    public MeProfileAdapter(Context context){

        super(R.layout.item_me_profile,null);
        initBeans();
        this.setNewData(beans);

        View headView  = LayoutInflater.from(context).inflate(R.layout.view_me_info,null);
        this.addHeaderView(headView);

    }

    private void initBeans(){

        beans = new LinkedList<>();

        String[] names = {"我的消息","我的关注","我的社团","我的活动","我的益工圈",
                "我的公益时间","我的应用","邀请伙伴"};
        int[] icons = {0};

        for (int i = 0 ; i < names.length ; i++) {
            MeItemBean bean = new MeItemBean();
            bean.title = names[i];
            //bean.icon = icons[i];
            beans.add(bean);
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

    public Class<? extends Activity> detailClassAtPosition(int postion){



        return null;
    }

    private void click(int position){

    }

}
