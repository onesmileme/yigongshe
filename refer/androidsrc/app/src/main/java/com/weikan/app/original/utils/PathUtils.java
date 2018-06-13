package com.weikan.app.original.utils;

import android.util.Log;
import com.weikan.app.MainApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/6
 */
public class PathUtils {

    public static final String TAG = "PathUtils";

    /// 取得Documents目录
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String confirmDocumentPath() {
        File documentsPath = MainApplication.getInstance().getExternalFilesDir("image");
        if (documentsPath != null) {
            documentsPath.mkdirs();
            Log.i(TAG, documentsPath.getAbsolutePath());
            return documentsPath.getAbsolutePath();
        }

        return "";
    }

    private static String confirmTmpPathWithoutPostfix() {
        String folder = confirmDocumentPath();

        Date date = new Date();
        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyy-MM-dd-HH-mm-ss-SSS", Locale.US);
        String strNowTime = timeFormatter.format(date);

        return new File(folder, strNowTime).getAbsolutePath();
    }

    /// 创建一个临时的Jpg文件路径
    public static String confirmTmpJpegPath() {
        return confirmTmpPathWithoutPostfix() + ".jpg";
    }

    /// 创建一个临时的Png文件路径
    public static String confirmTmpPngPath() {
        return confirmTmpPathWithoutPostfix() + ".png";
    }

    /// 清空所有图片文件
//    static func clean() {
//        let folder = confirmDocumentPath()
//
//        NSLog(folder)
//
//        let fileManager = NSFileManager.defaultManager()
//        do{
//            try fileManager.removeItemAtPath(folder)
//        } catch {
//        }
//    }

}
