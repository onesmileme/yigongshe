package com.weikan.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.pili.pldroid.player.PLNetworkManager;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseFragmentActivity;
import com.weikan.app.bean.ClearRedMsgEvent;
import com.weikan.app.bean.RedMsgEvent;
import com.weikan.app.common.Model.RedNoticeModel;
import com.weikan.app.common.manager.FunctionConfig;
import com.weikan.app.common.manager.VoicePlayManager;
import com.weikan.app.live.LiveListFragment;
import com.weikan.app.news.NewsHomeFragment;
import com.weikan.app.personalcenter.MineContainerFragment;
import com.weikan.app.personalcenter.bean.MyMsgRedObject;
import com.weikan.app.personalcenter.event.ShouldUpdateUserVerifyEvent;
import com.weikan.app.request.BackgroundTaskAgent;
import com.weikan.app.util.AppUtils;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.ShareTools;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.WenyouListFragment;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import platform.http.HttpUtils;
import platform.http.result.FailedResult;

public class MainActivity extends BaseFragmentActivity {

    public static final String BUNDLE_SHOULD_UPDATE_USER_VERIFY = "should_update_user_verify";

    protected int REFRESHTIME = 10 * 1000;

    private long exitTime = 0;

    private FragmentTabHost mTabHost = null;
    private LayoutInflater layoutInflater;

    private View tweetRedView;
    private View momentRedView;
    private View mineRedView;

    private static class TabItem {
        public final Class fragmentClass;
        public final int icon;
        public final String label;

        public TabItem(Class fragmentClass, int icon, String label) {
            this.fragmentClass = fragmentClass;
            this.icon = icon;
            this.label = label;
        }
    }

    private TabItem[] tabItems;
    // 红点通知提示tab位置，分别为 文章，文友圈，我的
    private int[] mRedNoticeArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutInflater = LayoutInflater.from(this);
        setSwipeBackEnable(false);

        setTabArray();
//        sendBindRequest();

        try {
            PLNetworkManager.getInstance().startDnsCacheService(this, new String[]{"live.hkstv.hk.lxdns.com"});
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ImageLoaderUtil.initImageLoaderUtil(this);


        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        final int count = tabItems.length;

        for (int i = 0; i < count; i++) {

            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tabItems[i].label).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, tabItems[i].fragmentClass, null);
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(0xffffffff);
        }
        tweetRedView = mTabHost.getTabWidget().getChildAt(mRedNoticeArray[0]).findViewById(R.id.iv_tab_red);
        momentRedView = mTabHost.getTabWidget().getChildAt(mRedNoticeArray[1]).findViewById(R.id.iv_tab_red);
        mineRedView = mTabHost.getTabWidget().getChildAt(mRedNoticeArray[2]).findViewById(R.id.iv_tab_red);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                for (int i = 0; i < count; i++) {
                    View group = mTabHost.getTabWidget().getChildAt(i);
                    TextView tv = (TextView) group.findViewById(R.id.textview);
                    if (mTabHost.getCurrentTab() == i) {
                        tv.setTextColor(0xffe84c3d);
//						group.findViewById(R.id.iv_tab_red).setVisibility(View.GONE);
                    } else {
                        tv.setTextColor(0xff7a7b7b);
                    }
                }
            }
        });




        AccountManager.getInstance().init(this);
        AccountManager.getInstance().updateUserInfo();

        ShareTools.getInstance().init(this);

        EventBus.getDefault().register(this);


        handleNotificationBundle();

        startRedNotice();
