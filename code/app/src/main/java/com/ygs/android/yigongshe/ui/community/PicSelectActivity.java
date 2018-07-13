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
import com.ygs.android.yigongshe.view.CommonTitleBar;
import java.util.HashMap;
import java.util.List;

public class PicSelectActivity extends BaseActivity
    implements GetPhotosTask.OnPostResultListener<List<ImageItem>>, ImageGridAdapter.CheckListener {

  @BindView(R.id.grid_picture) RecyclerView mRecyclerVIew;
  private ImageGridAdapter mPicGridAdapter;
  @BindView(R.id.titleBar) CommonTitleBar mTitleBar;
  private String mImageUrl;

  @Override protected void initIntent(Bundle bundle) {

  }

  protected boolean openTranslucentStatus() {
    return true;
  }

  @Override protected void initView() {
    mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_RIGHT_TEXT) {
          Bundle bundle = new Bundle();
          bundle.putString("imageurl", mImageUrl);
          backForResult(PublishCommunityActivity.class, bundle, 0);
          finish();
        }
      }
    });
    mPicGridAdapter = new ImageGridAdapter(this, this);
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

  @Override public void onCheckChanged(ImageItem imageItem) {
    mImageUrl = imageItem.imageUrl;
  }
}
