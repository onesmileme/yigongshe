package com.weikan.app.wenyouquan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.flexbox.FlexboxLayout;
import com.weikan.app.Constants;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.common.manager.FunctionConfig;
import com.weikan.app.face.FaceView;
import com.weikan.app.group.ChooseGroupActivity;
import com.weikan.app.group.adapter.GroupGridAdapter;
import com.weikan.app.group.bean.GroupDetailBean;
import com.weikan.app.group.bean.HotGroupListBean;
import com.weikan.app.original.bean.ImageNtsObject;
import com.weikan.app.original.bean.UploadImageObject;
import com.weikan.app.request.BackgroundTaskAgent;
import com.weikan.app.request.bean.TopicItem;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.Global;
import com.weikan.app.util.LToast;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.model.DataTransModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import platform.http.result.FailedResult;
import platform.photo.util.PhotoUtils;

/**
 * 发表文友圈帖子
 * Created by liujian on 16/6/28.
 */
public class WenyouPubActivity extends BaseActivity implements View.OnClickListener {

    public static final String PHOTO = "PHOTO";

    public static final String TYPE_NEW = "new";
    public static final String TYPE_FORWARD = "forward";

    //防止多次提交
    private boolean isPubing = false;

    ProgressDialog progressDialog;

    FlexboxLayout tagLayout;

    //输入框
    EditText editText;

    // 拍照控件
    ImageView[] ivPhotos = new ImageView[9];

    // 现存照片
    ArrayList<String> photos = new ArrayList<>();
    // 上传阿里图片服务后产生的
    Map<String, String> pathMap = new HashMap<>();
    UploadStatusManager uploadStatusManager = new UploadStatusManager();

    // 选择的话题id
    String selectTopic = "";
    //选择的group id
    String selectGroudId = "";
    // 话题列表
    List<TopicItem> topicList;

    private FaceView mFaceView;

    private boolean isSoftKeyboard = false;

    private GridView gridView;
    private GroupGridAdapter adapter;
    private List<GroupDetailBean> groupList = new ArrayList<>();
    private int selectPosition = -1;

    private enum UploadStatus {
        None, ING, Failed, Succeed
    }

    private class UploadStatusManager {
        Map<String, UploadStatus> uploadStatusMap = new HashMap<>();

        public UploadStatus get(String photo) {
            return uploadStatusMap.get(photo);
        }

