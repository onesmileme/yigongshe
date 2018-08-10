package com.ygs.android.yigongshe.utils;


import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.ygs.android.yigongshe.view.GlideCircleTransform;

public class ImageLoadUtil {

    public static void loadImage(ImageView imageView , String imgUrl){

        Glide.with(imageView.getContext()).load(imgUrl).into(imageView);
    }

    public static void loadImage(ImageView imageView , String imgUrl , Drawable placeHolder){
        Glide.with(imageView.getContext()).load(imgUrl).dontAnimate().placeholder(placeHolder).error(placeHolder).fallback(placeHolder).into(imageView);
    }

    public static void loadImage(ImageView imageView , String imgUrl , int placeHolder){
        Glide.with(imageView.getContext())
            .load(imgUrl)
            .dontAnimate()
            .placeholder(placeHolder)
            .error(placeHolder)
            .fallback(placeHolder)
            //.transform(new CenterCrop(imageView.getContext()),new GlideCircleTransform(imageView.getContext()))
            .into(imageView);
        //Glide.with(imageView.getContext())
        //    .load(imgUrl)
        //    .placeholder(placeHolder)
        //    .error(placeHolder)
        //    .fallback(placeHolder)
        //    .transform(new CenterCrop(imageView.getContext()),new GlideCircleTransform(imageView.getContext()))
        //    .into(imageView);

        /*
        * Glide.with(mContext)
        .load(item.create_avatar)
        .placeholder(R.drawable.defalutavar)
        .error(R.drawable.defalutavar)
        .fallback(R.drawable.defalutavar)
        .transform(new CenterCrop(mContext), new GlideCircleTransform(mContext))
        .into((ImageView) helper.getView(R.id.createAvatar));
        * */
    }
}
