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
        Glide.with(imageView.getContext()).load(imgUrl).placeholder(placeHolder).fallback(placeHolder).into(imageView);
    }
}
