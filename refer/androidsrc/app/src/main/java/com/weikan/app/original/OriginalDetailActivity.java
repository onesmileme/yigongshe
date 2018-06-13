package com.weikan.app.original;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.BuildConfig;
import com.weikan.app.Constants;
import com.weikan.app.MainApplication;
import com.weikan.app.R;
import com.weikan.app.ShareActivity;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.face.FaceView;
import com.weikan.app.original.adapter.OriginalCommentAdapter;
import com.weikan.app.original.adapter.TweetInnoListAdapter;
import com.weikan.app.original.bean.CollectionEvent;
import com.weikan.app.original.bean.OriginalCommentObject;
import com.weikan.app.original.bean.OriginalDetailData;
import com.weikan.app.original.bean.OriginalDetailItem;
import com.weikan.app.original.bean.OriginalListData;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.original.bean.TweetRelObject;
import com.weikan.app.original.event.OriginalDeleteEvent;
import com.weikan.app.original.event.OriginalEditEvent;
import com.weikan.app.original.event.OriginalPublishEvent;
import com.weikan.app.original.widget.AbsDetailHeaderProvider;
import com.weikan.app.original.widget.PartitionDetailHeaderProvider;
import com.weikan.app.original.widget.WebviewDetailHeaderProvider;
import com.weikan.app.personalcenter.UserHomeActivity;
import com.weikan.app.util.DateUtils;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.LToast;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.bean.WenyouListData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 原创详情页
 */
public class OriginalDetailActivity extends BaseActivity implements View.OnClickListener {
    private View mListHeadView;

    private ListView mListView;
    private PullToRefreshListView mPullRefreshListView;
    private EditText mCommentEditText;
    private RelativeLayout rlPics;

    private RecyclerView rvPics;
    private View vPicsDivider;
    private TweetInnoListAdapter picsAdapter;


    private OriginalCommentAdapter mOriginalCommentAdapter;

    private String tid;
    private boolean justweb;

    private TextView mBottomCommentTextView;
    private ImageView mBottomPraiseImageView;
    private ImageView mBottomShareImageView;

    private RelativeLayout mPraise;
    private View mPraiseDivider;

    private View headerInnerView;
    private AbsDetailHeaderProvider headerProvider;


    private RelativeLayout mRootView;

    private LinearLayout mCommentBottomView;
    private EditText mCommentBottomEditText;
    private ImageView mCommentEmojiImageView;
    private TextView mCommentSendTextView;
    private RelativeLayout mMixedBottomView;

    private FaceView mFaceView;

    private ArrayList<String> praiseUserNameList = new ArrayList<>();

    private ArrayList<WenyouListData.CommentItem> originalCommentObjectList = new ArrayList<WenyouListData.CommentItem>();

    public static final String TYPE_NEW = "new";
    public static final String TYPE_APPEND = "append";
    public static final String TYPE_NEXT = "next";

    // 详情页实体
    private OriginalDetailItem mOriginalObject;
    // 列表页传入的对应实体
    private OriginalItem mOriginalListObject;

    private boolean mIsEmojiShow = false;


    private TextView commentNumTextView;

