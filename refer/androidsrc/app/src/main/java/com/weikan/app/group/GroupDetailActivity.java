package com.weikan.app.group;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.weikan.app.Constants;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.group.bean.GroupDetailBean;
import com.weikan.app.listener.OnNoRepeatClickListener;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.LToast;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.WenyouPubActivity;
import com.weikan.app.wenyouquan.adapter.WenyouListAdapter;
import com.weikan.app.wenyouquan.bean.WenyouListData;
import com.weikan.app.widget.photoviewpager.BitmapPersistence;
import com.weikan.app.widget.photoviewpager.PhotoViewPagerActivity;
import com.weikan.app.widget.roundedimageview.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import platform.http.result.FailedResult;

/**
 * Created by Lee on 2017/01/06.
 */
public class GroupDetailActivity extends BasePullToRefreshActivity {
    protected ImageView ivGroupHeadBg;
    protected CircleImageView ivAvatar;
    protected TextView tvGroupName;
    protected TextView tvGroupLocation;
    protected ImageView ivGroupAdd;
    protected TextView tvGroupInfo;
    protected TextView tvGroupFen;
    protected TextView tvGroupFenNum;

    private ArrayList<WenyouListData.WenyouListItem> mDataList = new ArrayList<>();
    private WenyouListAdapter adapter;
    private ListView listView;
    private GroupDetailBean groupDetailBean;
    private String groupId;

    public static final int RESULTCODE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView ivRight = (ImageView) findViewById(R.id.iv_titlebar_right);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageDrawable(getResources().getDrawable(R.drawable.wenyoupub));
        ivRight.setOnClickListener(new OnNoRepeatClickListener() {
            @Override
            public void onNoRepeatClick(View v) {
                if (!AccountManager.getInstance().isLogin()) {
                    AccountManager.getInstance().gotoLogin(GroupDetailActivity.this);
                    return;
                }
                if (groupDetailBean == null) {
                    return;
                }
                Intent intent = new Intent(GroupDetailActivity.this, WenyouPubActivity.class);
                intent.putExtra(BundleParamKey.GROUPID, groupDetailBean.groupId);
                intent.putExtra(BundleParamKey.GROUPNAME, groupDetailBean.groupName);
                startActivity(intent);
            }
        });

        View headView = getLayoutInflater().inflate(R.layout.header_group_detail, null);
        initView(headView);
        listView = mPullRefreshListView.getRefreshableView();
        listView.addHeaderView(headView);

        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(width,width / 9 * 5);
        headView.setLayoutParams(layoutParams);

