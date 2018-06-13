package com.weikan.app.personalcenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.bean.UserInfoObject;
import com.weikan.app.personalcenter.bean.FollowResultObject;
import com.weikan.app.util.DateUtils;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.LToast;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.adapter.WenyouListAdapter;
import com.weikan.app.wenyouquan.bean.WenyouListData;
import com.weikan.app.wenyouquan.model.DataTransModel;
import com.weikan.app.widget.photoviewpager.BitmapPersistence;
import com.weikan.app.widget.photoviewpager.PhotoViewPagerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.result.FailedResult;

/**
 * Created by liujian on 16/2/17.
 */
public class UserHomeActivity extends BasePullToRefreshActivity {

    private WenyouListAdapter adapter;
    private ArrayList<WenyouListData.WenyouListItem> dataList = new ArrayList<>();

    private View headView;

    private TextView nameText;
    private ImageView avatar;
    private ImageView sexIv;
    private ImageView blueV;
    private ImageView consIv;
    private TextView officeText;
    private TextView addrText;
    private TextView desText;
    private TextView userFollowText;
    private TextView userTalkText;

    private TextView attentionCountText;
    private TextView fansCountText;

    private UserInfoObject.UserInfoContent userInfo;
    private String uid;

    public static Intent makeIntent(final Context context, @NonNull final String uid) {
        Intent intent = new Intent(context, UserHomeActivity.class);
        intent.putExtra(URLDefine.UID, uid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        adapter = new WenyouListAdapter(this, dataList);
        uid = getIntent().getStringExtra(URLDefine.UID);
        super.onCreate(savedInstanceState);
        if (TextUtils.isEmpty(uid)) {
            finish();
            return;
        }

        if (TextUtils.equals(uid, AccountManager.getInstance().getUserId())) {
            setTitleText("我的主页");
        }

        sendUserInfoRequest();

        sendNewRequest();
    }

    @Override
    protected String getTitleText() {
        return "Ta的主页";
    }

    @Override
    protected View makeHeadView() {
        View view = LayoutInflater.from(this).inflate(R.layout.header_user_home, null);

        headView = view;

        nameText = (TextView) headView.findViewById(R.id.tv_mine_name);
        avatar = (ImageView) headView.findViewById(R.id.iv_admin);
        sexIv = (ImageView) headView.findViewById(R.id.iv_mine_sex);
        blueV = (ImageView) headView.findViewById(R.id.iv_mine_v);
        consIv = (ImageView) headView.findViewById(R.id.iv_mine_cons);
        userFollowText = (TextView) headView.findViewById(R.id.tv_user_follow);
        userFollowText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserFollowClick();
            }
        });
        userTalkText = (TextView) headView.findViewById(R.id.tv_user_talk);
        userTalkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserHomeActivity.this, TalkActivity.class);
                intent.putExtra(URLDefine.UID, uid);
                intent.putExtra(URLDefine.UNAME, userInfo.nick_name);
                startActivity(intent);
            }
        });
        if (AccountManager.getInstance().getUserId().equals(uid) || !AccountManager.getInstance().isLogin()) {
            userTalkText.setVisibility(View.INVISIBLE);
            userFollowText.setVisibility(View.INVISIBLE);
        } else {
            userTalkText.setVisibility(View.VISIBLE);
            userFollowText.setVisibility(View.VISIBLE);
        }

        attentionCountText = (TextView) headView.findViewById(R.id.tv_attention_count);
        fansCountText = (TextView) headView.findViewById(R.id.tv_fans_count);

        headView.findViewById(R.id.rl_attention_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MyAttentionActivity.makeIntent(UserHomeActivity.this,
                        MyAttentionActivity.ATTENTION,
                        uid);
                startActivity(intent);
            }
        });

        headView.findViewById(R.id.rl_fans_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MyAttentionActivity.makeIntent(UserHomeActivity.this,
                        MyAttentionActivity.FOLLOWER,
                        uid);
                startActivity(intent);
            }
        });

        return view;
    }

    void updateHeadView() {

        nameText.setText(userInfo.nick_name);
        ImageLoaderUtil.updateImageBetweenUrl(avatar, userInfo.headimgurl);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapPersistence.getInstance().clean();
                Drawable bitmap = avatar.getDrawable();
                if (bitmap != null) {
                    BitmapPersistence.getInstance().mDrawable.add(bitmap);
                }
                BitmapPersistence.getInstance().mDrawableUrl.add(userInfo.headimgurl);
                Intent intent = new Intent(UserHomeActivity.this, PhotoViewPagerActivity.class);
                intent.putExtra("bitmaps_index", 0);
                startActivity(intent);
            }
        });
        officeText = (TextView) headView.findViewById(R.id.tv_mine_office);
        addrText = (TextView) headView.findViewById(R.id.tv_mine_addr);
        desText = (TextView) headView.findViewById(R.id.tv_mine_des);

        if (!TextUtils.isEmpty(userInfo.company)) {
            officeText.setVisibility(View.VISIBLE);
            officeText.setText(userInfo.company + " " + userInfo.post);
        }

        if (!TextUtils.isEmpty(userInfo.province)) {
            addrText.setVisibility(View.VISIBLE);
            addrText.setText(userInfo.province + " " + userInfo.city);
        }

        if (!TextUtils.isEmpty(userInfo.autograph)) {
            desText.setVisibility(View.VISIBLE);
            desText.setText("个性签名：" + userInfo.autograph);
        }

        attentionCountText.setText(userInfo.follow_num + "");
        fansCountText.setText(userInfo.fans_num + "");

        refreshUserFollowTextView(userInfo.follow_type);

        if (userInfo.sex == 1) {
            sexIv.setImageResource(R.drawable.icon_male);
        } else {
            sexIv.setImageResource(R.drawable.icon_famale);
        }

        if (userInfo.role == 2) {
            blueV.setVisibility(View.VISIBLE);
            blueV.setImageResource(R.drawable.icon_bluev_b);
        } else if (userInfo.role == 1) {
            blueV.setVisibility(View.VISIBLE);
            blueV.setImageResource(R.drawable.icon_redv_b);
        } else {
            blueV.setVisibility(View.GONE);
        }


        long timemillis = ((long) userInfo.birthday) * 1000;
        consIv.setImageResource(DateUtils.getConstellationResWhite(timemillis));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DataTransModel.needRefresh) {
            executeRefreshing();
            DataTransModel.needRefresh = false;
        }
    }

    @Override
    protected BaseAdapter getAdapter() {
        return adapter;
    }

    public void executeRefreshing() {
        if (getPullRefreshListView() != null) {
            getPullRefreshListView().setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            getPullRefreshListView().setRefreshing();
            getPullRefreshListView().setMode(PullToRefreshBase.Mode.BOTH);
        }
    }

    @Override
    protected void onPullDown() {
        super.onPullDown();
        sendNewRequest();
    }

    @Override
    protected void onPullUp() {
        super.onPullUp();
        long ctime = 0;
        if (dataList.size() != 0) {
            WenyouListData.WenyouListItem item = dataList.get(dataList.size() - 1);
            ctime = item.ctime;
        }
        sendNextRequest(ctime);
    }

    private void onUserFollowClick() {
        switch (userInfo.follow_type) {
            case -1:
                break;

            case 0: // +关注
                postAddFollow();
                break;

            case 1: // 已关注
            case 2: // 互相关注
                postCancelFollow();
                break;

            default:
                break;
        }
    }

    private void refreshUserFollowTextView(int followType) {
        switch (followType) {
            case -1:
                userFollowText.setVisibility(View.GONE);
                break;

            case 0:
                userFollowText.setVisibility(View.VISIBLE);
                userFollowText.setText("+关注");
                break;

            case 1:
                userFollowText.setVisibility(View.VISIBLE);
                userFollowText.setText("已关注");
                break;

            case 2:
                userFollowText.setVisibility(View.VISIBLE);
                userFollowText.setText("互相关注");
                break;

            default:
                break;
        }
        if (!AccountManager.getInstance().isLogin()) {
            userFollowText.setVisibility(View.INVISIBLE);
        }
    }

    private void sendNewRequest() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.USER_MOMENTS);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEW);
        params.put("search_uid", uid);
        HttpUtils.get(builder.build().toString(), params, new AmbJsonResponseHandler<WenyouListData>() {
            @Override
            public void success(@Nullable WenyouListData data) {
                dataList.clear();
                if (data != null) {
                    dataList.addAll(data.content);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                getPullRefreshListView().onRefreshComplete();
            }
        });
    }

    private void sendNextRequest(long ctime) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.USER_MOMENTS);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put("search_uid", uid);
        params.put("last_ctime", String.valueOf(ctime));

        HttpUtils.get(builder.build().toString(), params, new platform.http.responsehandler.AmbJsonResponseHandler<WenyouListData>() {
            @Override
            public void success(@Nullable WenyouListData data) {
                if (data != null) {
                    dataList.addAll(data.content);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                getPullRefreshListView().onRefreshComplete();
            }
        });
    }

    private void sendUserInfoRequest() {
        String myUid = AccountManager.getInstance().getUserId();

        PersonalAgent.getUserHome(myUid, uid, new JsonResponseHandler<UserInfoObject>() {
            @Override
            public void success(@NonNull UserInfoObject data) {
                if (data.content != null) {
                    userInfo = data.content;
                    updateHeadView();
                }
            }
        });
    }

    private void postAddFollow() {
        String myUid = AccountManager.getInstance().getUserId();

        PersonalAgent.postAddFollow(myUid, uid, new JsonResponseHandler<FollowResultObject>() {
            @Override
            public void success(@NonNull FollowResultObject data) {
                userInfo.follow_type = data.followType;
                refreshUserFollowTextView(data.followType);

                userInfo.fans_num++;
                fansCountText.setText(userInfo.fans_num + "");
                LToast.showToast("关注成功");
            }

            @Override
            protected void failed(FailedResult r) {
                LToast.showToast("关注失败");
                r.setIsHandled(true);
            }
        });
    }

    private void postCancelFollow() {
        String myUid = AccountManager.getInstance().getUserId();

        PersonalAgent.postCancelFollow(myUid, uid, new JsonResponseHandler<FollowResultObject>() {
            @Override
            public void success(@NonNull FollowResultObject data) {
                userInfo.follow_type = data.followType;
                refreshUserFollowTextView(data.followType);

                userInfo.fans_num--;
                fansCountText.setText(userInfo.fans_num + "");
                LToast.showToast("取消关注成功");
            }

            @Override
            protected void failed(FailedResult r) {
                LToast.showToast("取消关注失败");
                r.setIsHandled(true);
            }
        });
    }

}