    private boolean mIsAdmired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_original_detail);
        mCommentBottomView = (LinearLayout) findViewById(R.id.original_detail_bottom_comment_relativelayout);
        mMixedBottomView = (RelativeLayout) findViewById(R.id.original_detail_bottom_mixed_relativelayout);

        mCommentBottomEditText = (EditText) findViewById(R.id.original_detail_bottom_comment_edittext);
        mCommentSendTextView = (TextView) findViewById(R.id.original_detail_bottom_comment_send);
        mCommentEmojiImageView = (ImageView) findViewById(R.id.original_detail_bottom_comment_emoji);

        View touchView = findViewById(R.id.detail_none);
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCommentBottomView.getVisibility() == View.VISIBLE) {
                    mCommentBottomView.setVisibility(View.GONE);
                    mMixedBottomView.setVisibility(View.VISIBLE);
                    mFaceView.setVisibility(View.GONE);
                    closeInputMethod();
                    clearReplyInfo();
                }
                return false;
            }
        });

        mFaceView = (FaceView) findViewById(R.id.original_detail_faceview);
        mFaceView.setEditText(mCommentBottomEditText);

        mCommentEmojiImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsEmojiShow) {
                    if (mFaceView != null) {
                        mFaceView.setVisibility(View.VISIBLE);
                    }
                    mIsEmojiShow = true;
                    closeInputMethod();
                } else {
                    if (mFaceView != null) {
                        mFaceView.setVisibility(View.GONE);
                    }
                    mIsEmojiShow = false;
                }
            }
        });

        mCommentBottomEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFaceView.setVisibility(View.GONE);
                showInputMethod();
            }
        });

        RxView.clicks(mCommentSendTextView)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!AccountManager.getInstance().isLogin()) {
                            AccountManager.getInstance().gotoLogin(OriginalDetailActivity.this);
                            return;
                        }
                        String content = mCommentBottomEditText.getText().toString().trim();
                        if (TextUtils.isEmpty(content)) {
                            Toast.makeText(OriginalDetailActivity.this, "评论不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (content.length() > Constants.commentMaxLength) {
                            LToast.showToast(MainApplication.getInstance().getResources().getString(R.string.comment_pub_beyond, Constants.commentMaxLength));
                            return;
                        }
                        String replyCid = "";
                        String replyUid = "";
                        if (mCommentBottomEditText.getTag() != null) {
                            OriginalCommentObject commentObject = (OriginalCommentObject) mCommentBottomEditText.getTag();
                            replyCid = commentObject.cid + "";
                            replyUid = commentObject.uid;
                        }
                        toComment(content, tid, replyCid, replyUid);

                        // 用来返回列表页同步数据用
                        Intent intent = new Intent();
                        intent.putExtra("detailobject", mOriginalObject);
                        setResult(RESULT_OK, intent);
                    }
                });


        mRootView = (RelativeLayout) findViewById(R.id.original_detail_root_layout);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                if (isShowInput()) {
                    //大小超过100时，一般为显示虚拟键盘事件
                    mCommentBottomView.setVisibility(View.VISIBLE);
                    mCommentBottomEditText.requestFocus();
                    mMixedBottomView.setVisibility(View.GONE);
                } else {
                    //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
                    if (!mIsEmojiShow) {
                        mCommentBottomView.setVisibility(View.GONE);
                        mMixedBottomView.setVisibility(View.VISIBLE);
                        mFaceView.setVisibility(View.GONE);
                    }
                }
            }
        });


        updateTitle();

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.original_detail_listview);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = android.text.format.DateUtils.formatDateTime(OriginalDetailActivity.this,
                        System.currentTimeMillis(),
                        android.text.format.DateUtils.FORMAT_SHOW_TIME
                                | android.text.format.DateUtils.FORMAT_SHOW_DATE
                                | android.text.format.DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                getOriginalDetail(tid);
                sendNewRequest(tid);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = android.text.format.DateUtils.formatDateTime(OriginalDetailActivity.this,
                        System.currentTimeMillis(),
                        android.text.format.DateUtils.FORMAT_SHOW_TIME
                                | android.text.format.DateUtils.FORMAT_SHOW_DATE
                                | android.text.format.DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                if (originalCommentObjectList != null && originalCommentObjectList.size() > 0) {
                    sendNextRequest(originalCommentObjectList.get(originalCommentObjectList.size() - 1).cid, tid);
                } else {
                    sendNewRequest(tid);
                }
            }
        });

        mListView = mPullRefreshListView.getRefreshableView();
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mListHeadView = getLayoutInflater().inflate(R.layout.original_detail_header, null);

        rlPics = (RelativeLayout) mListHeadView.findViewById(R.id.rl_pics);
        rvPics = (RecyclerView) mListHeadView.findViewById(R.id.rv_pics);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPics.setLayoutManager(linearLayoutManager);

        picsAdapter = new TweetInnoListAdapter();
        picsAdapter.setOnItemClickListener(new TweetInnoListAdapter.InnerViewHolder.OnClickListener() {
            @Override
            public void onClick(TweetInnoListAdapter.InnerViewHolder viewHolder) {
                onPicsItemClick(viewHolder);
            }
        });
        rvPics.setAdapter(picsAdapter);

        vPicsDivider = mListHeadView.findViewById(R.id.original_pics_divider);

        mOriginalCommentAdapter = new OriginalCommentAdapter(OriginalDetailActivity.this);
        mOriginalCommentAdapter.setCommentList(originalCommentObjectList);
        mOriginalCommentAdapter.setTid(tid);
        mListView.setAdapter(mOriginalCommentAdapter);
        mListView.addHeaderView(mListHeadView);

        // 评论列表的点击事件
