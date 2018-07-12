package com.ygs.android.yigongshe.utils;

import android.os.Environment;
import java.io.File;

/**
 * Created by ruichao on 2018/7/12.
 */

public class PathUtils {
  public static String getChatFileDir() {
    String dir = getAppPath() + "files/";
    return checkAndMkdirs(dir);
  }

  public static String getChatFilePath(String id) {
    String dir = getChatFileDir();
    String path = dir + id;
    return path;
  }

  public static String getAppPath() {
    String dir = getSDcardDir() + "im/";
    return checkAndMkdirs(dir);
  }

  public static String checkAndMkdirs(String dir) {
    File file = new File(dir);
    if (file.exists() == false) {
      file.mkdirs();
    }
    return dir;
  }

  public static String getSDcardDir() {
    return Environment.getExternalStorageDirectory().getPath() + "/";
  }

  public static File getPhotoFile() {
    return new File(getPhoneExternalCamaraPhotosPath(), System.currentTimeMillis() + ".jpg");
  }

  public static String getPhoneExternalCamaraPhotosPath() {
    return new StringBuilder(getSDcardDir()).append("DCIM/Camera").toString();
  }
}
