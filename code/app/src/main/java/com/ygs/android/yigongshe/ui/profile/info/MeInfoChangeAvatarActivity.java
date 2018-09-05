package com.ygs.android.yigongshe.ui.profile.info;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.UserInfoBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.UploadImageBean;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.actionsheet.ActionSheet;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.community.PicSelectActivity;
import com.ygs.android.yigongshe.utils.ImageLoadUtil;
import com.ygs.android.yigongshe.utils.ImageUtils;
import com.ygs.android.yigongshe.utils.PathUtils;
import com.ygs.android.yigongshe.utils.StringUtil;
import com.ygs.android.yigongshe.utils.ZProgressHUD;
import com.ygs.android.yigongshe.view.CircleImageView;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import java.io.File;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class MeInfoChangeAvatarActivity extends BaseActivity implements ActionSheet.MenuItemClickListener {

    /**
     * 拍照标示
     */
    public static final int TAKE_PHOTOS = 1;

    /**
     * 选择图片标示
     */
    public static final int SELECT_PICS = 2;

    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;

    @BindView(R.id.avatar_iv)
    CircleImageView avatarImageView;

    @BindView(R.id.change_avatar_submit_btn)
    Button submitBtn;

    private String avatar;

    /**
     * 当前图片sd 路径
     */
    private File mPhotograph;
    private String mUploadImageUrl;

    @Override
    protected void initIntent(Bundle bundle){

        if (bundle != null){
            avatar = bundle.getString("avatar");
        }
    }


    @Override
    protected void initView(){

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    finish();
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAvatar();
            }
        });

        avatarImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                chooseAvatar();
            }
        });

        if (avatar != null){
            ImageLoadUtil.loadImage(avatarImageView,avatar,R.drawable.defalutavar);
        }else {
            avatarImageView.setImageResource(R.drawable.defalutavar);
        }

    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_meinfo_change_avatar;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_PICS:
                if (data != null) {
                    String selectPic = data.getBundleExtra(BaseActivity.PARAM_INTENT).getString("imageurl");
                    if (!TextUtils.isEmpty(selectPic)) {
                        Bitmap bitmap = BitmapFactory.decodeFile(selectPic);
                        avatarImageView.setImageBitmap(bitmap);
                        compressAndSendImages(selectPic);
                    }
                }

                break;
            case TAKE_PHOTOS:
                if (mPhotograph != null && mPhotograph.exists()) {
                    scanPhotos(mPhotograph.getAbsolutePath(), this);
                    Bitmap bitmap = BitmapFactory.decodeFile(mPhotograph.getAbsolutePath());
                    avatarImageView.setImageBitmap(bitmap);
                    compressAndSendImages(mPhotograph.getAbsolutePath());
                }
                break;
            default:
                break;
        }
    }

    private void chooseAvatar(){

        ActionSheet menuView = new ActionSheet(this);
        menuView.setCancelButtonTitle("取消");// before add items
        menuView.addItems("拍照", "从相册中选择");
        menuView.setItemClickListener(this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();

    }
    @Override
    public void onItemClick(int itemPosition)
    {
        Toast.makeText(this, (itemPosition + 1) + " click", Toast.LENGTH_SHORT).show();
        switch (itemPosition){
            //take photo
            case 0:
            {
                mPhotograph = PathUtils.getPhotoFile();
                // 指定拍照片的文件名
                goToPhoto(mPhotograph);
                break;
            }
            //choose
            case 1:
            {
                goToOthersForResult(PicSelectActivity.class, null, SELECT_PICS);
                break;
            }
            default:
                break;
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
                return ImageUtils.compressImage(selectedImage, newPath,256);
            }

            @Override protected void onPostExecute(String result) {
                uploadNoteImage(result);
            }
        }.
            execute(selectedImage);
    }

    private void uploadNoteImage(String filePath) {

        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part  和后端约定好Key，这里的partName是用image
        MultipartBody.Part body =
            MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // 添加描述
        String descriptionString = "avatar";
        RequestBody description =
            RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
        LinkCall<BaseResultDataInfo<UploadImageBean>> upload = LinkCallHelper.getApiService()
            .uploadRemarkImage(description, body, StringUtil.md5(filePath));
        upload.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UploadImageBean>>() {
            @Override
            public void onResponse(BaseResultDataInfo<UploadImageBean> entity, Response<?> response,
                                   Throwable throwable) {
                if (entity != null && entity.error == ApiStatus.OK) {
                    mUploadImageUrl = entity.data.site_url;
                }else{
                    Toast.makeText(MeInfoChangeAvatarActivity.this,entity.msg,Toast.LENGTH_SHORT).show();
                    mUploadImageUrl = null;
                }
            }
        });
    }

    private void changeAvatar(){

        if (mUploadImageUrl == null || mUploadImageUrl.length() == 0){
            Toast.makeText(this,"请点击头像选择图片",Toast.LENGTH_SHORT).show();
            chooseAvatar();
            return;
        }

        final ZProgressHUD hud = ZProgressHUD.getInstance(this);
        hud.setMessage("上传头像中...");
        hud.show();
        String token = YGApplication.accountManager.getToken();
        LinkCall<BaseResultDataInfo<UserInfoBean>> call = LinkCallHelper.getApiService().modifyAvatar(token,mUploadImageUrl);
        call.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UserInfoBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<UserInfoBean> entity, Response<?> response, Throwable throwable) {
                super.onResponse(entity, response, throwable);
                hud.dismiss();
                if (entity.error == ApiStatus.OK){
                    Toast.makeText(MeInfoChangeAvatarActivity.this,"头像修改成功",Toast.LENGTH_SHORT).show();
                    YGApplication.accountManager.updateAvatar(mUploadImageUrl);
                }
            }
        });
    }

}