        groupId = getIntent().getStringExtra(BundleParamKey.GROUPID);
        initClick();
        getGroupDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendNewRequest();
    }

    private void initClick() {
        ivAvatar.setOnClickListener(new OnNoRepeatClickListener() {
            @Override
            public void onNoRepeatClick(View v) {
                if (groupDetailBean == null) {
                    return;
                }
                BitmapPersistence.getInstance().clean();
                Drawable bitmap = ivAvatar.getDrawable();
                if (bitmap != null) {
                    BitmapPersistence.getInstance().mDrawable.add(bitmap);
                }
                BitmapPersistence.getInstance().mDrawableUrl.add(groupDetailBean.avatar.getImageUrlBig());
                Intent intent = new Intent(GroupDetailActivity.this, PhotoViewPagerActivity.class);
                intent.putExtra("bitmaps_index", 0);
                startActivity(intent);
            }
        });
        ivGroupAdd.setOnClickListener(new OnNoRepeatClickListener() {
            @Override
            public void onNoRepeatClick(View v) {
                if (!AccountManager.getInstance().isLogin()) {
                    AccountManager.getInstance().gotoLogin(GroupDetailActivity.this);
                    return;
                }
                if (groupDetailBean == null) {
                    return;
                }
                if (groupDetailBean.isFollowed == 0) {
                    groupFollow();
                } else {
                    pendingCancelFollow();
                }
            }
        });
    }

    private void pendingCancelFollow() {
        final AlertDialog dialog = new AlertDialog.Builder(GroupDetailActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                .setMessage("是否取消关注")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelGroupFollow();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.show();
    }

    @Override
    protected String getTitleText() {
        return getIntent().getStringExtra(BundleParamKey.GROUPNAME);
    }

    @Override
    protected BaseAdapter getAdapter() {
        if (adapter == null) {
            adapter = new WenyouListAdapter(GroupDetailActivity.this, mDataList);
            adapter.setOnItemCommentMoreClickListener(new WenyouListAdapter.onItemCommentMoreClickListener() {
                @Override
                public void onItemCommentMoreClick(final int position) {
                    if (position <= listView.getFirstVisiblePosition() || position > listView.getLastVisiblePosition()) {
                        listView.smoothScrollToPosition(position);
                    }
                }
            });
        }
        return adapter;
    }

    @Override
    protected void onPullDown() {
        super.onPullDown();
        getGroupDetail();
        sendNewRequest();
    }

    @Override
    protected void onPullUp() {
        super.onPullUp();
        long sid = 0;
        if (mDataList.size() > 0) {
            sid = mDataList.get(mDataList.size() - 1).ctime;
        }
        sendNextRequest(sid);
    }

    private void sendNewRequest() {
        GroupAgent.groupArticleList(groupId, new AmbJsonResponseHandler<WenyouListData>() {
            @Override
            public void success(@Nullable WenyouListData data) {
                mDataList.clear();
                if (data == null || data.content == null) {
                } else {
                    mDataList.addAll(data.content);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                getPullRefreshListView().onRefreshComplete();
            }
        });
    }

    private void sendNextRequest(long sid) {
        GroupAgent.groupArticleListMore(groupId, String.valueOf(sid), new AmbJsonResponseHandler<WenyouListData>() {
            @Override
            public void success(@Nullable WenyouListData data) {
                if (data == null || data.content == null || data.content.size() == 0) {
                    LToast.showToast("没有更多内容了。");
                    return;
                }
                mDataList.addAll(data.content);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                getPullRefreshListView().onRefreshComplete();
            }
        });
    }

    private void getGroupDetail() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.GROUP_DETAIL);
        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.GROUP_ID, groupId);
        HttpUtils.get(builder.build().toString(), params, new AmbJsonResponseHandler<GroupDetailBean>() {
            @Override
            public void success(@Nullable GroupDetailBean data) {
                groupDetailBean = data;
                showGroupDetal();

                Intent intent = new Intent();
                intent.putExtra(Constants.GROUP_DETAIL, groupDetailBean);
                setResult(RESULTCODE, intent);
            }
        });
    }

    private void showGroupDetal() {
        if (groupDetailBean == null) {
            return;
        } else {
            ((TextView) findViewById(R.id.tv_titlebar_title)).setText(groupDetailBean.groupName);
            if (!TextUtils.isEmpty(groupDetailBean.backoundPic.getImageUrlBig())) {
                ImageLoaderUtil.updateImage(ivGroupHeadBg, groupDetailBean.backoundPic.getImageUrlBig(), R.drawable.background_people_information);
            } else {
                ivGroupHeadBg.setImageResource(R.drawable.background_people_information);
            }
            if (!TextUtils.isEmpty(groupDetailBean.avatar.getImageUrlBig())) {
                ImageLoaderUtil.updateImage(ivAvatar, groupDetailBean.avatar.getImageUrlBig(), R.drawable.user_default);
            }
            tvGroupName.setText(groupDetailBean.groupName);
            tvGroupLocation.setText(groupDetailBean.area);
            tvGroupFen.setVisibility(View.VISIBLE);
            tvGroupFenNum.setText(String.valueOf(groupDetailBean.followCount));
            if (groupDetailBean.isFollowed == 1) {
                ivGroupAdd.setImageResource(R.drawable.icon_group_follow);
            } else {
                ivGroupAdd.setImageResource(R.drawable.icon_group_add);
            }
            tvGroupInfo.setText(groupDetailBean.intro);
        }
    }

    private void groupFollow() {
        GroupAgent.groupFollow(groupId, new SimpleJsonResponseHandler() {
            @Override
            public void success() {
                LToast.showToast("关注成功");
                ivGroupAdd.setImageResource(R.drawable.icon_group_follow);
                groupDetailBean.isFollowed = 1;
                groupDetailBean.followCount++;
                tvGroupFenNum.setText(String.valueOf(groupDetailBean.followCount));
            }

            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
                LToast.showToast("关注失败");
            }
        });
    }

    private void cancelGroupFollow() {
        GroupAgent.cancelGroupFollow(groupId, new SimpleJsonResponseHandler() {
            @Override
            public void success() {
                LToast.showToast("取消关注成功");
                ivGroupAdd.setImageResource(R.drawable.icon_group_add);
                groupDetailBean.isFollowed = 0;
                if (groupDetailBean.followCount > 0) {
                    groupDetailBean.followCount--;
                }
                tvGroupFenNum.setText(String.valueOf(groupDetailBean.followCount));
            }

            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
                LToast.showToast("取消关注失败");
            }
        });
    }

    private void initView(View view) {
        ivGroupHeadBg = (ImageView) view.findViewById(R.id.iv_group_head_bg);
        ivAvatar = (CircleImageView) view.findViewById(R.id.iv_avatar);
        tvGroupName = (TextView) view.findViewById(R.id.tv_group_name);
        tvGroupLocation = (TextView) view.findViewById(R.id.tv_group_location);
        ivGroupAdd = (ImageView) view.findViewById(R.id.iv_group_add);
        tvGroupInfo = (TextView) view.findViewById(R.id.tv_group_info);
        tvGroupFenNum = (TextView) view.findViewById(R.id.tv_group_fen_num);
        tvGroupFen = (TextView) view.findViewById(R.id.tv_group_fen_text);
    }
}
