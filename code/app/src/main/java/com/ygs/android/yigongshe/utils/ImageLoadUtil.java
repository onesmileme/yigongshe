package com.ygs.android.yigongshe.utils;


import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class ImageLoadUtil {

    public static void loadImage(ImageView imageView , String imgUrl){

        Glide.with(imageView.getContext()).load(imgUrl).into(imageView);
    }
}
