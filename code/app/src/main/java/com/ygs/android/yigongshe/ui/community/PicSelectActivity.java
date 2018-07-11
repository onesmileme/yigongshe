//package com.ygs.android.yigongshe.ui.community;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AbsListView;
//import android.widget.AdapterView;
//import android.widget.GridView;
//import android.widget.TextView;
//import com.ygs.android.yigongshe.R;
//import com.ygs.android.yigongshe.ui.base.BaseActivity;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class PicSelectActivity extends BaseActivity
//    implements AdapterView.OnItemClickListener,
//    AbsListView.OnScrollListener {
//
//  private View ll_back, ll_send;
//  private TextView btn_cancel, btn_preview, btn_send, tv_send_count;
//  private GridView grid_picture;
//  private String selectedImage = null;
//  private String confirm_text;
//  private HashMap<String, Integer> mSelectedMap = null;
//  private int picCount;
//
//  @Override protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_select_picture);
//    findViews();
//    init();
//    getData();
//  }
//
//  @Override protected void initIntent(Bundle bundle) {
//
//  }
//
//  @Override protected void initView() {
//
//  }
//
//  @Override protected int getLayoutResId() {
//    return R.layout.activity_select_picture;
//  }
//
//
//  private void getData() {
//    new GetPhotosTask(this).execute();
//  }
//
//
//  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//    ImageItem pictureItem = mPicGridAdapter.getItem(position);
//    if (pictureItem.isSelected()) {
//      pictureItem.setSelected(false);
//      selectedImageList.remove(pictureItem);
//      mSelectedMap.remove(pictureItem.getImageId());
//    } else {
//      if (selectedImageList.size() + picCount == SEND_NUMBER_LIMIT) {
//        //				ToastUtil.toast(R.string.send_pic_number_limit);
//        ToastUtil.toast(String.format("最多选择%d张图片", SEND_NUMBER_LIMIT));
//      } else {
//        pictureItem.setSelected(true);
//        selectedImageList.add(pictureItem);
//        // PhotoUtils.selectPhotos.add(pictureItem);
//        mSelectedMap.put(pictureItem.getImageId(), position);
//      }
//    }
//    setViewBySelectedCount();
//    ((onSelectListener) mPicGridAdapter).onSelected(position, view);
//  }
//
//  @Override public void onPostResult(List<ImageItem> result) {
//    if (result != null) {
//      mPicGridAdapter.setDatas(result);
//    }
//  }
//
//  @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
//    pauseOnScrollListener.onScrollStateChanged(view, scrollState);
//  }
//
//  @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
//      int totalItemCount) {
//    pauseOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
//  }
//
//  @Override public void onClick(View v) {
//    int id = v.getId();
//    if (id == R.id.ll_send) {
//      doBackForResult(selectedImageList);
//    } else if (id == R.id.btn_cancel) {
//      doBackForResult(null);
//    } else if (id == R.id.leftContainer) {
//      doBackForResult(null);
//    } else if (id == R.id.btn_preview) {
//      goToGalleryActivity();
//    }
//  }
//
//  private void doBackForResult(Serializable data) {
//    Bundle bundle = new Bundle();
//    bundle.putSerializable(ConstantUtil.DATA, data);
//    backForResult(PicSelectActivity.class, bundle, 1);
//  }
//
//  private void goToGalleryActivity() {
//    if (selectedImageList != null && selectedImageList.size() > 0) {
//      Bundle bundle = new Bundle();
//      bundle.putSerializable(ConstantUtil.DATA, selectedImageList);
//      goToOthersForResult(GalleryPreviewIMActivity.class, bundle, 0);
//    }
//  }
//
//  public interface onSelectListener {
//
//    public void onSelected(int Position, View view);
//  }
//
//  @Override public void onActivityResult(int requestCode, int resultCode, Bundle data) {
//    switch (resultCode) {
//      case GalleryPreviewIMActivity.PHOTO:
//        if (data != null) {
//          updateSelectImgs(data);
//        }
//        break;
//      case GalleryPreviewIMActivity.SEND:
//        if (data != null) {
//          updateSelectImgs(data);
//          doBackForResult(selectedImageList);
//        }
//        break;
//    }
//  }
//
//  /**
//   * �����Ѿ�����ѡ�е�imageItem����ԭ����selectedImageList�б���ȥ�������ܵ�ͼƬ�б��ｫ�
//   * �select״̬��Ϊfalse
//   */
//  private void updateSelectImgs(Bundle data) {
//    ArrayList<ImageItem> selectedImageListOld =
//        (ArrayList<ImageItem>) data.getSerializable(ConstantUtil.DATA);
//    for (int i = 0; i < selectedImageListOld.size(); i++) {
//      ImageItem item = selectedImageListOld.get(i);
//      if (!item.isSelected()) {
//        Integer positionInteger = mSelectedMap.remove(item.getImageId());
//        if (positionInteger != null) {
//          int position = positionInteger;
//          ImageItem picGridItem = mPicGridAdapter.getItem(position);
//          selectedImageList.remove(picGridItem);
//          mPicGridAdapter.getItem(position).setSelected(item.isSelected());
//        }
//      }
//    }
//    setViewBySelectedCount();
//    mPicGridAdapter.notifyDataSetChanged();
//  }
//
//  private void setViewBySelectedCount() {
//    if (selectedImageList.isEmpty()) {
//      tv_send_count.setVisibility(View.GONE);
//      ll_send.setEnabled(false);
//      btn_send.setTextColor(UIUtils.getColor(R.color.lib_core_light_black_1));
//      btn_preview.setEnabled(false);
//      btn_preview.setTextColor(UIUtils.getColor(R.color.lib_core_light_black_1));
//    } else {
//      tv_send_count.setVisibility(View.VISIBLE);
//      tv_send_count.setText("" + selectedImageList.size());
//      ll_send.setEnabled(true);
//      btn_send.setTextColor(UIUtils.getColor(R.color.color_main));
//      btn_preview.setEnabled(true);
//      btn_preview.setTextColor(UIUtils.getColor(R.color.color_main));
//    }
//  }
//}
