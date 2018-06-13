package com.weikan.app.original;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.*;
import com.alibaba.fastjson.JSONArray;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.weikan.app.Constants;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.original.bean.ImageModifiedContentObject;
import com.weikan.app.original.bean.ImageNtsObject;
import com.weikan.app.original.bean.ImageObject;
import com.weikan.app.original.bean.OriginalDetailItem;
import com.weikan.app.original.bean.OverlayObject;
import com.weikan.app.original.bean.PicObject;
import com.weikan.app.original.bean.UploadImageObject;
import com.weikan.app.original.event.OriginalDeleteEvent;
import com.weikan.app.original.event.OriginalEditEvent;
import com.weikan.app.original.event.OriginalPublishEvent;
import com.weikan.app.util.LToast;
import com.weikan.app.util.URLDefine;
import de.greenrobot.event.EventBus;
import rx.functions.Action1;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改帖子界面
 * Created by liujian on 16/3/27.
 */
public class DetailEditActivity extends BaseActivity {
//
//    // 详情页实体
//    private OriginalDetailObject.OriginalDetailItem mOriginalObject;
//
//    private ImageNtsObject origin;
//
//    // 修改后的图像路径
//    private String imageModifiedPath;
//
//    // 修改后叠加的Overlays
//    private List<OverlayObject> overlayObjects;
//
//    private int type;
//
//    private EditText etTitle;
//    private EditText etContent;
//    private Button btnDelete;
//    private SimpleDraweeView imageDetail;
//    private GridView gridPics;
//
//    private TextView tvTitleNum;
//    private TextView tvContentNum;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail_edit);
//
//        parseIntent();
//        if (mOriginalObject == null) {
//            finish();
//            return;
//        }
//
//        updateUI();
//        updateTitle();
//    }
//
//    @SuppressWarnings("unchecked")
//    private void parseIntent() {
//        Intent intent = getIntent();
//        if (intent != null) {
//            mOriginalObject = (OriginalDetailObject.OriginalDetailItem)
//                    intent.getSerializableExtra(Constants.DETAIL_OBJECT);
//            type = intent.getIntExtra(OriginalConsts.BUNDLE_EDIT_TYPE, OriginalConsts.EDIT_TYPE_MODIFY);
//            origin = (ImageNtsObject) intent.getSerializableExtra(OriginalConsts.BUNDLE_ORIGIN);
//            imageModifiedPath = intent.getStringExtra(OriginalConsts.BUNDLE_MODIFIED_FILE);
//            overlayObjects = (List<OverlayObject>) intent.getSerializableExtra(OriginalConsts.BUNDLE_OVERLAYS);
//        }
//    }
//
//    private void updateUI() {
//        etTitle = (EditText) findViewById(R.id.et_edit_title);
//        etContent = (EditText) findViewById(R.id.et_edit_content);
//        btnDelete = (Button) findViewById(R.id.bt_edit_detail_delete);
//        imageDetail = (SimpleDraweeView) findViewById(R.id.iv_edit_pic);
//        tvTitleNum = (TextView) findViewById(R.id.tv_title_num);
//        tvContentNum = (TextView) findViewById(R.id.tv_content_num);
//        gridPics = (GridView) findViewById(R.id.gv_pic_list);
//        gridPics.setVisibility(View.GONE);
//        // 焦点放在图片，这样就不出输入框
//        imageDetail.requestFocus();
//
//        // 设置输入的文字数目
//        RxTextView.textChangeEvents(etTitle)
//                .subscribe(new Action1<TextViewTextChangeEvent>() {
//                    @Override
//                    public void call(TextViewTextChangeEvent textViewTextChangeEvent) {
//                        int len = etTitle.getText().length();
//                        tvTitleNum.setText(len + "/60");
//                    }
//                });
//        // 设置输入的文字数目
//        RxTextView.textChangeEvents(etContent)
//                .subscribe(new Action1<TextViewTextChangeEvent>() {
//                    @Override
//                    public void call(TextViewTextChangeEvent textViewTextChangeEvent) {
//                        int len = etContent.getText().length();
//                        tvContentNum.setText(len + "/2048");
//                    }
//                });
//
//        etTitle.setText(mOriginalObject.title);
//
//        if (mOriginalObject.item_list != null && mOriginalObject.item_list.size() > 0) {
//            PicObject pic = mOriginalObject.item_list.get(0).img;
//            if (pic != null) {
//                imageDetail.setAspectRatio((float) pic.w / (float) pic.h);
//                imageDetail.getHierarchy().setPlaceholderImage(R.drawable.image_bg);
//
//                if (type == OriginalConsts.EDIT_TYPE_MODIFY) {
//                    final Uri uri = Uri.parse(pic.url);
//                    DraweeController controller = Fresco.newDraweeControllerBuilder()
//                            .setUri(uri)
//                            .setAutoPlayAnimations(true)
//                            .build();
//                    imageDetail.setController(controller);
//
//                } else if (type == OriginalConsts.EDIT_TYPE_MODIFY_PICTURE) {
//                    DraweeController controller = Fresco.newDraweeControllerBuilder()
//                            .setUri(Uri.parse("file://" + imageModifiedPath))
//                            .setAutoPlayAnimations(true)
//                            .build();
//                    imageDetail.setController(controller);
//                }
//            }
//
//            String desc = mOriginalObject.item_list.get(1).desc;
//            etContent.setText(desc);
//        }
//
//        // 如果是改图，那么隐藏掉这个按钮
//        if (type == OriginalConsts.EDIT_TYPE_MODIFY_PICTURE) {
//            btnDelete.setVisibility(View.INVISIBLE);
//        }
//
//        RxView.clicks(btnDelete)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        showDeleteDialog();
//                    }
//                });
//    }
//
//    private void updateTitle() {
//        final TextView titleView = (TextView) findViewById(R.id.tv_titlebar_title);
//        titleView.setText("编辑帖子");
//        ImageView backImageView = (ImageView) findViewById(R.id.iv_titlebar_back);
//        RxView.clicks(backImageView)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        onBackPressed();
//                    }
//                });
//
//        TextView right = (TextView) findViewById(R.id.tv_titlebar_right);
//        right.setText("提交");
//        right.setVisibility(View.VISIBLE);
//        RxView.clicks(right)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        showLoadingDialog();
//                        startUploading();
//                    }
//                });
//    }
//
//    private void showDeleteDialog() {
//        AlertDialog dialog = new AlertDialog.Builder(DetailEditActivity.this)
//                .setMessage("是否确认删除？")
//                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        showLoadingDialog();
//                        sendDeleteRequest(mOriginalObject.tid);
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                }).create();
//        dialog.show();
//    }
//
//    private void showBackDialog() {
//        AlertDialog dialog = new AlertDialog.Builder(DetailEditActivity.this)
//                .setMessage("是否保存修改？")
//                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        showLoadingDialog();
//                        startUploading();
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                }).create();
//        dialog.show();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (type == OriginalConsts.EDIT_TYPE_MODIFY) {
//            showBackDialog();
//        } else {
//            finish();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    private void startUploading() {
//        if (type == OriginalConsts.EDIT_TYPE_MODIFY ){
//            sendEditRequest(mOriginalObject.tid, etTitle.getText().toString(), etContent.getText().toString(), null);
//        } else if (type == OriginalConsts.EDIT_TYPE_MODIFY_PICTURE) {
//            File file = new File(this.imageModifiedPath);
//            OriginalAgent.uploadImage(file, new JsonResponseHandler<UploadImageObject>() {
//                @Override
//                public void success(@NonNull UploadImageObject data) {
//                    onImageUploaded(data);
//                }
//            });
//        }
//    }
//
//    /**
//     * 提交完图片，提交编辑请求
//     * @param data data
//     */
//    private void onImageUploaded(@NonNull UploadImageObject data) {
//        ImageModifiedContentObject modified = new ImageModifiedContentObject();
//        modified.origin = this.origin;
//        modified.modified = data.img;
//        modified.overlays = DetailEditActivity.this.overlayObjects;
//
//        List<ImageModifiedContentObject> objs = Collections.singletonList(modified);
//        sendEditImageRequest(mOriginalObject.tid, etTitle.getText().toString(), etContent.getText().toString(), objs);
//    }
//
//    private void sendEditRequest(String tid, String title, String content, List<ImageNtsObject> pics) {
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme(URLDefine.SCHEME);
//        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
//        builder.encodedPath(URLDefine.TWEET_EDIT);
//
//        Map<String, String> params = new HashMap<>();
//        Map<String, String> postparams = new HashMap<>();
//        params.put("uid", AccountManager.getInstance().getUserId());
//        params.put("tid", tid);
//        params.put("title", title);
//        params.put("desc", content);
//        if (pics != null) {
//            postparams.put("pics", JSONArray.toJSONString(pics));
//        }
//
//        HttpUtil.newUrlEncodedPost(builder.build().toString(), params, postparams,
//                new SimpleJsonResponseHandler() {
//            @Override
//            public void success() {
//                LToast.showToast("修改成功。");
//                EventBus.getDefault().post(new OriginalEditEvent(mOriginalObject.tid));
//                finish();
//            }
//
//            @Override
//            public void end() {
//                super.end();
//                hideLoadingDialog();
//            }
//        });
//    }
//
//    private void sendEditImageRequest(String tid, String title, String content,
//                                      List<ImageModifiedContentObject> pics) {
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme(URLDefine.SCHEME);
//        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
//        builder.encodedPath(URLDefine.TWEET_INNO_PUB);
//
//        Map<String, String> params = new HashMap<>();
//        params.put("uid", AccountManager.getInstance().getUserId());
//        params.put("tid", tid);
//        params.put("title", title);
//        params.put("desc", content);
//        params.put("pics", JSONArray.toJSONString(pics));
//
//        HttpUtil.newUrlEncodedPost(builder.build().toString(), params, new SimpleJsonResponseHandler() {
//            @Override
//            public void success() {
//                LToast.showToast("发布成功。");
//                EventBus.getDefault().post(new OriginalPublishEvent(mOriginalObject.tid));
//                finish();
//            }
//
//            @Override
//            public void end() {
//                super.end();
//                hideLoadingDialog();
//            }
//        });
//    }
//
//    private void sendDeleteRequest(String tid) {
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme(URLDefine.SCHEME);
//        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
//        builder.encodedPath(URLDefine.TWEET_REMOVE);
//
//        Map<String, String> params = new HashMap<>();
//        params.put("uid", AccountManager.getInstance().getUserId());
//        params.put("tid", tid);
//
//        HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {
//            @Override
//            public void success() {
//                LToast.showToast("删除成功。");
//                EventBus.getDefault().post(new OriginalDeleteEvent(mOriginalObject));
//                finish();
//            }
//
//            @Override
//            public void end() {
//                super.end();
//                hideLoadingDialog();
//            }
//        });
//    }
}
