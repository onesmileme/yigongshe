package com.weikan.app.original;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.weikan.app.Constants;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.original.adapter.MaterialPicturesAdapter;
import com.weikan.app.original.bean.*;
import com.weikan.app.original.event.OriginalDeleteEvent;
import com.weikan.app.original.event.OriginalEditEvent;
import com.weikan.app.original.event.OriginalPublishEvent;
import com.weikan.app.original.utils.PathUtils;
import com.weikan.app.original.widget.*;
import com.weikan.app.util.URLDefine;
import de.greenrobot.event.EventBus;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片编辑页面
 *
 * @author kailun on 16/4/4
 */
public class ImageEditActivity extends BaseActivity implements PictureOperationView.PendingInputTextPanelListener {

    ImageView ivBack;
    TextView tvConfirm;

    PictureOperationView vOperation;
    ImageView ivContainer;

    RecyclerView rvPics;

    TextView tvText;
    TextView tvPhoto;

    View vCover;
    InputTextPanelView inputTextPanelView;

    MaterialPicturesAdapter adapter;

    OriginalDetailItem detailItem;

    // 原始的底图
    ImageNtsObject origin = null;
    ImageModifiedContentObject modifiedContent = null;
    private Bitmap oriBitmap;
    double oriScale = 1.0;

    // 首次onWindowFocusChanged取得焦点前为true，否则为false
    boolean isFirstFocused = true;

