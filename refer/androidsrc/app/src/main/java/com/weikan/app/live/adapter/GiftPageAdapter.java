package com.weikan.app.live.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ylp on 2016/11/15.
 */

public class GiftPageAdapter extends PagerAdapter{
    private List<View> mListViews;

    public  GiftPageAdapter(){
        mListViews = new ArrayList<>();
    }
 public void setmListViews(List<View> mListViews){
     this.mListViews.addAll(0,mListViews);
 }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)   {
        container.removeView(mListViews.get(position));//删除页卡
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {  //这个方法用来实例化页卡
        container.addView(mListViews.get(position), 0);//添加页卡
        return mListViews.get(position);
    }

    @Override
    public int getCount() {
        return  mListViews.size();//返回页卡的数量
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0==arg1;//官方提示这样写
    }
}
