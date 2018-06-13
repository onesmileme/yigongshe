package com.weikan.app.personalcenter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.BaseAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.weikan.app.LoginAndRgistActivity;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BasePullToRefreshActivity;
import com.weikan.app.personalcenter.adapter.MyAttentionAdapter;
import com.weikan.app.personalcenter.bean.ClearMineRedEvent;
import com.weikan.app.personalcenter.bean.FollowResultObject;
import com.weikan.app.personalcenter.bean.MyAttentionObject;
import com.weikan.app.personalcenter.bean.MyAttentionResponseObject;
import com.weikan.app.personalcenter.widget.MyAttentionItemView;
import com.weikan.app.util.LToast;

import java.util.List;

import de.greenrobot.event.EventBus;
import platform.http.responsehandler.ConfusedJsonResponseHandler;
import platform.http.responsehandler.JsonResponseHandler;
import rx.functions.Action1;

/**
 * 我的关注 / 我的粉丝
 *
 * @author kailun on 16/8/9
 */
public class MyAttentionActivity extends BasePullToRefreshActivity {

    public static final int ATTENTION = 1;
    public static final int FOLLOWER = 2;

    private static final String BUNDLE_TYPE = "bundle_type";
    private static final String BUNDLE_TARGET_UID = "bundle_target_uid";

    private MyAttentionAdapter adapter;

    private int curType;
    private String myUid = "";
    private String targetUid = "";

    public static Intent makeIntent(@NonNull Context context, int type) {
        Intent intent = new Intent(context, MyAttentionActivity.class);
        intent.putExtra(BUNDLE_TYPE, type);
        return intent;
    }

    public static Intent makeIntent(@NonNull Context context, int type, @NonNull String targetUid) {
        Intent intent = new Intent(context, MyAttentionActivity.class);
        intent.putExtra(BUNDLE_TYPE, type);
        intent.putExtra(BUNDLE_TARGET_UID, targetUid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // myUid必须在基类onCreate之前取得
        myUid = AccountManager.getInstance().getUserId();

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(BUNDLE_TYPE)) {
            curType = intent.getIntExtra(BUNDLE_TYPE, ATTENTION);
            targetUid = intent.getStringExtra(BUNDLE_TARGET_UID);
            if (targetUid == null) {
                targetUid = myUid;
            }
        }

        boolean showMe = TextUtils.isEmpty(targetUid) || TextUtils.equals(myUid, targetUid);
        if (curType == ATTENTION) {
            if (showMe) {
                setTitleText("我的关注");
            } else {
                setTitleText("TA的关注");
            }
        } else {
            if (showMe) {
                setTitleText("我的粉丝");
            } else {
                setTitleText("TA的粉丝");
            }
        }

        adapter = new MyAttentionAdapter(this);
        adapter.actionAttentionClick = new Action1<MyAttentionItemView>() {
            @Override
            public void call(MyAttentionItemView myAttentionItemView) {
                onAttentionClick(myAttentionItemView);
            }
        };

        PullToRefreshListView listView = getPullRefreshListView();
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.BOTH);

        sendRequest();
    }

    @Override
    protected String getTitleText() {
        return "";
    }

    @Override
    protected BaseAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void onPullDown() {
        sendRequest();
    }

    @Override
    protected void onPullUp() {
        sendRequestMore();
    }

    private void onAttentionClick(final MyAttentionItemView itemView) {

        if (!AccountManager.getInstance().isLogin()) {
            Intent intent = new Intent(MyAttentionActivity.this, LoginAndRgistActivity.class);
            startActivityForResult(intent, 1000);
            return;
        }

        final MyAttentionObject obj = itemView.get();
        if (obj == null) {
            return;
        }

        switch (obj.followType) {
            case 0: // 0 显示+关注 按钮
                sendFollowRequest(obj);
                break;

            case 1: // 1 显示 已关注 按钮;
                pendingCancelFollow(obj);
                break;

            case 2: // 2 显示 互相关注 按钮
                pendingCancelFollow(obj);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            myUid = AccountManager.getInstance().getUserId();
            sendRequest();
        }
    }

    /**
     * 加关注
     * @param obj obj
     */
    private void sendFollowRequest(final MyAttentionObject obj) {
        PersonalAgent.postAddFollow(myUid, obj.uid, new JsonResponseHandler<FollowResultObject>() {
            @Override
            public void success(@NonNull FollowResultObject data) {
                obj.followType = data.followType;
                adapter.notifyDataSetChanged();
                LToast.showToast("关注成功");
            }
        });
    }

    /**
     * 取消关注
     * @param obj obj
     */
    private void sendCancelFollowRequest(final MyAttentionObject obj) {
        PersonalAgent.postCancelFollow(myUid, obj.uid, new JsonResponseHandler<FollowResultObject>() {
            @Override
            public void success(@NonNull FollowResultObject data) {
                obj.followType = data.followType;
                adapter.notifyDataSetChanged();
                LToast.showToast("取消关注成功");
            }
        });
    }

    private void pendingCancelFollow(final MyAttentionObject obj) {
        final AlertDialog dialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setMessage("是否取消关注")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendCancelFollowRequest(obj);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.show();
    }

    private void sendRequest() {
        PersonalAgent.getAttentionList(curType, myUid, targetUid, "new", 0,
                new ConfusedJsonResponseHandler<MyAttentionResponseObject>() {
                    @Override
                    public void success(@NonNull MyAttentionResponseObject data) {
                        adapter.setItems(data.data);
                        adapter.notifyDataSetChanged();

                        if (curType == FOLLOWER) {
                            EventBus.getDefault().post(new ClearMineRedEvent(ClearMineRedEvent.CLEAR_FANS));
                        }
                    }

                    @Override
                    public void end() {
                        getPullRefreshListView().onRefreshComplete();
                    }
                });
    }

    private void sendRequestMore() {
        List<MyAttentionObject> items = adapter.getItems();
        if (items.size() == 0) {
            return;
        }

        int uTime = items.get(items.size() - 1).utime;
        PersonalAgent.getAttentionList(curType, myUid, targetUid, "next", uTime,
                new ConfusedJsonResponseHandler<MyAttentionResponseObject>() {
                    @Override
                    public void success(@NonNull MyAttentionResponseObject data) {
                        adapter.addItems(data.data);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void end() {
                        getPullRefreshListView().onRefreshComplete();
                    }
                });
    }
}
