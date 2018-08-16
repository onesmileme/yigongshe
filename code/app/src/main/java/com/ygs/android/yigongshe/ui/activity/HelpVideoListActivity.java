package com.ygs.android.yigongshe.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.HelpVideoItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.CircleDeleteResponse;
import com.ygs.android.yigongshe.bean.response.HelpVideoListResponse;
import com.ygs.android.yigongshe.bean.response.HelpVideoResponse;
import com.ygs.android.yigongshe.bean.response.ListLikeResponse;
import com.ygs.android.yigongshe.bean.response.UploadImageBean;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.utils.AppUtils;
import com.ygs.android.yigongshe.utils.NetworkUtils;
import com.ygs.android.yigongshe.utils.StringUtil;
import com.ygs.android.yigongshe.utils.VideoUtils;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import com.ygs.android.yigongshe.view.MyDecoration;
import com.ygs.android.yigongshe.view.TitleBarTabView;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/7/7.
 */

public class HelpVideoListActivity extends BaseActivity {
    private static int _COUNT = 20; //每页条数
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.titleBar)
    CommonTitleBar mTitleBar;
    private int pageCnt = 1; //第几页
    private HelpVideoListAdapter mAdapter;
    private LinkCall<BaseResultDataInfo<HelpVideoListResponse>> mCall;
    private int mActivityId;
    private final int REQUEST_VIDEO_CAPTURE = 0;
    private String mToken;
    private View errorView;
    @BindView(R.id.titleBarTabView)
    TitleBarTabView mTitleBarTabView;
    private String mType;//按时间，按点赞数
    private String mOrder;//排序规则，升序：ASC，降序：DESC
    AccountManager mAccountManager = YGApplication.accountManager;

    protected boolean openTranslucentStatus() {
        return true;
    }

    @Override
    protected void initIntent(Bundle bundle) {
        mActivityId = bundle.getInt("activity_id");
    }

    @Override
    protected void initView() {
        errorView =
            getLayoutInflater().inflate(R.layout.error_view, (ViewGroup)mRecyclerView.getParent(),
                false);

        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    finish();
                } else if (action == CommonTitleBar.ACTION_RIGHT_TEXT) {
                    AccountManager accountManager = YGApplication.accountManager;
                    mToken = accountManager.getToken();
                    if (TextUtils.isEmpty(mToken)) {
                        Toast.makeText(HelpVideoListActivity.this, "没有登录", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                    }
                }
            }
        });
        initTitleBarTabView();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(
            new MyDecoration(this, MyDecoration.VERTICAL_LIST, R.drawable.divider));
        initAdapter();
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setEnabled(false);
        refresh();
    }

    private void initTitleBarTabView() {
        int statusBarHeight = AppUtils.getStatusBarHeight(this);
        LinearLayout.LayoutParams params =
            (LinearLayout.LayoutParams)mTitleBarTabView.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        mTitleBarTabView.setLayoutParams(params);
        String[] tabs = getResources().getStringArray(R.array.video_tab_view);
        for (int i = 0; i < tabs.length; i++) {
            mTitleBarTabView.addTab(tabs[i], i);
        }
        mTitleBarTabView.setCurrentTab(0);

        mTitleBarTabView.addTabCheckListener(new TitleBarTabView.TabCheckListener() {
            @Override
            public void onTabChecked(int position) {
                if (position == mTitleBarTabView.getCurrentTabPos()) {
                    if (position == 0) {
                        mType = "create_at";
                        mOrder = "DESC";
                    } else {
                        mType = "like_num";
                        mOrder = "";
                    }
                    refresh();
                }
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_help_video_list;
    }

    private void initAdapter() {
        mAdapter = new HelpVideoListAdapter();
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(final BaseQuickAdapter adapter, final View view,
                                         final int position) {
                AccountManager accountManager = YGApplication.accountManager;
                if (TextUtils.isEmpty(accountManager.getToken())) {
                    Toast.makeText(HelpVideoListActivity.this, "没有登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (view.getId() == R.id.iv_markgood) {
                    final HelpVideoItemBean itemBean = (HelpVideoItemBean)adapter.getItem(position);
                    if (itemBean.is_like == 0) {
                        LinkCall<BaseResultDataInfo<ListLikeResponse>> like = LinkCallHelper.getApiService()
                            .likeVideo(itemBean.videoid, accountManager.getToken());
                        like.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ListLikeResponse>>() {
                            @Override
                            public void onResponse(BaseResultDataInfo<ListLikeResponse> entity,
                                                   Response<?> response, Throwable throwable) {
                                super.onResponse(entity, response, throwable);
                                if (entity != null) {
                                    if (entity.error == 2000) {
                                        Toast.makeText(HelpVideoListActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                                        ((ImageView)view).setImageResource(R.drawable.hasmarkgood);
                                        TextView tv = (TextView)adapter.getViewByPosition(mRecyclerView, position,
                                            R.id.markgood);
                                        tv.setText(itemBean.like_num + 1 + "");
                                        tv.setTextColor(getResources().getColor(R.color.green));
                                    } else {
                                        Toast.makeText(HelpVideoListActivity.this, entity.msg, Toast.LENGTH_SHORT)
                                            .show();
                                    }
                                }
                            }
                        });
                    }
                } else if (view.getId() == R.id.delete) {
                    HelpVideoItemBean itemBean = (HelpVideoItemBean)adapter.getItem(position);
                    LinkCall<BaseResultDataInfo<CircleDeleteResponse>> deleteCircle =
                        LinkCallHelper.getApiService()
                            .deleteVideo(itemBean.videoid, accountManager.getToken());
                    deleteCircle.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CircleDeleteResponse>>() {
                        @Override
                        public void onResponse(BaseResultDataInfo<CircleDeleteResponse> entity,
                                               Response<?> response, Throwable throwable) {
                            super.onResponse(entity, response, throwable);
                            if (entity != null) {
                                if (entity.error == 2000) {
                                    Toast.makeText(HelpVideoListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    refresh();
                                } else {
                                    Toast.makeText(HelpVideoListActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener()

        {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                HelpVideoItemBean itemBean = ((HelpVideoItemBean)adapter.getItem(position));
                bundle.putString("src", itemBean.src);
                goToOthers(HelpVideoDetailActivity.class, bundle);
            }
        });
    }

    private void refresh() {
        if (!NetworkUtils.isConnected(this)) {
            mAdapter.setEmptyView(errorView);
            return;
        }
        //mAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecyclerView.getParent());
        pageCnt = 1;
        mAdapter.setEnableLoadMore(false);
        mCall = LinkCallHelper.getApiService()
            .getHelpVideoList(pageCnt, _COUNT, mActivityId, mType, mOrder, mAccountManager.getToken());
        mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<HelpVideoListResponse>>() {
            @Override
            public void onResponse(BaseResultDataInfo<HelpVideoListResponse> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null) {
                    if (entity.error == 2000) {
                        HelpVideoListResponse data = entity.data;
                        pageCnt = data.page;
                        ++pageCnt;
                        _COUNT = data.perpage;
                        setData(true, data.video_list);
                        mAdapter.setEnableLoadMore(true);
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else {
                        mAdapter.setEnableLoadMore(true);
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(HelpVideoListActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void loadMore() {
        mCall = LinkCallHelper.getApiService()
            .getHelpVideoList(pageCnt, _COUNT, mActivityId, mType, mOrder, mAccountManager.getToken());
        mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<HelpVideoListResponse>>() {
            @Override
            public void onResponse(BaseResultDataInfo<HelpVideoListResponse> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null) {
                    if (entity.error == 2000) {
                        HelpVideoListResponse data = entity.data;
                        pageCnt = data.page;
                        ++pageCnt;
                        setData(false, data.video_list);
                    } else {
                        mAdapter.loadMoreFail();
                    }
                }
            }
        });
    }

    private void setData(boolean isRefresh, List data) {
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            mAdapter.setNewData(data);
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            }
        }
        if (size < _COUNT) {
            //第一页如果不够一页就不显示没有更多数据布局
            mAdapter.loadMoreEnd(isRefresh);
        } else {
            mAdapter.loadMoreComplete();
        }
    }

    @Override
    protected void onStop() {
        if (mCall != null) {
            mCall.cancel();
        }
        super.onStop();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (data != null) {
                Uri videoUri = data.getData();
                String path = VideoUtils.getPath(this, videoUri);
                File file = new File(path);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                //MultipartBody.Part  和后端约定好Key，这里的partName是用image
                MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                //MultipartBody.Part body1 = MultipartBody.Part.create(requestFile);
                // 添加描述
                String descriptionString = "hello, 这是文件描述";
                RequestBody description =
                    RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
                LinkCall<BaseResultDataInfo<UploadImageBean>> upload = LinkCallHelper.getApiService()
                    .uploadRemarkImage(description, body, StringUtil.md5(path));
                upload.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UploadImageBean>>() {
                    @Override
                    public void onResponse(BaseResultDataInfo<UploadImageBean> entity, Response<?> response,
                                           Throwable throwable) {
                        if (entity != null) {
                            if (entity.error == 2000) {
                                String uploadUrl = entity.data.site_url;
                                uploadHelpVideo(uploadUrl);
                            } else {
                                Toast.makeText(HelpVideoListActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }
    }

    private void uploadHelpVideo(String uploadUrl) {
        LinkCall<BaseResultDataInfo<HelpVideoResponse>> upload =
            LinkCallHelper.getApiService().uploadHelpVideo(uploadUrl, mActivityId, mToken);
        upload.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<HelpVideoResponse>>() {
            @Override
            public void onResponse(final BaseResultDataInfo<HelpVideoResponse> entity,
                                   Response<?> response, Throwable throwable) {
                if (entity != null) {
                    if (entity.error == 2000) {
                        Toast.makeText(HelpVideoListActivity.this, "视频上传成功", Toast.LENGTH_SHORT).show();
                        refresh();
                    } else {
                        Toast.makeText(HelpVideoListActivity.this, entity.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
