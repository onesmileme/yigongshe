package com.weikan.app.personalcenter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.weikan.app.BuildConfig;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.account.AccountObtainEvent;
import com.weikan.app.base.BasePullToRefreshFragment;
import com.weikan.app.bean.ClearRedMsgEvent;
import com.weikan.app.bean.RedMsgEvent;
import com.weikan.app.bean.UserInfoObject;
import com.weikan.app.common.Model.RedNoticeModel;
import com.weikan.app.common.manager.FunctionConfig;
import com.weikan.app.group.MyFollowGroupActivity;
import com.weikan.app.personalcenter.bean.ClearMineRedEvent;
import com.weikan.app.personalcenter.bean.MyMsgRedObject;
import com.weikan.app.personalcenter.event.ShouldUpdateUserVerifyEvent;
import com.weikan.app.util.DateUtils;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.adapter.WenyouListAdapter;
import com.weikan.app.wenyouquan.bean.WenyouListData;
import com.weikan.app.wenyouquan.model.DataTransModel;
import com.weikan.app.widget.photoviewpager.BitmapPersistence;
import com.weikan.app.widget.photoviewpager.PhotoViewPagerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import platform.http.HttpUtils;

/**
 * 新的个人主页
 * Created by liujian on 16/7/25.
 */
public class NewMineFragment extends BasePullToRefreshFragment {

    private WenyouListAdapter adapter;
    private ArrayList<WenyouListData.WenyouListItem> dataList = new ArrayList<>();

    View headView;

    TextView nameText;
    ImageView avatar;
    ImageView sexIv;
    ImageView consIv;
    ImageView blueV;
    TextView officeText;
    TextView desText;
    TextView addrText;

