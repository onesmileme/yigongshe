package com.ygs.android.yigongshe.ui.community;

import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore.Images.Media;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.ImageItem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GetPhotosTask extends AsyncTask<String, Integer, List<ImageItem>> {

  private static final String TAG = GetPhotosTask.class.getSimpleName();

  private static final String[] ACCEPTABLE_IMAGE_TYPES = new String[] {
      "image/jpeg", "image/png", "image/bmp"
  };

  private static final String WHERE_CLAUSE = "(" + Media.MIME_TYPE + " in (?, ?, ?,?))";

  private OnPostResultListener<List<ImageItem>> resultListener;

  public GetPhotosTask(OnPostResultListener<List<ImageItem>> resultListener) {
    this.resultListener = resultListener;
  }

  @Override protected List<ImageItem> doInBackground(String... params) {
    return GetPhotosTask.this.universalRetrievePhotos();
  }

  /**
   * 注意魅族手机，简直不能更奇葩，系统gallery居然没有遍历所有文件夹下的图片，所以本段代码获取到的图片可能比系统gallery还要多
   * 所以我给它起名为UniversalRetrieve，万能检索
   */
  public List<ImageItem> universalRetrievePhotos() {

    List<ImageItem> itemList = new ArrayList<>();
    Cursor cursor = null;
    try {
       /* 查询照片 */
      cursor = YGApplication.mApplication.getContentResolver()
          .query(Media.EXTERNAL_CONTENT_URI, null, WHERE_CLAUSE, ACCEPTABLE_IMAGE_TYPES,
              Media.DATE_MODIFIED);

      if (cursor == null) {
        return itemList;
      }

      ImageItem imageItemInstance = new ImageItem();

      while (cursor.moveToNext()) {
        /* 获取图片路径 */
        String path = cursor.getString(cursor.getColumnIndex(Media.DATA));

				/* 排除掉路径存在于系统数据库，但是图片不存在的情况 */
        if (path != null && new File(path).exists()) {

					/* 在大量Entity被创建的场景中，原型模式可有效减少内存消耗 */
          ImageItem clone = imageItemInstance.newInstance();

          ///* 获取图片ID */
          //String imageId = cursor.getString(cursor.getColumnIndex(Media._ID));
          //long addData = cursor.getLong(cursor.getColumnIndexOrThrow(Media.DATE_ADDED));
          //String imageName = cursor.getString(cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME));
          clone.imageUrl = path;
          itemList.add(clone);
        }
      }
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }

    return itemList;
  }

  @Override protected void onPostExecute(List<ImageItem> result) {
    if (resultListener != null) {
      resultListener.onPostResult(result);
    }
  }

  public interface OnPostResultListener<T> {
    void onPostResult(T result);
  }
}
