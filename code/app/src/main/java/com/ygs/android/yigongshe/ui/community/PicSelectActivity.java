package com.ygs.android.yigongshe.ui.community;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import butterknife.BindView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.ImageItem;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import java.util.HashMap;
import java.util.List;

public class PicSelectActivity extends BaseActivity
    implements GetPhotosTask.OnPostResultListener<List<ImageItem>> {

  private View ll_back, ll_send;
  private TextView btn_cancel, btn_preview, btn_send, tv_send_count;
  @BindView(R.id.grid_picture) RecyclerView mRecyclerVIew;
  private String selectedImage = null;
  private String confirm_text;
  private HashMap<String, Integer> mSelectedMap = null;
  private int picCount;

  private ImageGridAdapter mPicGridAdapter;

  @Override protected void initIntent(Bundle bundle) {

  }

  protected boolean openTranslucentStatus() {
    return true;
  }

  @Override protected void initView() {
    mPicGridAdapter = new ImageGridAdapter(this);
    mRecyclerVIew.setAdapter(mPicGridAdapter);
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
    gridLayoutManager.setOrientation(GridLayout.VERTICAL);
    mRecyclerVIew.setLayoutManager(gridLayoutManager);
    getData();
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_select_picture;
  }

  private void getData() {
    new GetPhotosTask(this).execute();
  }

  @Override public void onPostResult(List<ImageItem> result) {
    if (result != null) {
      mPicGridAdapter.addData(result);
    }
  }
}
