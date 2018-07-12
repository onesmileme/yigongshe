package com.ygs.android.yigongshe.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by ruichao on 2018/7/12.
 */

public class ImageUtils {

  public static String compressImage(String path, String newPath) {
    return compressImage(path, newPath, 1080);
  }

  public static String compressImage(String path, String newPath, int rect) {
    Bitmap newBitmap = getBitmap(path, rect, false);
    if (newBitmap == null) {
      return path;
    }
    byte[] bytes = revitionImageSize(newBitmap, rect, 100 * 1024);
    InputStream inputStream = new ByteArrayInputStream(bytes);
    boolean result = copyToFile(inputStream, new File(newPath));
    if (result) {
      return newPath;
    } else {
      return path;
    }
  }

  /**
   * 获取图像
   *
   * @param filePath 本地图片地址
   * @param rect 图片尺寸
   * @param isMax 是否是最长边
   */
  public static Bitmap getBitmap(String filePath, int rect, boolean isMax) {
    return getBitmap(filePath, rect, isMax, false);
  }

  /**
   * 获取图像
   *
   * @param filePath 本地图片地址
   * @param rect 图片尺寸
   * @param isMax 是否是最长边
   * @param isZoomOut 是否放大
   */
  public static Bitmap getBitmap(String filePath, int rect, boolean isMax, boolean isZoomOut) {
    InputStream is = null;
    Bitmap photo = null;
    try {
      BitmapFactory.Options opts = new BitmapFactory.Options();
      opts.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(filePath, opts);

      // 得到图片原始宽高
      int photoWidth = opts.outWidth;
      int photoHeight = opts.outHeight;

      // 判断图片是否需要缩放
      is = new FileInputStream(filePath);
      opts = new BitmapFactory.Options();

      if (photoWidth > rect || photoHeight > rect) {
        if (photoWidth > photoHeight) {
          if (isZoomOut) {
            opts.inSampleSize = photoWidth / rect;
          } else {
            opts.inSampleSize = isMax ? photoWidth / rect : photoHeight / rect;
          }
        } else {
          opts.inSampleSize = !isMax ? photoWidth / rect : photoHeight / rect;
        }
      }
      opts.inJustDecodeBounds = false;
      try {
        photo = BitmapFactory.decodeStream(is, null, opts);
      } catch (OutOfMemoryError e1) {
        e1.printStackTrace();
        System.gc();
        try {
          photo = BitmapFactory.decodeFile(filePath, opts);
        } catch (OutOfMemoryError e2) {
          e2.printStackTrace();
          System.gc();
        }
      } finally {
        is.close();
      }
      // photo = BitmapFactory.decodeStream(is, null, opts);
      // is.close();
      photo = rotaingImageView(readPictureDegree(filePath), photo);
      Bitmap resizeBitmap = resizeImage(photo, rect, isMax, isZoomOut);
      if (resizeBitmap != null) {
        photo = resizeBitmap;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    return photo;
  }

  /**
   * 旋转图片
   *
   * @return Bitmap
   */
  public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
    if (angle == 0 || bitmap == null) {
      return bitmap;
    }
    // 旋转图片 动作
    Matrix matrix = new Matrix();
    matrix.postRotate(angle);
    // 创建新的图片
    Bitmap resizedBitmap =
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    return resizedBitmap;
  }

  /**
   * 读取图片属性：旋转的角度
   *
   * @param path 图片绝对路径
   * @return degree旋转的角度
   */
  public static int readPictureDegree(String path) {
    int degree = 0;
    try {
      ExifInterface exifInterface = new ExifInterface(path);
      int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
          ExifInterface.ORIENTATION_NORMAL);
      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90:
          degree = 90;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          degree = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          degree = 270;
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return degree;
  }

  /**
   * 压缩图片
   *
   * @param bitmap 图片对象
   * @param rect 压缩的尺寸
   * @param isMax 是否是最长边
   * @param isZoomOut 是否放大
   */
  public static Bitmap resizeImage(Bitmap bitmap, int rect, boolean isMax, boolean isZoomOut) {
    try {
      // load the origial Bitmap

      int width = bitmap.getWidth();

      int height = bitmap.getHeight();

      if (!isZoomOut && rect >= width && rect >= height) {
        return bitmap;
      }
      int newWidth = 0;
      int newHeight = 0;

      if (isMax || isZoomOut) {
        newWidth = width >= height ? rect : rect * width / height;
        newHeight = width <= height ? rect : rect * height / width;
      } else {
        newWidth = width <= height ? rect : rect * width / height;
        newHeight = width >= height ? rect : rect * height / width;
      }

      if (width >= height) {
        if (isMax) {
          newWidth = rect;
          newHeight = height * newWidth / width;
        } else {
          if (isZoomOut) {
            newWidth = rect;
            newHeight = height * newWidth / width;
          } else {
            newHeight = rect;
            newWidth = width * newHeight / height;
          }
        }
      } else {
        if (!isMax) {
          newWidth = rect;
          newHeight = height * newWidth / width;
        } else {
          newHeight = rect;
          newWidth = width * newHeight / height;
        }
      }

      // calculate the scale
      float scaleWidth = 0f;
      float scaleHeight = 0f;

      scaleWidth = ((float) newWidth) / width;

      scaleHeight = ((float) newHeight) / height;

      Matrix matrix = new Matrix();

      matrix.postScale(scaleWidth, scaleHeight);
      Bitmap newBitmap = null;
      try {
        newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
      } catch (OutOfMemoryError e1) {
        e1.printStackTrace();
        System.gc();
        return null;
      }
      bitmap = newBitmap;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    return bitmap;
  }

  /**
   * 压缩图片的size
   *
   * @param bitmap 图片的数据流
   * @param size 尺寸
   * @throws IOException
   */
  public static byte[] revitionImageSize(Bitmap bitmap, int maxRect, int size) {
    byte[] b = null;
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      bitmap = resizeImage(bitmap, maxRect, true, false);

      if (bitmap == null) {
        return null;
      }

      bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
      b = os.toByteArray();
      int options = 80;
      while (b.length > size) {
        os.reset();
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
        b = os.toByteArray();
        options -= 10;
      }
      os.flush();
      os.close();
      bitmap = BitmapFactory.decodeByteArray(new byte[0], 0, 0);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return b;
  }

  /**
   * Copy data from a source stream to destFile. Return true if succeed,
   * return false if failed.
   */
  public static boolean copyToFile(InputStream inputStream, File destFile) {
    try {
      if (destFile.exists()) {
        destFile.delete();
      }
      FileOutputStream out = new FileOutputStream(destFile);
      try {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) >= 0) {
          out.write(buffer, 0, bytesRead);
        }
      } finally {
        out.flush();
        try {
          out.getFD().sync();
        } catch (IOException e) {
        }
        out.close();
      }
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * 获取图片上传参数map
   */
  public static Map<String, RequestBody> getRequestBodyParams(String path) {
    File image = new File(path);
    RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), image);
    Map<String, RequestBody> params = new HashMap<>();
    params.put("file", requestBody);
    return params;
  }
}
