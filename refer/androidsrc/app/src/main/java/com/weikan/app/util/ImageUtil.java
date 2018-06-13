package com.weikan.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Real on 15/3/22.
 */
public class ImageUtil {
    public static final String TAG = "ImageUtil";
    public static final int PHOTO_MAX_LENGTH_OF_EDGE = 800;

    static File sLastPhotoFilePath = null;

    public static File lastPhotoFile(){
        return sLastPhotoFilePath;
    }

    public static void setLastPhotoFile(File file){
        sLastPhotoFilePath = file;
    }

    public static File createTmpImageFilePath(Context context)
    {
        File photoDir = context.getExternalCacheDir();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        String photoName = formatter.format(new Date()) + ".jpg";

        return new File(photoDir, photoName);
    }

    public static int calculateRatio(int src, int dest) {
        double ratio = (double) src / dest;
        int num = 1;
        if (ratio > 0 && ratio < 0.5) {
            num = 1;
        } else {
            num = (int) Math.round(ratio);
        }
        return num;
    }

    public static void compressImageUri(Context context, Uri src, File dst) {

        // 读取照片大小
        BitmapFactory.Options opts = new BitmapFactory.Options();
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(src);
            opts.inJustDecodeBounds = true;// 仅仅读取图片尺寸
            BitmapFactory.decodeStream(is, null, opts);
        }catch (IOException e) {
            try {
                is.close();
            }catch (IOException ex) {
            }
        }

        int sw = opts.outWidth, sh = opts.outHeight;
        int edge = (sw < sh ? sw : sh);

        // 以一定的缩小比例读取图片
        opts = new BitmapFactory.Options();
        opts.inSampleSize = calculateRatio(edge, ImageUtil.PHOTO_MAX_LENGTH_OF_EDGE);// 缩放的比例，新算法改为四舍五入
        opts.inJustDecodeBounds = false;

        // 输出到文件
        FileOutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(src);
            Bitmap bmp = BitmapFactory.decodeStream(is, null, opts);//压缩图片尺寸

            os = new FileOutputStream(dst);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, os);
        }catch (IOException e) {
            try {
                is.close();
            }catch (IOException ex) {
            }
            try {
                os.close();
            }catch (IOException ex) {
            }
        }

    }

    public static void compressImageFile(File src, File dst, int rotate){

        // 读取照片大小
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 仅仅读取图片尺寸
        BitmapFactory.decodeFile(src.getAbsolutePath(), opts);

        int sw = opts.outWidth, sh = opts.outHeight;
        int edge = (sw < sh ? sw : sh);

        // 以一定的缩小比例读取图片
        opts = new BitmapFactory.Options();
        opts.inSampleSize = calculateRatio(edge, ImageUtil.PHOTO_MAX_LENGTH_OF_EDGE);// 缩放的比例，新算法改为四舍五入
        opts.inJustDecodeBounds = false;

        Bitmap is = BitmapFactory.decodeFile(src.getAbsolutePath(), opts);//压缩图片尺寸
        if(rotate != 0){
            is = ImageUtil.rotate(is, rotate);
        }

        // 输出到文件
        FileOutputStream os = null;
        try
        {
            os = new FileOutputStream(dst);
            is.compress(Bitmap.CompressFormat.JPEG, 80, os);
        }catch (IOException e) {
            Log.e(TAG, e.toString());
            try{
                os.close();
            }catch (Exception ex){
            }
        }
    }

    public static Bitmap rotate(Bitmap src, int degrees) {

        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, src.getWidth() / 2, src.getHeight() / 2);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static Bitmap compressBitmap(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 1920;//这里设置高度为800f
        float ww = 1280;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;
    }

    public static Bitmap compressBitmap(Bitmap image,float reqWidth, float reqHeight) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bitmapSize = getBitmapSize(image);
        int radio = 100;
        if (bitmapSize / 1024 > 1024) {
            radio = 50;
        }
        image.compress(Bitmap.CompressFormat.JPEG, radio, baos);
//        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
//            baos.reset();//重置baos即清空baos
//            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
//        }
//        image.recycle();

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
//        newOpts.inJustDecodeBounds = true;
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        newOpts.inTempStorage = new byte[16 * 1024];
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_4444;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        int w = image.getWidth();
        int h = image.getHeight();
//        int w = newOpts.outWidth;
//        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = reqHeight;//1920;//这里设置高度为800f
        float ww = reqWidth;//1280;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (w / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (h / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了

//        isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        try{

            baos.close();
            isBm.close();

        }catch (Exception e){

        }

        return bitmap;
    }

    public static int getBitmapSize(Bitmap bitmap){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){    //API 19
            return bitmap.getAllocationByteCount()/8;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){//API 12
            return bitmap.getByteCount()/8;
        }
        return bitmap.getRowBytes() * bitmap.getHeight()/8;                //earlier version
    }

    public static int readRotateDegree(File src) {
    	if(src == null){
    		return 0;
    	}
        ExifInterface exif = null;
        try{
           exif = new ExifInterface(src.getAbsolutePath());
        }catch (IOException e) {
            return 0;
        }

        String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);

        if("1".equals(orientation)){
            return 0;
        }

        if("3".equals(orientation)){
            return 180;
        }

        if("6".equals(orientation)){
            return 90;
        }

        if("8".equals(orientation)){
            return -90;
        }

        return 0;
    }
}
