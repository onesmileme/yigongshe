package com.ygs.android.yigongshe.ui.community;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.ygs.android.yigongshe.MainActivity;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.PublishCommunityResponse;
import com.ygs.android.yigongshe.bean.response.UploadImageBean;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.utils.ImageUtils;
import com.ygs.android.yigongshe.utils.PathUtils;
import com.ygs.android.yigongshe.utils.StringUtil;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/7/10.
 */

public class PublishCommunityActivity extends BaseActivity implements View.OnClickListener {
  /**
   * 拍照标示
   */
  public static final int TAKE_PHOTOS = 1;

  /**
   * 选择图片标示
   */
  public static final int SELECT_PICS = 2;
  @BindView(R.id.iv_delete) ImageView mDelete;
  @BindView(R.id.item_default_image) ImageView mDefaultImage;
  @BindView(R.id.item_image) ImageView mImage;
  @BindView(R.id.selected_topic) TextView mSelectedTopic;
  private String mSelectedTopicStr;
  private PopupWindow pop;
  private LinearLayout ll_popup;
  private TextView btn_cancel;
  @BindView(R.id.saySth) EditText mEditText;
  @BindView(R.id.titleBar) CommonTitleBar mTitleBar;
  /**
   * 当前图片sd 路径
   */
  private File mPhotograph;
  private String mUploadImageUrl;

  @Override protected void initIntent(Bundle bundle) {

  }

