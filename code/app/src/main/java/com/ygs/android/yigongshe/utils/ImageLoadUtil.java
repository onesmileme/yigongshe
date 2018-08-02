package com.ygs.android.yigongshe.utils;


import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class ImageLoadUtil {

    public static void loadImage(ImageView imageView , String imgUrl){

        Glide.with(imageView.getContext()).load(imgUrl).into(imageView);
    }

    public static void loadImage(ImageView imageView , String imgUrl , Drawable placeHolder){
        Glide.with(imageView.getContext()).load(imgUrl).error(placeHolder).fallback(placeHolder).into(imageView);
    }

    public static void loadImage(ImageView imageView , String imgUrl , int placeHolder){
        Glide.with(imageView.getContext()).load(imgUrl).error(placeHolder).fallback(placeHolder).into(imageView);

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
