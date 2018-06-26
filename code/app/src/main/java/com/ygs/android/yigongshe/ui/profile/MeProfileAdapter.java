package com.ygs.android.yigongshe.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.DynamicItemBean;
import com.ygs.android.yigongshe.bean.MeItemBean;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.profile.activity.MeActivitiesActivity;
import com.ygs.android.yigongshe.ui.profile.apply.MeApplyActivity;
import com.ygs.android.yigongshe.ui.profile.association.MeAssociationActivity;
import com.ygs.android.yigongshe.ui.profile.charitytime.MeCharityTimeActivity;
import com.ygs.android.yigongshe.ui.profile.focus.MeFocusActivity;
import com.ygs.android.yigongshe.ui.profile.message.MessageActivity;
import com.ygs.android.yigongshe.utils.DensityUtil;

import java.util.LinkedList;
import java.util.List;

public class MeProfileAdapter extends BaseQuickAdapter<MeItemBean,BaseViewHolder> {

    List<MeItemBean> beans;

    Context mContext;

    public MeProfileAdapter(Context context){

        super(R.layout.item_me_profile,null);
        initBeans();
        this.setNewData(beans);
        mContext = context;

        View headView  = LayoutInflater.from(context).inflate(R.layout.view_me_info,null);
        int height = DensityUtil.dp2px(context,86);
        ViewGroup.LayoutParams params =  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
        headView.setLayoutParams(params);
        this.addHeaderView(headView);

    }

    private void initBeans(){

        beans = new LinkedList<>();

        String[] names = {"我的消息","我的关注","我的社团","我的活动","我的益工圈",
                "我的公益时间","我的应用","邀请伙伴"};
        int[] icons = {R.drawable.me_message,R.drawable.me_focus,R.drawable.me_association,
        R.drawable.me_activity,R.drawable.me_charity_circle,R.drawable.me_chairty_duration,
        R.drawable.me_app,R.drawable.me_invite};

        for (int i = 0 ; i < names.length ; i++) {
            MeItemBean bean = new MeItemBean();
            bean.title = names[i];
            bean.icon = icons[i];
            if (i == 1 ||i == 4 || i == 7){
                bean.showSplitLine = false;
            }
            beans.add(bean);
        }

        MeItemBean bean = beans.get(beans.size() - 1);
        bean.showIndicator = false;


    }

    @Override
    protected void convert(BaseViewHolder helper, MeItemBean item) {

        helper.setText(R.id.me_item_title, item.title);
        ImageView iconIv = helper.getView(R.id.me_item_icon);
        iconIv.setBackgroundResource(item.icon);
        helper.setVisible(R.id.me_item_indicator,item.showIndicator);
        helper.setVisible(R.id.me_item_splitline,item.showSplitLine);
    }

    public Class<? extends BaseActivity> detailClassAtPosition(int postion){


        switch (postion){
            case 0:{//message
                return MessageActivity.class;
            }
            case 1://我的关注
                return MeFocusActivity.class;
            case 2://我的社团
                return MeAssociationActivity.class;
            case 3://我的活动
                return MeActivitiesActivity.class;
            case 4://我的益工圈
                return null;
            case 5://我的公益时间
                return MeCharityTimeActivity.class;
            case 6://我的应用
                return MeApplyActivity.class;

            default:
                break;
        }

        return null;
    }

//    private void click(int position){
//
//        if (position == beans.size() - 1){
//            //invite people
//            return;
//        }
//
//        Class<? extends  BaseActivity> clazz = detailClassAtPosition(position);
//        if (clazz == null){
//            Log.e(TAG, "click: failed position "+position );
//            return;
//        }
//
//        Intent intent = new Intent(mContext,clazz);
//        mContext.startActivity(intent);
//
//    }

}