    TextView redMsgText;
    TextView redFansText;
    TextView redTalkText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (adapter == null) {
            adapter = new WenyouListAdapter(getActivity(), dataList);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.base_pull_title).setVisibility(View.VISIBLE);
        view.findViewById(R.id.iv_titlebar_back).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.tv_titlebar_title)).setText("我的");

        ImageView rightBtn = (ImageView) view.findViewById(R.id.iv_titlebar_right_2);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AccountManager.getInstance().isLogin()) {
                    AccountManager.getInstance().gotoLogin(getActivity());
                    return;
                }
                Intent intent = new Intent(getActivity(), PersonalSettingsActivity.class);
                startActivity(intent);
            }
        });


        ImageView rightBtn2 = (ImageView) view.findViewById(R.id.iv_titlebar_right);
        rightBtn2.setVisibility(View.VISIBLE);
        rightBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);

            }
        });

        EventBus.getDefault().register(this);

        sendNewRequest(-1);


    }

    @Override
    protected View makeHeadView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.mine_list_header, null);

        headView = view;

        headView.findViewById(R.id.rl_mine_header_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyMsgActivity.class);
                startActivity(intent);

            }
        });

        headView.findViewById(R.id.rl_mine_header_identify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserIdentifyActivity.class);
                startActivity(intent);
            }
        });
        if (BuildConfig.IS_ROLE_AUTH_SUPPORT) {
            headView.findViewById(R.id.rl_mine_header_identify).setVisibility(View.VISIBLE);
        } else {
            headView.findViewById(R.id.rl_mine_header_identify).setVisibility(View.GONE);
        }

        headView.findViewById(R.id.rl_mine_header_talklist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyTalkListActivity.class);
                startActivity(intent);
            }
        });

        headView.findViewById(R.id.rl_mine_header_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = MyAttentionActivity.makeIntent(getActivity(), MyAttentionActivity.ATTENTION);
                startActivity(intent);
            }
        });

        headView.findViewById(R.id.rl_mine_header_fans).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = MyAttentionActivity.makeIntent(getActivity(), MyAttentionActivity.FOLLOWER);
                startActivity(intent);

            }
        });
        headView.findViewById(R.id.rl_mine_header_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyCollectActivity.class);
                startActivity(intent);

            }
        });
        if(FunctionConfig.getInstance().isSupportWenyouGroup()){
            headView.findViewById(R.id.rl_mine_header_follow).setVisibility(View.VISIBLE);
            headView.findViewById(R.id.rl_mine_header_follow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MyFollowGroupActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            headView.findViewById(R.id.rl_mine_header_follow).setVisibility(View.GONE);
            headView.findViewById(R.id.group_line).setVisibility(View.VISIBLE);
        }
        updateHeadView();

        updateRedView(RedNoticeModel.lastRedObject);

        return view;
    }

    void updateHeadView() {

        nameText = (TextView) headView.findViewById(R.id.tv_mine_name);
        avatar = (ImageView) headView.findViewById(R.id.iv_admin);
        sexIv = (ImageView) headView.findViewById(R.id.iv_mine_sex);
        consIv = (ImageView) headView.findViewById(R.id.iv_mine_cons);
        redMsgText = (TextView) headView.findViewById(R.id.tv_mine_header_msg_count);
        redFansText = (TextView) headView.findViewById(R.id.tv_mine_header_fans_count);
        redTalkText = (TextView) headView.findViewById(R.id.tv_mine_header_talklist_count);
        addrText = (TextView) headView.findViewById(R.id.tv_mine_addr);
        officeText = (TextView) headView.findViewById(R.id.tv_mine_office);
        blueV = (ImageView) headView.findViewById(R.id.iv_mine_v);

        final UserInfoObject.UserInfoContent userinfo = AccountManager.getInstance().getUserData();

        refreshUserVerifyInfo(userinfo);

        nameText.setText(userinfo.nick_name);
        ImageLoaderUtil.updateImageBetweenUrl(avatar, userinfo.headimgurl);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapPersistence.getInstance().clean();
                Drawable bitmap = avatar.getDrawable();
                if (bitmap != null) {
                    BitmapPersistence.getInstance().mDrawable.add(bitmap);
                }
                BitmapPersistence.getInstance().mDrawableUrl.add(userinfo.headimgurl);
                Intent intent = new Intent(getContext(), PhotoViewPagerActivity.class);
                intent.putExtra("bitmaps_index", 0);
                getContext().startActivity(intent);
            }
        });

        desText = (TextView) headView.findViewById(R.id.tv_mine_des);

        if (AccountManager.getInstance().getUserData().role == 2) {
            blueV.setVisibility(View.VISIBLE);
            blueV.setImageResource(R.drawable.icon_bluev_b);
        } else if (AccountManager.getInstance().getUserData().role == 1) {
            blueV.setVisibility(View.VISIBLE);
            blueV.setImageResource(R.drawable.icon_redv_b);
        } else {
            blueV.setVisibility(View.GONE);
        }

        setText(officeText, userinfo.company + " " + userinfo.post);
        setText(addrText, userinfo.province + " " + userinfo.city);

        if (TextUtils.isEmpty(userinfo.autograph)) {
            desText.setText("");
            desText.setVisibility(View.GONE);
        } else {
            desText.setText("个性签名：" + userinfo.autograph);
            desText.setVisibility(View.VISIBLE);
        }

        if (userinfo.sex == 1) {
            sexIv.setImageResource(R.drawable.icon_male);
        } else {
            sexIv.setImageResource(R.drawable.icon_famale);
        }

        long timemillis = ((long) userinfo.birthday) * 1000;
        consIv.setImageResource(DateUtils.getConstellationResWhite(timemillis));
    }

    void updateRedView(MyMsgRedObject object) {
        if (redMsgText != null && object != null) {
            if (object.sysNum > 0) {
                redMsgText.setVisibility(View.VISIBLE);
                redMsgText.setText(object.sysNum + "");
            } else {
                redMsgText.setVisibility(View.GONE);
                redMsgText.setText("");
            }
        }
        if (redFansText != null && object != null) {
            if (object.new_fans_num > 0) {
                redFansText.setVisibility(View.VISIBLE);
                redFansText.setText(object.new_fans_num + "");
            } else {
                redFansText.setVisibility(View.GONE);
                redFansText.setText("");
            }
        }
        if (redTalkText != null && object != null) {
            if (object.pmsg > 0) {
                redTalkText.setVisibility(View.VISIBLE);
            } else {
                redTalkText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 用户信息更新后刷新view
     *
     * @param event
     */
    public void onEventMainThread(AccountObtainEvent event) {
        updateHeadView();
    }


    /**
     * 红点消息
     *
     * @param event
     */
    public void onEventMainThread(RedMsgEvent event) {
        updateRedView(event.object);
    }

    /**
     * 消除红点
     *
     * @param event
     */
    public void onEventMainThread(ClearMineRedEvent event) {
        if (event.clearTag > 0) {
            if ((event.clearTag & ClearMineRedEvent.CLEAR_SYSMSG) > 0) {
                if (redMsgText != null) {
                    redMsgText.setVisibility(View.GONE);
                    redMsgText.setText("");
                }
                if (RedNoticeModel.lastRedObject != null) {
                    RedNoticeModel.lastRedObject.sysNum = 0;
                }
            }
            if ((event.clearTag & ClearMineRedEvent.CLEAR_FANS) > 0) {
                if (redFansText != null) {
                    redFansText.setVisibility(View.GONE);
                    redFansText.setText("");
                }
                if (RedNoticeModel.lastRedObject != null) {
                    RedNoticeModel.lastRedObject.new_fans_num = 0;
                }
            }
            if ((event.clearTag & ClearMineRedEvent.CLEAR_TALK) > 0) {
                if (redTalkText != null) {
                    redTalkText.setVisibility(View.GONE);
                }
                if (RedNoticeModel.lastRedObject != null) {
                    RedNoticeModel.lastRedObject.pmsg = 0;
                }
            }
        }
        // 红点都没了也通知下主界面tab
        if (RedNoticeModel.lastRedObject.sysNum == 0 && RedNoticeModel.lastRedObject.new_fans_num == 0
                && RedNoticeModel.lastRedObject.pmsg == 0) {
            EventBus.getDefault().post(new ClearRedMsgEvent(ClearRedMsgEvent.CLEAR_MINE));
        }
    }

    @SuppressWarnings("UnusedParameters")
    public void onEventMainThread(ShouldUpdateUserVerifyEvent event) {
        // updateUserInfo成功后，会广播AccountObtainEvent
        // 使得headView更新
        AccountManager.getInstance().updateUserInfo();
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
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
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
        AccountManager.getInstance().updateUserInfo();
        sendNewRequest(-1);
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

    private void sendNewRequest(long ctime) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.WENYOU_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEW);
        params.put("filter_type", "3");
        HttpUtils.get(builder.build().toString(), params, new platform.http.responsehandler.AmbJsonResponseHandler<WenyouListData>() {
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
        builder.encodedPath(URLDefine.WENYOU_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put("filter_type", "3");
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

    private void refreshUserVerifyInfo(UserInfoObject.UserInfoContent userinfo) {
        View rlIdentify = headView.findViewById(R.id.rl_mine_header_identify);
        TextView tvIdentify = (TextView) headView.findViewById(R.id.tv_mine_header_identify);
        // 待审核和审核通过
        if (userinfo.verifyStatus == 0 || userinfo.verifyStatus == 2) {
            rlIdentify.setEnabled(true);
            tvIdentify.setText("身份认证");
        }
        // 待审核
        else if (userinfo.verifyStatus == 1) {
            rlIdentify.setEnabled(false);
            tvIdentify.setText("身份认证(审核中)");
        }
        // 审核通过
        else if (userinfo.verifyStatus == 3) {
            rlIdentify.setEnabled(false);
            tvIdentify.setText("身份认证(已认证)");
        }
    }

    private void setText(TextView textView, String text) {
        if (textView == null) {
            return;
        }
        if (TextUtils.isEmpty(text.trim())) {
            textView.setText("");
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }
}
