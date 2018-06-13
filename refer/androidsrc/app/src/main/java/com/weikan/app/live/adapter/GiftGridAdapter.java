package com.weikan.app.live.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.live.bean.GiftObject;
import com.weikan.app.selectimage.ImageLoader;
import com.weikan.app.util.ImageLoaderUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ylp on 2016/11/15.
 */

public class GiftGridAdapter extends BaseAdapter {
    private Context context;
    private List<GiftObject.Gift> giftList;
    public GiftGridAdapter(Context context){
        giftList = new ArrayList<>();
        this.context = context;
    }
    public void setGiftList(List<GiftObject.Gift> giftList){
        this.giftList.addAll(giftList);
    }
    @Override
    public int getCount() {
        return giftList == null || giftList.size() == 0 ? 0 :giftList.size();
    }

    @Override
    public Object getItem(int position) {
        return giftList == null || giftList.size() == 0 ? null :giftList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gift,null);
            viewHolder = new ViewHolder();
            viewHolder.ivGift = (ImageView) convertView.findViewById(R.id.iv_gift);
            viewHolder.ivChoose = (ImageView)convertView.findViewById(R.id.iv_gift_check);
            viewHolder.tvPrice = (TextView)convertView.findViewById(R.id.tv_gift_price);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        GiftObject.Gift gift = giftList.get(position);
        if(gift == null){
            viewHolder.ivChoose.setVisibility(View.INVISIBLE);
            viewHolder.ivGift.setVisibility(View.INVISIBLE);
            viewHolder.tvPrice.setVisibility(View.INVISIBLE);
        }else{
            if(gift.status == 1){
                viewHolder.ivChoose.setVisibility(View.VISIBLE);
            }else{
                viewHolder.ivChoose.setVisibility(View.INVISIBLE);
            }
            viewHolder.ivGift.setVisibility(View.VISIBLE);
            viewHolder.tvPrice.setVisibility(View.VISIBLE);
            viewHolder.tvPrice.setText(gift.emoney);
            ImageLoaderUtil.updateImage(viewHolder.ivGift,gift.url);
        }
        return convertView;
    }
    public void setChooseorClear(String id){
        for(GiftObject.Gift gift : giftList){
           if(gift != null ){
               if(gift.id.equals(id)){
                   gift.status = 1;
               }else{
                   gift.status = 0;
               }
           }
        }
        notifyDataSetChanged();
    }
    class ViewHolder{
        ImageView ivGift;
        ImageView ivChoose;
        TextView tvPrice;
    }
}
