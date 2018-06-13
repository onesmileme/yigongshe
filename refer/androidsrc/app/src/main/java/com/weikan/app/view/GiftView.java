package com.weikan.app.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.widget.roundedimageview.CircleImageView;
import com.weikan.app.widget.roundedimageview.RoundedImageView;

/**
 * Created by ylp on 2016/10/14.
 */

public class GiftView{
    public static View creatGiftView(Context context,String headUrl,String userName,String giftDes,String giftUrl){
        View view = LayoutInflater.from(context).inflate(R.layout.view_gift,null);
       CircleImageView head = (CircleImageView)view.findViewById(R.id.user_head);
        TextView user = (TextView)view.findViewById(R.id.username);
        TextView gift = (TextView)view.findViewById(R.id.gift_des);
        ImageView giftImg = (ImageView)view.findViewById(R.id.gift);
        ImageLoaderUtil.updateImage(head,headUrl,R.drawable.user_default);
        user.setText(userName);
        gift.setText(giftDes);
        ImageLoaderUtil.updateImage(giftImg,giftUrl);
        return view;
    }
}