    // 一定要强引用，否则会被gc
    Target imageLoadTarget = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        initViews();
        parseIntent();
        if (detailItem == null) {
            finish();
        }
        requestEditDetailObject();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && isFirstFocused) {
            isFirstFocused = false;
            if (origin != null) {
                Log.e(getClass().getSimpleName(), "onWindowFocusChanged: loadImage");
                loadImage();
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("放弃改图吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(OriginalPublishEvent event) {
        finish();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(OriginalEditEvent event) {
        finish();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(OriginalDeleteEvent event) {
        finish();
    }

    @Override
    public boolean isSupportSwipBack() {
        return false;
    }

    @Override
    public void pendingInputTextPanel() {
        AbstractOverlayView overlayView = vOperation.getCurrentOverlayView();
        if (overlayView == null) {
            return;
        }

        if (overlayView instanceof TextOverlayView) {
            showInputTextPanel((TextOverlayView) overlayView);
        }
    }

    private void initViews() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmClick();
            }
        });

        vOperation = (PictureOperationView) findViewById(R.id.fl_container);
        vOperation.setOnInputTextPanelPopListener(this);
        ivContainer = (ImageView) findViewById(R.id.iv_container);

        rvPics = (RecyclerView) findViewById(R.id.rv_pics);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPics.setLayoutManager(linearLayoutManager);

        adapter = new MaterialPicturesAdapter();
        adapter.setOnMaterialPictureClickListener(new MaterialPicturesAdapter.MaterialPictureViewHolder.OnClickListener() {
            @Override
            public void onClick(MaterialPicturesAdapter.MaterialPictureViewHolder viewHolder) {
                onMaterialPictureClick(viewHolder);
            }
        });
        rvPics.setAdapter(adapter);

        tvText = (TextView) findViewById(R.id.tv_text);
        tvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTextButtonClick();
            }
        });

        tvPhoto = (TextView) findViewById(R.id.tv_photo);
        tvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhotoButtonClick();
            }
        });

        vCover = findViewById(R.id.v_cover);
        vCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInputTextPanel();
            }
        });
        inputTextPanelView = (InputTextPanelView) findViewById(R.id.input_text_panel);
        inputTextPanelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTextPanelView.getEditText().requestFocus();
            }
        });
        inputTextPanelView.getButtonConfirm().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInputTextPanelConfirm();
            }
        });
    }

    private void parseIntent() {
        Intent intent = getIntent();
        detailItem = (OriginalDetailItem)
                intent.getSerializableExtra(OriginalConsts.BUNDLE_DETAIL_OBJECT);
    }

    public void loadImage() {

        ImageObject n = origin.n;

        // 通过图片宽高比，确定最后flContainer的尺寸
        final int width = n.w;
        final int height = n.h;

        int vWidth = vOperation.getWidth();
        int vHeight = vOperation.getHeight();

        double imageW2h = (double)width / height;
        double viewW2h = (double)vWidth / vHeight;

        if (imageW2h > viewW2h) { // 图片宽高比比View大，那么上下留空
            vHeight = (int) Math.round(vWidth / imageW2h);
        } else {
            vWidth = (int) Math.round(vHeight * imageW2h);
        }

        this.oriScale = (double) vWidth / width;

        ViewGroup.LayoutParams lp = vOperation.getLayoutParams();
        lp.width = vWidth;
        lp.height = vHeight;
        vOperation.setLayoutParams(lp);

        // 加载图片
        this.imageLoadTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ImageEditActivity.this.oriBitmap = bitmap;
                ivContainer.setImageBitmap(bitmap);
                Log.e(ImageEditActivity.class.getSimpleName(), "onBitmapLoaded");

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(ImageEditActivity.class.getSimpleName(), "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.e(ImageEditActivity.class.getSimpleName(), "onPrepareLoad");

            }
        };

        Log.e(ImageEditActivity.class.getSimpleName(), "url: " + n.url);

        Picasso.with(this)
                .load(Uri.parse(n.url))
                .into(this.imageLoadTarget);

        if (modifiedContent != null) {
            // 添加所有Overlay
            AbstractOverlayView lastOverlayView = null;
            for (OverlayObject oObj: modifiedContent.overlays) {
                AbstractOverlayView v = makeOverlay(oObj);
                if (v != null) {
                    lastOverlayView = v;
                }
            }

            if (lastOverlayView != null) {
                vOperation.setCurrentOverlayView(lastOverlayView);
            }
        }
    }

    private void onTextButtonClick() {
        addTextOverlay();
    }

    private void onPhotoButtonClick() {
        if (rvPics.getVisibility() == View.VISIBLE) {
            rvPics.setVisibility(View.GONE);
        } else {
            rvPics.setVisibility(View.VISIBLE);
            requestMaterialList();
        }
    }

    private void onMaterialPictureClick(MaterialPicturesAdapter.MaterialPictureViewHolder viewHolder) {
        Bitmap bitmap = viewHolder.getBitmap();
        ImageNtsObject item = viewHolder.getItem();
        if (bitmap == null) {
            return; // 图片还没加载好...
        }

        addImageOverlay(item);
    }

    private void onConfirmClick() {
        Pair<String, ArrayList<OverlayObject>> result = make();
        String outputPath = result.first;
        ArrayList<OverlayObject> overlays = result.second;

        Intent intent = new Intent(this, DetailEditActivity.class);
        intent.putExtra(Constants.DETAIL_OBJECT, detailItem);
        intent.putExtra(OriginalConsts.BUNDLE_EDIT_TYPE, OriginalConsts.EDIT_TYPE_MODIFY_PICTURE);
        intent.putExtra(OriginalConsts.BUNDLE_ORIGIN, origin);
        intent.putExtra(OriginalConsts.BUNDLE_MODIFIED_FILE, outputPath);
        intent.putExtra(OriginalConsts.BUNDLE_OVERLAYS, overlays);
        startActivity(intent);
    }

    private void onInputTextPanelConfirm() {
        String content = inputTextPanelView.getEditText().getText().toString();
        String colorText = inputTextPanelView.getColorText();
        hideInputTextPanel();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "必须输入至少一个文字", Toast.LENGTH_SHORT).show();
            return;
        }

        TextOverlayView overlayView = (TextOverlayView) vOperation.getCurrentOverlayView();
        if (overlayView == null) {
            return;
        }

        OverlayObject obj = overlayView.makeOverlayObject();
        if (obj.text != null) {
            obj.text.content = content;
            obj.text.color = colorText;
        }
        overlayView.initWithOverlayObject(obj);
    }

    private void showInputTextPanel(TextOverlayView overlayView) {
        rvPics.setVisibility(View.GONE); // 记得先把图片的Layout隐藏了

        vCover.setVisibility(View.VISIBLE);
        inputTextPanelView.setVisibility(View.VISIBLE);

        EditText et = inputTextPanelView.getEditText();
        et.setText(overlayView.getContent());
        et.setSelection(0, overlayView.getContent().length());
        et.requestFocus();

        String colorText = overlayView.getColorText();
        inputTextPanelView.setColorText(colorText);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_FORCED);
    }


    private void hideInputTextPanel() {
        vCover.setVisibility(View.GONE);
        inputTextPanelView.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputTextPanelView.getEditText().getWindowToken(), 0);
    }

    /**
     * 保存Bitmap图片到指定文件
     *
     * @param bm bm
     * @param filePath filePath
     */
    public static void saveBitmap(Bitmap bm, String filePath) {
        File f = new File(filePath);
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTextOverlay() {
        if (origin == null || origin.n == null) {
            return;
        }

        ImageObject n = origin.n;

        TextOverlayView overlayView = new TextOverlayView(this);
        overlayView.setOriScala(oriScale);

        OverlayObject obj = new OverlayObject();
        obj.type = AbstractOverlayView.TYPE_TEXT;

        OverlayObject.Text text = new OverlayObject.Text();
        text.content = "请输入"; // "任何不超过一行的文字";
        text.size = 60;
        text.color = "#ffffff";
        obj.text = text;

        obj.picture = null;

        double density = getResources().getDisplayMetrics().density;

        double resize = density * 0.5 / oriScale;

        // 缺省在左上角的代码，先注掉
//        Rect rect = TextOverlayView.calTextRect(text);
//        obj.x = rect.width() * resize / 2.0 // rect是屏幕坐标
//                + AbstractOverlayView.PADDING * density / oriScale;
//        obj.y = rect.height() * resize / 2.0
//                + AbstractOverlayView.PADDING * density / oriScale;

        // 缺省在屏幕中间的代码
        obj.x = n.w / 2.0;
        obj.y = n.h / 2.0;

        obj.resize = resize; // 所有文字缺省需要乘以0.5
        obj.rotation = 0;

        vOperation.addOverlayView(overlayView);
        overlayView.initWithOverlayObject(obj);

        vOperation.setCurrentOverlayView(overlayView);
    }

    private void addImageOverlay(ImageNtsObject item) {
        if (origin == null || origin.n == null) {
            return;
        }
        ImageObject n = origin.n;

        PictureOverlayView overlayView = new PictureOverlayView(this);
        overlayView.setOriScala(oriScale);

        OverlayObject obj = new OverlayObject();
        obj.type = AbstractOverlayView.TYPE_PICTURE;

        obj.text = null;

        OverlayObject.Picture picture = new OverlayObject.Picture();
        picture.n = item.n;
        obj.picture = picture;

        double density = getResources().getDisplayMetrics().density;

        double resize = 1.5 / oriScale;

        // 缺省在左上角的代码，先注掉
//        obj.x = img.w * resize / 2.0
//                + AbstractOverlayView.PADDING * density / oriScale;
//        obj.y = img.h * resize / 2.0
//                + AbstractOverlayView.PADDING * density / oriScale;

        // 缺省在屏幕中间的代码
        obj.x = n.w / 2.0;
        obj.y = n.h / 2.0;

        obj.resize = resize;
        obj.rotation = 0;

        vOperation.addOverlayView(overlayView);
        overlayView.initWithOverlayObject(obj);

        vOperation.setCurrentOverlayView(overlayView);

    }

    private AbstractOverlayView makeOverlay(OverlayObject obj) {
        AbstractOverlayView overlayView = null;
        if (TextUtils.equals(obj.type, AbstractOverlayView.TYPE_PICTURE)) {
            overlayView = new PictureOverlayView(this);
        } else if (TextUtils.equals(obj.type, AbstractOverlayView.TYPE_TEXT)) {
            overlayView = new TextOverlayView(this);
        } else {
            return null;
        }

        vOperation.addOverlayView(overlayView);
        overlayView.setOriScala(oriScale);
        overlayView.initWithOverlayObject(obj);

        return overlayView;
    }

    private Pair<String, ArrayList<OverlayObject>> make() {
        // 加载图片前先读取尺寸
        Bitmap bgCopy = oriBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(bgCopy);

        // 所有Overlay对象
        ArrayList<OverlayObject> overlays = new ArrayList<>();
        for (AbstractOverlayView v: vOperation.getOverlayViews()) {
            Matrix mv = new Matrix();

            OverlayObject o = v.makeOverlayObject();
            overlays.add(o);

            // 先平移
            Bitmap bitmap = v.getBitmap();
            double dx = o.x - bitmap.getWidth() / 2; // 注意，这里不需要乘上scala
            double dy = o.y - bitmap.getHeight() / 2; // 而应当用原始大小，后面统一缩放
            mv.postTranslate((float) dx, (float) dy);

            // 以中心点为基准缩放
            mv.postScale((float) o.resize, (float) o.resize, (float) o.x, (float) o.y);

            // 以中心点为基准旋转
            double rotation = o.rotation * 180.0 / Math.PI;
            mv.postRotate((float) rotation, (float) o.x, (float) o.y);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setDither(true);
            canvas.drawBitmap(v.getBitmap(), mv, paint);
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        String outPath = PathUtils.confirmTmpJpegPath();
        saveBitmap(bgCopy, outPath);

        return new Pair<>(outPath, overlays);
    }

    private void requestEditDetailObject() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.TWEET_TWEET_CONTENT);

        Map<String, String> params = new HashMap<>();
        params.put("tid", detailItem.tid);
        params.put("uid", AccountManager.getInstance().getUserId());

        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<ImageEditObject>() {

            @Override
            public void success(@NonNull ImageEditObject data) {
                ImageNtsWithModifiedObject obj = data.content.get(0);

                // 区分首次次编辑 和 二次编辑
                if (obj.modifiedContent == null) {
                    final ImageNtsObject origin = new ImageNtsObject();
                    origin.n = obj.n;
                    origin.s = obj.s;
                    origin.t = obj.t;
                    ImageEditActivity.this.origin = origin;
                    ImageEditActivity.this.modifiedContent = null;

                    if (!isFirstFocused) {
                        Log.e(getClass().getSimpleName(), "newGet: loadImage");
                        loadImage();
                    }

                } else {
                    final ImageModifiedContentObject contentObj =  obj.modifiedContent;
                    ImageEditActivity.this.origin = obj.modifiedContent.origin;
                    ImageEditActivity.this.modifiedContent = contentObj;

                    if (!isFirstFocused) {
                        loadImage();
                    }
                }
            }
        });
    }

    private void requestMaterialList() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.MATERIAL_LISTS);

        Map<String, String> params = new HashMap<>();


        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<MaterialListObject>() {

            @Override
            public void success(@NonNull MaterialListObject data) {
                if(data.content == null) {
                    return;
                }

                List<ImageNtsObject> chartlet = data.content.chartlet;
                adapter.setItems(chartlet);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