//        RxAdapterView.itemClicks(mListView)
//                .map(new Func1<Integer, OriginalCommentObject>() {
//                    @Override
//                    public OriginalCommentObject call(Integer integer) {
//                        int headerCount = mListView.getHeaderViewsCount();
//                        int pos = integer  - headerCount;
//                        if (pos >= 0 && pos < mOriginalCommentAdapter.getCount()) {
//                            OriginalCommentObject commentObject = (OriginalCommentObject) mOriginalCommentAdapter.getItem(pos);
//                            return commentObject;
//                        } else {
//                            return null;
//                        }
//                    }
//                })
//                .subscribe(new Action1<OriginalCommentObject>() {
//                    @Override
//                    public void call(OriginalCommentObject commentObject) {
//                        if (commentObject != null) {
//                            if (!commentObject.uid.equals(AccountManager.getInstance().getUserId())) {
//                                String name = commentObject.getSname() != null ? commentObject.getSname() : "";
//                                mCommentBottomEditText.setHint("回复 " + name + ":");
//                                mCommentBottomEditText.setTag(commentObject);
//                                mCommentEditText.requestFocus();
//                                showInputMethod();
//                            }
//                        }
//                    }
//                });

        mPraise = (RelativeLayout) mListHeadView.findViewById(R.id.original_detail_praise);
        mPraiseDivider = mListHeadView.findViewById(R.id.original_praise_divider);

        commentNumTextView = (TextView) mListHeadView.findViewById(R.id.original_detail_comment_num);

        mCommentEditText = (EditText) findViewById(R.id.original_detail_comment_edittext);

        mBottomCommentTextView = (TextView) findViewById(R.id.original_detail_bottom_comment_textview);
        mBottomCommentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.setSelection(2);
            }
        });
        mBottomPraiseImageView = (ImageView) findViewById(R.id.original_detail_bottom_praise_imageview);
        mBottomPraiseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AccountManager.getInstance().isLogin()) {
                    AccountManager.getInstance().gotoLogin(OriginalDetailActivity.this);
                    return;
                }

                if (mOriginalObject.praise != null && mOriginalObject.praise.flag) {
                    toCancelPraise(tid);
                } else {
                    toAddPraise(tid);
                }
            }
        });
        mBottomShareImageView = (ImageView) findViewById(R.id.original_detail_bottom_share_imageview);
        mBottomShareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOriginalObject == null || mOriginalObject.forward == null) {
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(OriginalDetailActivity.this, ShareActivity.class);
                intent.putExtra("content", mOriginalObject.share_content);
                intent.putExtra("url", mOriginalObject.forward.url);
                intent.putExtra("imgurl", mOriginalObject.sharePic != null && mOriginalObject.sharePic.t != null ? mOriginalObject.sharePic.t.url : "");
                intent.putExtra("tid", tid);
                intent.putExtra("isCollect", mOriginalObject.collection.flag);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("tid")) {
                tid = intent.getStringExtra("tid");
                mOriginalCommentAdapter.setTid(tid);
            }
            if (intent.hasExtra("justweb")) {
                justweb = intent.getBooleanExtra("justweb", false);

            }
            if (intent.hasExtra("detailitem")) {
                mOriginalListObject = (OriginalItem) intent.getSerializableExtra("detailitem");
            }
        }

        showLoadingDialog();
        getOriginalDetail(tid);

        if (justweb) {
            mListHeadView.findViewById(R.id.rl_ori_detail_head_user).setVisibility(View.GONE);
            mListHeadView.findViewById(R.id.original_detail_praise).setVisibility(View.GONE);
            mListHeadView.findViewById(R.id.ll_ori_detail_head_pinglun).setVisibility(View.GONE);
            findViewById(R.id.original_detail_bottom_relativelayout).setVisibility(View.GONE);
            TextView titleView = (TextView) findViewById(R.id.tv_titlebar_title);
            titleView.setText("使用说明");
        }

        EventBus.getDefault().register(this);
    }

    private void onPicsItemClick(TweetInnoListAdapter.InnerViewHolder viewHolder) {
        TweetRelObject item = viewHolder.getItem();
        if (item != null) {
            Intent intent = new Intent(this, OriginalDetailActivity.class);
            intent.putExtra("tid", item.tid);
            intent.putExtra("justweb", false);
//            intent.putExtra("detailitem", item);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_titlebar_back:
                closeInputMethod();
                Intent intent = new Intent();
                if (mOriginalObject != null && mOriginalObject.collection != null) {
                    intent.putExtra("isCollect", mOriginalObject.collection.flag);
                }
                setResult(200, intent);
                finish();
                break;
            default:
                break;
        }
    }

    private void updateTitle() {
        TextView titleView = (TextView) findViewById(R.id.tv_titlebar_title);
        titleView.setText("详情");
        ImageView backImageView = (ImageView) findViewById(R.id.iv_titlebar_back);
        backImageView.setOnClickListener(this);
        ImageView shareView = (ImageView) findViewById(R.id.iv_titlebar_right);
        shareView.setImageResource(R.drawable.dis_item_share);
        if (!TextUtils.isEmpty(BuildConfig.WECHAT_ID)) {
            shareView.setVisibility(View.VISIBLE);
            shareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOriginalObject == null || mOriginalObject.share == null) {
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setClass(OriginalDetailActivity.this, ShareActivity.class);
                    intent.putExtra("title", mOriginalObject.share.title);
                    intent.putExtra("content", mOriginalObject.share.desc);
                    intent.putExtra("url", mOriginalObject.share.url);
                    intent.putExtra("imgurl", mOriginalObject.share.icon != null ? mOriginalObject.share.icon : "");
                    intent.putExtra("tid", tid);
                    intent.putExtra("isCollect", mOriginalObject.collection.flag);
                    intent.putExtra("shareWenyouType", ShareActivity.SHARE_WENYOU_FOR_NEWS);
                    startActivity(intent);
                }
            });
        }
    }

    private void updateTitleEdit(OriginalDetailItem object) {
        if (!TextUtils.isEmpty(object.template_type)
                && object.template_type.equals(OriginalDetailItem.TemplateType.multi.name())
                && object.authorid != null
                && object.authorid.equals(AccountManager.getInstance().getUserId())) {

            TextView right = (TextView) findViewById(R.id.tv_titlebar_right);
            right.setText("编辑");
            right.setVisibility(View.VISIBLE);
            RxView.clicks(right)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            Intent intent = new Intent();
                            intent.setClass(OriginalDetailActivity.this, DetailEditActivity.class);
                            intent.putExtra(Constants.DETAIL_OBJECT, mOriginalObject);
                            intent.putExtra(OriginalConsts.BUNDLE_EDIT_TYPE, OriginalConsts.EDIT_TYPE_MODIFY);
                            startActivityForResult(intent, Constants.ARTICLE_EDIT_REQUEST_CODE);
                        }
                    });
        }
    }

    private JsonResponseHandler getDetailHttpHandler = new JsonResponseHandler<OriginalDetailData>() {

        @Override
        public void success(@NonNull OriginalDetailData data) {
            if (data.content != null) {
                mOriginalObject = data.content;
                if (mOriginalObject == null) {
                    hideLoadingDialog();
                    Toast.makeText(OriginalDetailActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateHeadView(mOriginalObject);
                updateTitleEdit(mOriginalObject);
            } else {
                LToast.showToast("数据异常");
            }
        }

        @Override
        public void end() {
            super.end();
            hideLoadingDialog();
        }
    };

    private Action1<OriginalDetailItem> goUserHomeAction = new Action1<OriginalDetailItem>() {
        @Override
        public void call(OriginalDetailItem item) {
            if (!TextUtils.isEmpty(item.authorid)) {
                Intent intent = new Intent();
                intent.setClass(OriginalDetailActivity.this, UserHomeActivity.class);
                intent.putExtra("uid", item.authorid);
                startActivity(intent);
            }
        }
    };

    private void updateHeadView(final OriginalDetailItem originalObject) {
        if (mListHeadView == null || originalObject == null) {
            return;
        }
        TextView nickname = (TextView) mListHeadView.findViewById(R.id.original_detail_nickname);
        nickname.setText(originalObject.oa_nick_name);
        ImageView avatar = (ImageView) mListHeadView.findViewById(R.id.original_detail_avatar);
        ImageLoaderUtil.updateImage(avatar, originalObject.headimgurl);

        RxView.clicks(nickname)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .map(new Func1<Void, OriginalDetailItem>() {
                    @Override
                    public OriginalDetailItem call(Void aVoid) {
                        return originalObject;
                    }
                })
                .subscribe(goUserHomeAction);

        RxView.clicks(avatar)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .map(new Func1<Void, OriginalDetailItem>() {
                    @Override
                    public OriginalDetailItem call(Void aVoid) {
                        return originalObject;
                    }
                })
                .subscribe(goUserHomeAction);

        if (!TextUtils.isEmpty(originalObject.template_type)
                && originalObject.template_type.equals(
                OriginalDetailItem.TemplateType.multi.name())) {
            headerProvider = new PartitionDetailHeaderProvider();
        } else {
            headerProvider = new WebviewDetailHeaderProvider();
        }
        LinearLayout headerContainer = (LinearLayout) mListHeadView.findViewById(R.id.ll_original_detail_header_container);
        TextView title = (TextView) mListHeadView.findViewById(R.id.tv_title);
        TextView originaltime = (TextView) mListHeadView.findViewById(R.id.tv_time);
        TextView author = (TextView) mListHeadView.findViewById(R.id.tv_author);
        LinearLayout ll = (LinearLayout) mListHeadView.findViewById(R.id.ll);
        if (!TextUtils.isEmpty(originalObject.content)) {
            ll.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            originaltime.setVisibility(View.VISIBLE);
            author.setVisibility(View.VISIBLE);
            title.setText(originalObject.title);
            originaltime.setText(DateUtils.getStrTime(originalObject.ctime));
            author.setText(originalObject.author);
        } else {
            ll.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            originaltime.setVisibility(View.GONE);
            author.setVisibility(View.GONE);
        }
        headerInnerView = headerProvider.getDetailHeaderView(this, headerInnerView, headerContainer, originalObject);
        if (headerInnerView.getParent() == null) {
            headerContainer.addView(headerInnerView);
            if (headerProvider instanceof PartitionDetailHeaderProvider) {
                View viewById = headerInnerView.findViewById(R.id.tv_modify_picture);
                RxView.clicks(viewById)
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {
                                gotoEditPhoto();
                            }
                        });
            }
        }


        TextView time = (TextView) mListHeadView.findViewById(R.id.original_detail_time);
        time.setText(DateUtils.getStrTime(originalObject.ctime));

        // 创意列表
        List<TweetRelObject> tweetRelList = originalObject.tweetRelList;
        if (tweetRelList.size() != 0) {
            rlPics.setVisibility(View.VISIBLE);
            vPicsDivider.setVisibility(View.VISIBLE);

            picsAdapter.setItems(tweetRelList);
            picsAdapter.notifyDataSetChanged();
        } else {
            rlPics.setVisibility(View.GONE);
            vPicsDivider.setVisibility(View.GONE);
        }

        if (originalObject.praise != null && originalObject.praise.user != null && originalObject.praise.user.size() > 0) {
            mPraise.setVisibility(View.VISIBLE);
            mPraiseDivider.setVisibility(View.VISIBLE);
            praiseUserNameList = originalObject.praise.user;
            setPraiseUserContent(praiseUserNameList);
        } else {
            mPraise.setVisibility(View.GONE);
            mPraiseDivider.setVisibility(View.GONE);
        }

        if (originalObject.praise != null && originalObject.praise.flag) {
            mBottomPraiseImageView.setImageResource(R.drawable.original_detail_bottom_praised);
        } else {
            mBottomPraiseImageView.setImageResource(R.drawable.original_detail_bottom_praise);
        }

    }


    private void setPraiseUserContent(ArrayList<String> praiseUserNameList) {
        String praiseUserContent = "";
        if (praiseUserNameList != null && praiseUserNameList.size() > 0) {
            mPraise.setVisibility(View.VISIBLE);
            mPraiseDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < praiseUserNameList.size(); i++) {
                if (i == 0) {
                    praiseUserContent += praiseUserNameList.get(i);
                } else {
                    praiseUserContent += "、" + praiseUserNameList.get(i);
                }
            }
            TextView praiseRight = (TextView) mListHeadView.findViewById(R.id.original_detail_praise_right);
            praiseRight.setText(praiseUserContent);
        } else {
            mPraise.setVisibility(View.GONE);
            mPraiseDivider.setVisibility(View.GONE);
        }
    }

    protected void onPause() {
        super.onPause();
        if (headerProvider != null) {
            headerProvider.onPause(headerInnerView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(tid)) {
            sendNewRequest(tid);
        }
        if (headerProvider != null) {
            headerProvider.onResume(headerInnerView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private MyHttpResponseHandler httpHandler = new MyHttpResponseHandler();


    public class MyHttpResponseHandler extends AmbJsonResponseHandler<OriginalListData> {

        private String refreshType = TYPE_NEW;

        public void setRefreshType(String refreshType) {
            this.refreshType = refreshType;
        }

        @Override
        public void success(@Nullable OriginalListData data) {
            if (data == null) {
                return;
            }
            if (data.content != null) {
                if (originalCommentObjectList != null) {
                    if (refreshType.equals(TYPE_NEW)) {
                        // 下拉刷新，清空后最新的从头开始加
                        originalCommentObjectList.clear();
                        originalCommentObjectList.addAll(0, data.content.commentList);
                    } else if (refreshType.equals(TYPE_APPEND)) {
                        originalCommentObjectList.addAll(0, data.content.commentList);
                    } else {
                        // 上拉加载更多，从最后加
                        int size = originalCommentObjectList.size();
                        originalCommentObjectList.addAll(size, data.content.commentList);
                    }
                }
                mOriginalCommentAdapter.notifyDataSetChanged();
                updateCommentCountText();
                mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
            } else {
                if (originalCommentObjectList != null && originalCommentObjectList.size() > 0) {
                    mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                } else {
                    updateCommentCountText();
                    mPullRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }

            }
        }

        @Override
        public void end() {
            super.end();
            if (mPullRefreshListView != null) {
                mPullRefreshListView.onRefreshComplete();
            }
            updateCommentCountText();
        }
    }

    private void updateCommentCountText() {
        commentNumTextView.setText("评论（" + originalCommentObjectList.size() + "）");
        mBottomCommentTextView.setText(originalCommentObjectList.size() + "");
    }

    private void sendNewRequest(String tid) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.COMMENT_LIST);
        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.TYPE, TYPE_NEW);
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        params.put("last_cid", Integer.toString(0));
        httpHandler.setRefreshType(TYPE_NEW);
        HttpUtils.get(builder.build().toString(), params, httpHandler);
    }

    private void sendNextRequest(String last_cid, String tid) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.COMMENT_LIST);
        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        params.put("last_cid", last_cid);
        httpHandler.setRefreshType(TYPE_NEXT);
        HttpUtils.get(builder.build().toString(), params, httpHandler);
    }


    private void getOriginalDetail(String tid) {
        if (TextUtils.isEmpty(tid)) {
            return;
        }
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.ORIGINAL_DETAIL);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        HttpUtils.get(builder.build().toString(), params, getDetailHttpHandler);
    }

