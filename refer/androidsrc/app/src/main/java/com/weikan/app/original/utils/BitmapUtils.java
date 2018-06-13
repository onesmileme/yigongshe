package com.weikan.app.original.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/9
 */
public class BitmapUtils {

    public static void savePng(Bitmap bm, String filePath) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
            bm.compress(Bitmap.CompressFormat.PNG, 100, os);
        } catch (IOException ignored) {
        } finally {
            try {
                if(os != null) {
                    os.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}