//        AccountManager.getInstance().sendUserLoginRequest(this,"",
//                "http://wx.qlogo.cn/mmopen/Q3auHgzwzM48Zd3khmzCqozSEoQ7xExGc6bsjVFhRG5UrlcOTVaRibtrOBSVacbBQ2MuX6ibEhCuwB3QpSlS7UUZBfQ2eNqysp1vjLw8MhgrQ/0",
//                "weixin","七秒钟的记忆","ovyCPwqGuXlyMgbOfVJBUCLWOkPQ","");

        // 更新下热门话题
        BackgroundTaskAgent.updateTopic();

        parseIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        parseIntent(intent);
    }

    private void parseIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            boolean shouldUpdateUserVerify = extras.getBoolean(BUNDLE_SHOULD_UPDATE_USER_VERIFY, false);
            if (shouldUpdateUserVerify) {
                int i = 0;
                for (; i < tabItems.length; i++) {
                    if (tabItems[i].fragmentClass.equals(MineContainerFragment.class)) {
                        break;
                    }
                }
                if (i < tabItems.length) {
                    mTabHost.setCurrentTab(i);
                    EventBus.getDefault().post(new ShouldUpdateUserVerifyEvent());
                }
            }
        }
    }

    /**
     * 支持配置取消直播功能
     */
    private void setTabArray() {
        List<TabItem> items = new ArrayList<>();

        // 单Tab模板和多Tab模板二选一
        if (BuildConfig.ARTICLE_TEMPLATE.equals("multi")) {
            items.add(new TabItem(
                    NewsHomeFragment.class,
                    R.drawable.tab_home_btn,
                   "首页"));
        } else {
            items.add(new TabItem(
                    MainFragment.class,
                    R.drawable.tab_home_btn,
                    "首页"));
        }

        // 直播是可选的
        if (FunctionConfig.getInstance().isSupportLive()) {
            items.add(new TabItem(
                    LiveListFragment.class,
                    R.drawable.tab_live_btn,
                    "直播"));
        }

        items.add(new TabItem(
                WenyouListFragment.class,
                R.drawable.tab_message_btn,
                getString(R.string.tabHost3Title)
        ));

        items.add(new TabItem(
                MineContainerFragment.class,
                R.drawable.tab_mine_btn,
                "我的"
        ));

        tabItems = items.toArray(new TabItem[items.size()]);

        if (FunctionConfig.getInstance().isSupportLive()) {
            mRedNoticeArray = new int[]{0, 2, 3};
        } else {
            mRedNoticeArray = new int[]{0, 1, 2};
        }
    }


    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(tabItems[index].icon);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(tabItems[index].label);

        if (index == 0) {
            textView.setTextColor(0xffe84c3d);
        } else {
            textView.setTextColor(0xff7a7b7b);
        }

        return view;
    }


    @Override
    public boolean isSupportSwipBack() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabHost = null;
        EventBus.getDefault().unregister(this);
        handler.removeMessages(1);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        // 切换到后台之后，暂停所有声音播放
        VoicePlayManager playManager = VoicePlayManager.getInstance();
        if (playManager.getState() == VoicePlayManager.State.Playing) {
            playManager.stop();
        }

// EventBus.getDefault().unregister(this);
    }


    private static Bundle notificationBundle = null;

    public static void setNotificationBundle(Bundle bundle) {
        MainActivity.notificationBundle = bundle;
    }

    /**
     * 处理Notification的Bundle
     * 完成页面跳转之类的事情
     */
    public void handleNotificationBundle() {
        Bundle bundle = MainActivity.notificationBundle;
        if (bundle == null) {
            return;
        }

        MainActivity.notificationBundle = null;

        Class activityClass = (Class) bundle.getSerializable(BundleParamKey.NOTIFICATION_INTENT_TARGET);
        Intent intent = new Intent(this, activityClass);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void startRedNotice() {
        Message newmsg = new Message();
        newmsg.what = 1;
        handler.sendMessage(newmsg);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    sendRedRequest();
                    Message newmsg = new Message();
                    newmsg.what = 1;
                    handler.sendMessageDelayed(newmsg, REFRESHTIME);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(
                        MainActivity.this,
                        getResources().getString(R.string.doubleclick_exitapp_message)
                                + getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void setTranslucentStatus(boolean on) {

        if (Build.VERSION.SDK_INT >= 19) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
//            //创建状态栏的管理实例
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            //激活状态栏设置
//            tintManager.setStatusBarTintEnabled(true);
//            //设置颜色
//            tintManager.setStatusBarTintResource(R.color.transparent);
        }
    }

    public void onEventMainThread(RedMsgEvent event) {
        if (event.object != null) {
            if (event.object.tweet_num > 0) {
                tweetRedView.setVisibility(View.VISIBLE);
            } else {
                tweetRedView.setVisibility(View.GONE);
            }
            if (event.object.moments_num > 0) {
                momentRedView.setVisibility(View.VISIBLE);
            } else {
                momentRedView.setVisibility(View.GONE);
            }
            if (event.object.new_fans_num > 0 || event.object.sysNum > 0 || event.object.pmsg > 0) {
                mineRedView.setVisibility(View.VISIBLE);
            } else {
                mineRedView.setVisibility(View.GONE);
            }
        }
    }

    public void onEventMainThread(ClearRedMsgEvent event) {
        if (event.clearTag > 0) {
            if ((event.clearTag & ClearRedMsgEvent.CLEAR_TWEET) > 0) {
                tweetRedView.setVisibility(View.GONE);
            }
            if ((event.clearTag & ClearRedMsgEvent.CLEAR_MOMENT) > 0) {
                momentRedView.setVisibility(View.GONE);
            }
            if ((event.clearTag & ClearRedMsgEvent.CLEAR_MINE) > 0) {
                mineRedView.setVisibility(View.GONE);
            }
        }
    }


    private void sendRedRequest() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.MY_MSG_NOTIFY);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put("last_moments_time", RedNoticeModel.getMomentRefreshTime() / 1000 + "");
        params.put("last_tweet_time", RedNoticeModel.getTweetRefreshTime() / 1000 + "");

        HttpUtils.get(builder.build().toString(), params, new platform.http.responsehandler.AmbJsonResponseHandler<MyMsgRedObject>() {
            @Override
            public void success(MyMsgRedObject data) {
                RedNoticeModel.lastRedObject = data;
                EventBus.getDefault().post(new RedMsgEvent(data));
            }

            @Override
            protected void failed(FailedResult r) {
                r.setIsHandled(true);
            }
        });
    }

}