  @Override protected void initView() {
    initPopupWindow();
    mDefaultImage.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        pop.showAtLocation(btn_cancel, Gravity.BOTTOM, 0, 0);
      }
    });

    mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_RIGHT_TEXT) {
          AccountManager accountManager = YGApplication.accountManager;
          if (TextUtils.isEmpty(accountManager.getToken())) {
            Toast.makeText(PublishCommunityActivity.this, "没有登录", Toast.LENGTH_SHORT).show();
            return;
          }
          if (TextUtils.isEmpty(mEditText.getText())) {
            Toast.makeText(PublishCommunityActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
          }
          //else if (TextUtils.isEmpty(mSelectedTopic.getText())) {
          //  Toast.makeText(PublishCommunityActivity.this, "话题不能为空", Toast.LENGTH_SHORT).show();
          //  return;
          //}
          LinkCall<BaseResultDataInfo<PublishCommunityResponse>> pc = LinkCallHelper.getApiService()
              .publishCommunity(accountManager.getToken(), mSelectedTopicStr,
                  mEditText.getText().toString(), mUploadImageUrl, MainActivity.mCityName);
          pc.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<PublishCommunityResponse>>() {
            @Override public void onResponse(BaseResultDataInfo<PublishCommunityResponse> entity,
                Response<?> response, Throwable throwable) {
              super.onResponse(entity, response, throwable);
              if (entity != null) {
                if (entity.error == 2000) {
                  Toast.makeText(PublishCommunityActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                  finish();
                } else {
                  Toast.makeText(PublishCommunityActivity.this, entity.msg, Toast.LENGTH_SHORT)
                      .show();
                }
              }
            }
          });
        } else if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
          finish();
        }
      }
    });
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_publish_community;
  }

  @OnClick({ R.id.iv_delete, R.id.ll_select_topic }) public void onBtnClicked(View iv) {
    switch (iv.getId()) {
      case R.id.iv_delete:
        mDelete.setVisibility(View.GONE);
        mImage.setVisibility(View.GONE);
        mDefaultImage.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            pop.showAtLocation(btn_cancel, Gravity.BOTTOM, 0, 0);
          }
        });
        break;
      case R.id.ll_select_topic:
        goToOthersForResult(TopicSelectActivity.class, null, 0);
        break;
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case SELECT_PICS:
        if (data != null) {
          String selectPic = data.getBundleExtra(BaseActivity.PARAM_INTENT).getString("imageurl");
          if (!TextUtils.isEmpty(selectPic)) {
            mDefaultImage.setOnClickListener(null);
            mDelete.setVisibility(View.VISIBLE);
            mImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                .load(selectPic)
                .placeholder(R.drawable.loading2)
                .error(R.drawable.loading2)
                .fallback(R.drawable.loading2)
                .centerCrop()
                .into(mImage);
            compressAndSendImages(selectPic);
          }
        }

        break;
      case TAKE_PHOTOS:
        if (mPhotograph != null && mPhotograph.exists()) {
          mDefaultImage.setOnClickListener(null);
          mDelete.setVisibility(View.VISIBLE);
          mImage.setVisibility(View.VISIBLE);
          scanPhotos(mPhotograph.getAbsolutePath(), this);
          Glide.with(this)
              .load(mPhotograph.getAbsolutePath())
              .placeholder(R.drawable.loading2)
              .error(R.drawable.loading2)
              .fallback(R.drawable.loading2)
              .centerCrop()
              .into(mImage);
          compressAndSendImages(mPhotograph.getAbsolutePath());
        }
        break;
      default:
        if (null != data) {
          Bundle bundle = data.getBundleExtra(BaseActivity.PARAM_INTENT);
          mSelectedTopicStr = bundle.getString("key");
          if (!TextUtils.isEmpty(mSelectedTopicStr)) {
            mSelectedTopic.setVisibility(View.VISIBLE);
            mSelectedTopic.setText(
                StringUtil.getReleaseString(getResources().getString(R.string.topicItem),
                    new Object[] { mSelectedTopicStr }));
          } else {
            mSelectedTopic.setVisibility(View.GONE);
          }
        }
        break;
    }
  }

  private void initPopupWindow() {
    pop = new PopupWindow(this);
    View view = getLayoutInflater().inflate(R.layout.view_pop_image_selection, null);
    ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
    RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
    TextView btn_camera = (TextView) view.findViewById(R.id.btn_camera);
    TextView btn_photo = (TextView) view.findViewById(R.id.btn_photo);
    btn_cancel = view.findViewById(R.id.btn_cancel);
    btn_camera.setOnClickListener(this);
    btn_photo.setOnClickListener(this);
    btn_cancel.setOnClickListener(this);
    parent.setOnClickListener(this);
    pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
    pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    pop.setBackgroundDrawable(new BitmapDrawable());
    pop.setFocusable(true);
    pop.setOutsideTouchable(true);
    pop.setContentView(view);
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.btn_camera) {
      mPhotograph = PathUtils.getPhotoFile();
      // 指定拍照片的文件名
      goToPhoto(mPhotograph);
      pop.dismiss();
      ll_popup.clearAnimation();
    } else if (id == R.id.btn_photo) {
      //Bundle mBundle = new Bundle();
      goToOthersForResult(PicSelectActivity.class, null, SELECT_PICS);
      pop.dismiss();
      ll_popup.clearAnimation();
    } else if (id == R.id.parent || id == R.id.btn_cancel) {
      pop.dismiss();
      ll_popup.clearAnimation();
    }
  }

  private void goToPhoto(File file) {
    try {
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
      startActivityForResult(intent, TAKE_PHOTOS);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void scanPhotos(String filePath, Context context) {
    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    Uri uri = Uri.fromFile(new File(filePath));
    intent.setData(uri);
    context.sendBroadcast(intent);
  }

  /**
   * 压缩图片到新的存储地址
   */
  @SuppressWarnings("unchecked") private void compressAndSendImages(final String selectedImage) {
    new AsyncTask<String, Integer, String>() {

      @Override protected String doInBackground(String... params) {
        String newPath =
            PathUtils.getChatFilePath(String.valueOf(System.currentTimeMillis())) + ".jpg";
        return ImageUtils.compressImage(selectedImage, newPath);
      }

      @Override protected void onPostExecute(String result) {
        uploadNoteImage(result);
      }
    }.
        execute(selectedImage);
  }

  private void uploadNoteImage(String filePath) {
    //Map<String, String> maps = new HashMap<String, String>();
    //Map<String, RequestBody> params = ImageUtils.getRequestBodyParams(filePath);
    // 创建 RequestBody，用于封装构建RequestBody
    File file = new File(filePath);
    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

    // MultipartBody.Part  和后端约定好Key，这里的partName是用image
    MultipartBody.Part body =
        MultipartBody.Part.createFormData("file", file.getName(), requestFile);

    // 添加描述
    String descriptionString = "hello, 这是文件描述";
    RequestBody description =
        RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
    LinkCall<BaseResultDataInfo<UploadImageBean>> upload = LinkCallHelper.getApiService()
        .uploadRemarkImage(description, body, StringUtil.md5(filePath));
    upload.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UploadImageBean>>() {
      @Override
      public void onResponse(BaseResultDataInfo<UploadImageBean> entity, Response<?> response,
          Throwable throwable) {
        if (entity != null && entity.error == 2000) {
          mUploadImageUrl = entity.data.site_url;
        }
      }
    });
  }
}