        public void put(String photo, UploadStatus status) {
            uploadStatusMap.put(photo, status);

            // 只要有一个失败了，就通知失败
            if (status == UploadStatus.Failed) {
                onPhotoUploadFailed();
                return;
            }

            // 全部成功，才算通知成功
            boolean allSucceed = true;
            if (status == UploadStatus.Succeed) {
                for (UploadStatus s : uploadStatusMap.values()) {
                    if (s != UploadStatus.Succeed) {
                        allSucceed = false;
                        break;
                    }
                }
            } else {
                allSucceed = false;
            }

            if (allSucceed) {
                onAllPhotoUploadSucceed();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_dis);

        TextView titleText = (TextView) findViewById(R.id.tv_titlebar_title);
        titleText.setText("发表新帖");
        findViewById(R.id.iv_titlebar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                finish();
            }
        });
        gridView = (GridView) findViewById(R.id.gv_wenyou_pub_group);
        adapter = new GroupGridAdapter(this);
        adapter.setList(groupList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == groupList.size() - 1) {
                    hideSoftKeyboard();
                    Intent intent = new Intent(WenyouPubActivity.this, ChooseGroupActivity.class);
                    intent.putExtra(BundleParamKey.GROUPID, selectGroudId);
                    String gn = "";
                    for (GroupDetailBean groupDetailBean : groupList) {
                        if (groupDetailBean.groupId.equals(selectGroudId)) {
                            gn = groupDetailBean.groupName;
                            break;
                        }
                    }
                    intent.putExtra(BundleParamKey.GROUPNAME, gn);
                    startActivityForResult(intent, 100);
                } else {
                    if (selectGroudId.equals(groupList.get(position).groupId)) {
                        groupList.get(position).isSelected = 0;
                        selectGroudId = "";
                    } else {
                        if (selectPosition > -1) {
                            groupList.get(selectPosition).isSelected = 0;
                        }
                        selectPosition = position;
                        groupList.get(position).isSelected = 1;
                        selectGroudId = groupList.get(position).groupId;
                    }
                    adapter.notifyDataSetChanged();
                    selectTopic = "";
                    updateTopicView();
                }
            }
        });
        mFaceView = (FaceView) findViewById(R.id.wenyou_pub_faceview);
        findViewById(R.id.btn_face).setOnClickListener(this);
        findViewById(R.id.btn_pic_choose).setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("发布中");
        tagLayout = (FlexboxLayout) findViewById(R.id.fbl_wenyou_pub_tag);


        // 隐藏输入法
        editText = (EditText) findViewById(R.id.ed_pub_dis);
        mFaceView.setEditText(editText);
        editText.setOnClickListener(this);
        editText.postDelayed(new Runnable() {

            @Override
            public void run() {
                mFaceView.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                // 显示或者隐藏输入法
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
        final ScrollView sv = (ScrollView) findViewById(R.id.sv_pub_dis);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.getLineCount() > 6) {
                    sv.scrollTo(sv.getScrollX(), editText.getHeight() - editText.getLineHeight() * 6);
                }
            }
        });


        TextView pub = (TextView) findViewById(R.id.tv_titlebar_right);
        pub.setVisibility(View.VISIBLE);
        pub.setText("发布");
        pub.setOnClickListener(this);

        ivPhotos[0] = (ImageView) findViewById(R.id.iv_pub_dis_photo0);
        ivPhotos[1] = (ImageView) findViewById(R.id.iv_pub_dis_photo1);
        ivPhotos[2] = (ImageView) findViewById(R.id.iv_pub_dis_photo2);
        ivPhotos[3] = (ImageView) findViewById(R.id.iv_pub_dis_photo3);
        ivPhotos[4] = (ImageView) findViewById(R.id.iv_pub_dis_photo4);
        ivPhotos[5] = (ImageView) findViewById(R.id.iv_pub_dis_photo5);
        ivPhotos[6] = (ImageView) findViewById(R.id.iv_pub_dis_photo6);
        ivPhotos[7] = (ImageView) findViewById(R.id.iv_pub_dis_photo7);
        ivPhotos[8] = (ImageView) findViewById(R.id.iv_pub_dis_photo8);
        for (int i = 0; i < ivPhotos.length; i++) {
            final int finalI = i;
            ivPhotos[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPhotoClick(finalI);
                }
            });
        }

        topicList = BackgroundTaskAgent.getTopicData();

        initTopicView();
        initPubGroup();
        getSwipeBackLayout().addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {

            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
                hideSoftKeyboard();
            }

            @Override
            public void onScrollOverThreshold() {

            }
        });
    }

    /**
     * 底部话题选择界面
     */
    void initTopicView() {
        if (topicList != null && topicList.size() > 0) {
            for (int i = 0; i < topicList.size(); i++) {
                final TopicItem item = topicList.get(i);
                TextView tv = new TextView(this);
                tv.setText(item.name);
                tv.setTextColor(getResources().getColorStateList(R.color.selector_wenyou_pub_tag_text));
                tv.setBackgroundResource(R.drawable.wenyou_pub_tag_bg);
                if (selectTopic.equals(item.id)) {
                    tv.setSelected(true);
                } else {
                    tv.setSelected(false);
                }
                FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.leftMargin = Global.dpToPx(this, 8);
                lp.rightMargin = Global.dpToPx(this, 8);
                lp.topMargin = Global.dpToPx(this, 5);
                lp.bottomMargin = Global.dpToPx(this, 5);
                tagLayout.addView(tv, lp);

                final int finalI = i;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearGroup();
//                        String topic = "#" + topicList.get(finalI).name + " ";
                        if (view.isSelected()) {
                            selectTopic = "";
                            view.setSelected(false);
//                            if(editText.getText().toString().startsWith(topic)) {
//                                Editable ea = editText.getText().replace(0, topic.length(), "");
//                                editText.setText(ea);
//                                editText.setSelection(ea.length());
//                            }
                        } else {
                            selectTopic = item.id;
                            view.setSelected(true);
//                            Editable ea = editText.getText().insert(0, topic);
//                            editText.setText(ea);
//                            editText.setSelection(ea.length());
                        }
                        updateTopicView();
                    }
                });
            }
        }
    }

    void updateTopicView() {
        if (topicList != null) {
            for (int i = 0; i < topicList.size(); i++) {
                View v = tagLayout.getChildAt(i);
                TopicItem item = topicList.get(i);
                if (v != null && item != null) {
                    if (selectTopic.equals(item.id)) {
                        v.setSelected(true);
                    } else {
                        v.setSelected(false);
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard();
    }

    public void hideSoftKeyboard() {
        // 隐藏软键盘

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null && editText != null && imm.isActive(editText)) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        isSoftKeyboard = false;
    }


    private void sendRequest(String desc) {
        ArrayList<ImageNtsObject> pics = new ArrayList<>();
        for (String photo : photos) {
            pics.add(JSONObject.parseObject(pathMap.get(photo), ImageNtsObject.class));
        }
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.WENYOU_PUB);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put("desc", desc);
        if (pics.size() > 0) {
            params.put("pics", JSONArray.toJSONString(pics));
        }
        if (!TextUtils.isEmpty(selectTopic)) {
            params.put("topic_id", selectTopic);
        }
        if (!TextUtils.isEmpty(selectGroudId)) {
            params.put("group_id", selectGroudId);
        }
        params.put("desc", desc);
        HttpUtils.urlEncodedPost(builder.build().toString(), params, new SimpleJsonResponseHandler() {
            @Override
            public void begin() {
                super.begin();
                if (progressDialog != null && !progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }

            @Override
            public void success() {
                photos.clear(); // 清空所有临时的照片
                finish();
                hideSoftKeyboard();
                DataTransModel.needRefresh = true;
                setResult(RESULT_OK, null);
            }

            @Override
            public void end() {
                isPubing = false;
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshPhotos(); // onResume的时候刷新图片
    }

    private boolean isFristChange = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showSoftKeyboard();
            }
        }, 200);

        if (resultCode == 2000) {
            String groupId = data.getStringExtra(BundleParamKey.GROUPID);
            String groupName = data.getStringExtra(BundleParamKey.GROUPNAME);
            if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(groupName)) {
                clearGroup();
                adapter.notifyDataSetChanged();
                return;
            }
            boolean isHaveGroup = false;
            for (int i = 0; i < groupList.size(); i++) {
                GroupDetailBean groupDetailBean = groupList.get(i);
                if (!groupId.equals(groupDetailBean.groupId)) {
                    isHaveGroup = false;
                    groupDetailBean.isSelected = 0;
                } else {
                    if (selectPosition > -1) {
                        groupList.get(selectPosition).isSelected = 0;
                    }
                    isHaveGroup = true;
                    selectPosition = i;
                    groupDetailBean.isSelected = 1;
                    selectGroudId = groupId;
                    break;
                }
            }
            if (!isHaveGroup) {
                selectPosition = 0;
                selectGroudId = groupId;

                GroupDetailBean groupDetailBean = new GroupDetailBean();
                groupDetailBean.groupId = groupId;
                groupDetailBean.groupName = groupName;
                groupDetailBean.isSelected = 1;

                if (isFristChange) {
                    isFristChange = false;
                    groupList.add(0, groupDetailBean);
                    if (groupList.size() >= 4) {
                        groupList.remove(3);
                    }
                } else {
                    groupList.set(0, groupDetailBean);
                }
            } else {
                // 如果选择了群组，把话题取消
                selectTopic = "";
                updateTopicView();
            }
            adapter.notifyDataSetChanged();
            return;
        }
        PhotoUtils.processActivityResult(requestCode, resultCode, data, new PhotoUtils.ProcessListener() {
            @Override
            public void onResult(List<String> result) {
                photos.addAll(result);
                for (String p : result) {
                    uploadStatusManager.put(p, UploadStatus.ING);
                    sendUploadImgRequest(p);
                }
            }
        });

        refreshPhotos(); // onResume的时候刷新图片
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_titlebar_right:
                publish();
                break;

            case R.id.ed_pub_dis:
                if (!isSoftKeyboard) {
                    showSoftKeyboard();
                }
                break;
            case R.id.btn_face:
                if (mFaceView.getVisibility() == View.GONE) {
                    hideSoftKeyboard();
                    mFaceView.setVisibility(View.VISIBLE);
                } else {
                    showSoftKeyboard();
                    mFaceView.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_pic_choose:
                int index = 9 - photos.size();
                if (index > 0) {
                    PhotoUtils.gotoPending(this, 9 - photos.size(), false);
                } else {
                    LToast.showToast("最多只能选取9张图片。");
                }
                break;

        }
    }

    public void showSoftKeyboard() {
        isSoftKeyboard = true;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null && editText != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        mFaceView.setVisibility(View.GONE);
    }

    public void refreshPhotos() {
        for (int i = 0; i < ivPhotos.length; i++) {
            if (i < photos.size()) {
                ivPhotos[i].setClickable(true);
                loadPhoto(ivPhotos[i], i);
            } else if (i == photos.size()) {
//                ivPhotos[i].setClickable(true);
//                ivPhotos[i].setImageResource(R.drawable.pub_dis_image);

                ivPhotos[i].setClickable(false);
                ivPhotos[i].setImageBitmap(null);
            } else if (i > photos.size()) {
                ivPhotos[i].setClickable(false);
                ivPhotos[i].setImageBitmap(null);
            }
        }
    }

    private void loadPhoto(ImageView view, int index) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bmp = BitmapFactory.decodeFile(photos.get(index), options);
        view.setImageBitmap(bmp);
    }

    public void onPhotoClick(int index) {
        if (index >= photos.size()) {
            PhotoUtils.gotoPending(this, 9 - photos.size(), false);
        } else {
            showDeleteImageDialog(index);
        }
    }

    public void showDeleteImageDialog(final int index) {
        new AlertDialog.Builder(this)
                .setItems(
                        new String[]{"删除", "取消"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onDeleteImageDialogClick(index, which);
                            }
                        }
                )
                .show();
    }

    private void onDeleteImageDialogClick(final int index, final int which) {
        if (which == 0) {
            if (index >= 0 && index < photos.size()) {
                photos.remove(index);

                refreshPhotos();
            }
        }
    }


    public void sendUploadImgRequest(@NonNull String path) {
        File imageFile = new File(path);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.UPLOAD_TWEET_PIC);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());

        Map<String, File> fileParams = new HashMap<>();
        fileParams.put("file", imageFile);


        ImgHttpResponseHandler uploadImgHandler = new ImgHttpResponseHandler();
        uploadImgHandler.setOriginalPath(path);

        HttpUtils.multipartPost(builder.build().toString(), params, fileParams, uploadImgHandler);
    }


    private class ImgHttpResponseHandler extends JsonResponseHandler<UploadImageObject> {
        private String originalPath;

        @Override
        public void success(@NonNull UploadImageObject data) {
            String url = JSONObject.toJSONString(data.img);
            pathMap.put(originalPath, url);

            uploadStatusManager.put(originalPath, UploadStatus.Succeed);
        }

        @Override
        protected void failed(FailedResult r) {
            super.failed(r);
            r.setIsHandled(true);
            uploadStatusManager.put(originalPath, UploadStatus.Failed);
        }

        public void setOriginalPath(String originalPath) {
            this.originalPath = originalPath;
        }


    }


    @SuppressLint("StringFormatMatches")
    private void publish() {
        if (photos.size() == 0 && TextUtils.isEmpty(editText.getText().toString())) {
            LToast.showToast("请输入帖子内容");
            return;
        }

        if (editText.getText().toString().length() > Constants.pubContentMaxLength) {
            LToast.showToast(getResources().getString(R.string.wenyou_pub_beyond, Constants.pubContentMaxLength));
            return;
        }

        if (!isPubing) {
            isPubing = true;
            progressDialog.show();

            // 找到状态为None和Failed的图片
            // 上传这些图片
            boolean allSucceed = true;
            for (String p : photos) {
                UploadStatus s = uploadStatusManager.get(p);

                if (s == UploadStatus.None || s == UploadStatus.Failed) {
                    allSucceed = false;
                    uploadStatusManager.put(p, UploadStatus.ING);
                    sendUploadImgRequest(p);
                } else if (s == UploadStatus.ING) {
                    allSucceed = false;
                }
            }


            if (allSucceed) {
                // 如果所有图片都传了，那么这里可以提交了
                sendRequest(editText.getText().toString());
            }
        }
    }

    private void onPhotoUploadFailed() {
        if (isPubing) {
            isPubing = false;
            LToast.showToast("发布失败，请重试");
            progressDialog.dismiss();
        }
    }

    private void onAllPhotoUploadSucceed() {
        if (isPubing) {
            sendRequest(editText.getText().toString());
        }
    }

    private void initPubGroup() {
        if (!FunctionConfig.getInstance().isSupportWenyouGroup()) {
            return;
        }
//        String groupId = getIntent().getStringExtra(BundleParamKey.GROUPID);
//        String groupName =  getIntent().getStringExtra(BundleParamKey.GROUPNAME);
//        if(!TextUtils.isEmpty(groupId) && !TextUtils.isEmpty(groupName)){
//            selectGroudId = groupId;
//            GroupDetailBean groupDetailBean = new GroupDetailBean();
//            groupDetailBean.groupId = groupId;
//            groupDetailBean.groupName = groupName;
//            groupDetailBean.isSelected = 1;
//            groupList.add(groupDetailBean);
//        }
        getHotGroup();
    }

    private void getHotGroup() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.HOT_GROUPS);
        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        HttpUtils.get(builder.build().toString(), params, new platform.http.responsehandler.AmbJsonResponseHandler<HotGroupListBean>() {
            @Override
            public void success(@Nullable HotGroupListBean data) {
                if (data == null || data.groups == null || data.groups.size() == 0) {
                    ifFromDetail();
                    if (groupList.size() != 0) {
                        gridView.setVisibility(View.VISIBLE);
                    } else {
                        gridView.setVisibility(View.GONE);
                    }
                } else {
                    String groupId = getIntent().getStringExtra(BundleParamKey.GROUPID);
                    String groupName = getIntent().getStringExtra(BundleParamKey.GROUPNAME);
                    if (!TextUtils.isEmpty(groupId) && !TextUtils.isEmpty(groupName)) {
                        boolean isHaveFromDetail = false;
                        for (int i = 0; i < data.groups.size(); i++) {
                            GroupDetailBean detailBean = data.groups.get(i);
                            if (detailBean.groupId.equals(groupId)) {
                                isHaveFromDetail = true;
                                selectPosition = i;
                                selectGroudId = detailBean.groupId;
                                detailBean.isSelected = 1;
                            }
                        }
                        if (!isHaveFromDetail) {
                            ifFromDetail();
                        }
                    }
                    final int max = data.groups.size() > 3 - groupList.size() ? 3 - groupList.size() : data.groups.size();
                    for (int i = 0; i < max; i++) {
                        groupList.add(data.groups.get(i));
                    }
                }
                if (groupList.size() != 0) {
                    gridView.setVisibility(View.VISIBLE);
                    GroupDetailBean groupDetailBean = new GroupDetailBean();
                    groupList.add(groupDetailBean);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
                ifFromDetail();
                if (groupList.size() != 0) {
                    gridView.setVisibility(View.VISIBLE);
                } else {
                    gridView.setVisibility(View.GONE);
                }
                if (groupList.size() != 0) {
                    GroupDetailBean groupDetailBean = new GroupDetailBean();
                    groupList.add(groupDetailBean);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void clearGroup() {
        selectGroudId = "";
        selectPosition = -1;
        for (GroupDetailBean bean : groupList) {
            bean.isSelected = 0;
        }
        adapter.notifyDataSetChanged();
    }

    private void ifFromDetail() {
        String groupId = getIntent().getStringExtra(BundleParamKey.GROUPID);
        String groupName = getIntent().getStringExtra(BundleParamKey.GROUPNAME);
        if (!TextUtils.isEmpty(groupId) && !TextUtils.isEmpty(groupName)) {
            selectGroudId = groupId;
            selectPosition = 0;
            GroupDetailBean groupDetailBean = new GroupDetailBean();
            groupDetailBean.groupId = groupId;
            groupDetailBean.groupName = groupName;
            groupDetailBean.isSelected = 1;
            groupList.add(groupDetailBean);
            isFristChange = false;
        }
    }
}