//    private void getTweetInnoList(String tid) {
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme(URLDefine.SCHEME);
//        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
//        builder.encodedPath(URLDefine.TWEET_INNO_LIST);
//
//        Map<String, String> params = new HashMap<>();
//        params.put("type", "new");
//        params.put("tid", tid);
//        params.put("uid", AccountManager.getInstance().getUserId());
//
//        params.put("first_ctime", "0");
//
//        HttpUtils.get(builder.build().toString(), params,
//                new platform.http.responsehandler.JsonResponseHandler<TweetInnoListObject>() {
//                    @Override
//                    public void success(@NonNull TweetInnoListObject data) {
//                        if (data.content.size() != 0) {
//                            rlPics.setVisibility(View.VISIBLE);
//                            vPicsDivider.setVisibility(View.VISIBLE);
//
//                            picsAdapter.setItems(data.content);
//                            picsAdapter.notifyDataSetChanged();
//                        } else {
//                            rlPics.setVisibility(View.GONE);
//                            vPicsDivider.setVisibility(View.GONE);
//                        }
//                    }
//                });
//    }


    private SimpleJsonResponseHandler commentHttpHandler = new SimpleJsonResponseHandler() {

        @Override
        public void success() {
            Toast.makeText(OriginalDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();

            mIsEmojiShow = false;
            sendNewRequest(tid);
            mCommentBottomEditText.setText("");

            mBottomCommentTextView.setText(originalCommentObjectList.size() + "");
            commentNumTextView.setText("评论（" + originalCommentObjectList.size() + "）");
            closeInputMethod();
            clearReplyInfo();
            mListView.setSelection(2);

            mOriginalObject.comment.num++;
        }
    };

    private void toComment(String content, String tid, String reply_cid, String reply_uid) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.COMMENT_PUBLISH);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        params.put("content", content);
        params.put("reply_cid", reply_cid);
        params.put("reply_uid", reply_uid);
        HttpUtils.get(builder.build().toString(), params, commentHttpHandler);
    }


    private SimpleJsonResponseHandler addPraiseHttpHandler = new SimpleJsonResponseHandler() {

        @Override
        public void success() {
            Toast.makeText(OriginalDetailActivity.this, "已赞", Toast.LENGTH_SHORT).show();
            mListView.setSelection(2);
            if (praiseUserNameList != null) {
                praiseUserNameList.add(0, AccountManager.getInstance().getUserData().nick_name);
                setPraiseUserContent(praiseUserNameList);
            }
            mBottomPraiseImageView.setImageResource(R.drawable.original_detail_bottom_praised);
            if (mOriginalObject.praise == null) {
                mOriginalObject.praise = new OriginalDetailItem.Praise();
            }
            mOriginalObject.praise.flag = true;
            mOriginalObject.praise.num++;
            Intent intent = new Intent();
            intent.putExtra(Constants.DETAIL_OBJECT, mOriginalObject);
            setResult(RESULT_OK, intent);
        }
    };

    private void toAddPraise(String tid) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.ZAN_ADD);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        HttpUtils.get(builder.build().toString(), params, addPraiseHttpHandler);
    }

    private SimpleJsonResponseHandler cancelPraiseHttpHandler = new SimpleJsonResponseHandler() {

        @Override
        public void success() {
            Toast.makeText(OriginalDetailActivity.this, "取消赞", Toast.LENGTH_SHORT).show();
            mListView.setSelection(2);
            if (praiseUserNameList != null) {
                praiseUserNameList.remove(0);
                setPraiseUserContent(praiseUserNameList);
            }
            mBottomPraiseImageView.setImageResource(R.drawable.original_detail_bottom_praise);
            if (mOriginalObject.praise == null) {
                mOriginalObject.praise = new OriginalDetailItem.Praise();
            }
            mOriginalObject.praise.flag = false;
            mOriginalObject.praise.num--;
            Intent intent = new Intent();
            intent.putExtra(Constants.DETAIL_OBJECT, mOriginalObject);
            setResult(RESULT_OK, intent);
        }
    };

    private void toCancelPraise(String tid) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.ZAN_CANCEL);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        HttpUtils.get(builder.build().toString(), params, cancelPraiseHttpHandler);
    }

    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            if (mCommentEditText != null) {
                imm.hideSoftInputFromWindow(mCommentEditText.getWindowToken(), 0);
            }

        }
    }

    /**
     * 清空输入框的回复信息
     */
    private void clearReplyInfo() {
        if (mCommentBottomEditText != null) {
            mCommentBottomEditText.setHint("");
            mCommentBottomEditText.setTag(null);
        }
    }

    private void showInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
        if (mCommentEditText != null) {
            imm.showSoftInput(mCommentEditText, InputMethodManager.SHOW_FORCED);
        }

    }

    private boolean isShowInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive(mCommentBottomEditText) || imm.isActive(mCommentEditText);
    }

    private void gotoEditPhoto() {
        Intent it = new Intent(this, ImageEditActivity.class);
        it.putExtra(OriginalConsts.BUNDLE_DETAIL_OBJECT, mOriginalObject);
        startActivity(it);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(OriginalPublishEvent event) {
        finish();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(OriginalEditEvent event) {
        getOriginalDetail(tid);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(CollectionEvent event) {
        mOriginalObject.collection.flag = event.flag;
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(OriginalDeleteEvent event) {
        // 如果已经删除，直接finish
        Intent intent = new Intent();
        intent.putExtra(Constants.IS_DELETE, true);
        intent.putExtra("detailobject", event.item);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            if (mOriginalObject != null && mOriginalObject.collection != null) {
                intent.putExtra("isCollect", mOriginalObject.collection.flag);
            }
            setResult(200, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
