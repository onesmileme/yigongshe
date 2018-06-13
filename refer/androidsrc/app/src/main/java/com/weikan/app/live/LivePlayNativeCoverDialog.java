package com.weikan.app.live;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.ShareActivity;
import com.weikan.app.account.AccountManager;
import com.weikan.app.common.adater.BaseRecyclerAdapter;
import com.weikan.app.common.manager.FunctionConfig;
import com.weikan.app.common.net.JsonArrayResponseHandler;
import com.weikan.app.common.net.Page;
import com.weikan.app.common.util.TickTimer;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.listener.OnNoRepeatClickListener;
import com.weikan.app.live.adapter.GiftGridAdapter;
import com.weikan.app.live.adapter.GiftPageAdapter;
import com.weikan.app.live.adapter.LiveEventListAdapter;
import com.weikan.app.live.adapter.OnlineUserListAdapter;
import com.weikan.app.live.bean.GiftObject;
import com.weikan.app.live.bean.LiveEventObject;
import com.weikan.app.live.bean.LiveListObject;
import com.weikan.app.live.bean.MoneyBean;
import com.weikan.app.live.bean.OnlineUserListDataObject;
import com.weikan.app.live.bean.OnlineUserObject;
import com.weikan.app.live.bean.PayEvent;
import com.weikan.app.personalcenter.UserHomeActivity;
import com.weikan.app.piaoxin.HeartLayout;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.DensityUtil;
import com.weikan.app.util.Global;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.LToast;
import com.weikan.app.util.URLDefine;
import com.weikan.app.view.GiftView;
import com.weikan.app.wenyouquan.bean.WenyouListData;
import com.weikan.app.widget.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import platform.http.responsehandler.AmbJsonResponseHandler;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import platform.http.result.FailedResult;

/**
 * Created by ylp on 2017/1/11.
 */
public class LivePlayNativeCoverDialog extends Dialog implements
        android.view.View.OnClickListener {
    private WenyouListData.WenyouListItem item;
    private LiveListObject liveData;
    private RoundedImageView rvPhoto;
    private TextView tvName;
    private TextView tvUserNum;
    private ImageView ivExit;
    private OnlineUserListAdapter onlineUserListAdapter;
    private LiveEventListAdapter liveEventListAdapter;
    private RecyclerView rvOnlineUser;
    private ListView lvEvent;
    private boolean isRefreshing = false;
    private Page page = null;
    private TickTimer tickTimer = null;
    private TickTimer giftTimer = null;
    private TickTimer seekTimer = null;
    private List<GiftObject> giftList = null;
    private long lasttime;
    private LinearLayout giftLl;
    private ImageView ivMessage;
    private ImageView ivShare;
    private ImageView ivGift;
    private ImageView ivLove;
    private Button sendMessage;
    private EditText etMessage;
    private LinearLayout llEvent;
    private LinearLayout llInput;
    private Context context;
    private boolean inputIsShow = false;
    private boolean giftIsShow = false;
    private LinearLayout root;
    private List<GiftObject.Gift> gifts;
    private RelativeLayout rlGift;
    private ViewPager vpGift;
    private LinearLayout llPage;
    private TextView tvChongzhi;
    private Button sendGift;
    private GiftObject.Gift chooseGift;
    private List<GiftGridAdapter> giftGridAdapterList;
    private List<View> viewPagerList;
    private GiftPageAdapter giftPageAdapter;
    private HeartLayout heartLayout;
    private LivePlayNativeActivity activity;
    private double antionY;
    private View centerView;
    private RelativeLayout top;
    private TickTimer playBackTimer;
    private long eventTime = new Date().getTime();
    private int currentMessage = 0;
    private long playBackTime;
    private int money;
    private boolean isGetEvent = false;
    private ImageView ivPlay;
    private SeekBar seekBar;
    private TextView playTime;
    private ArrayList<LiveEventObject> liveEventObjects = new ArrayList<LiveEventObject>();
    private double alreadyPlayTimel = 0;
    private double totalTime;
    private boolean isPlay = false;
    public static final int START_PLAY = 1;
    public static final int STOP_PLAY = 2;
    public static final int CLOSE_PLAY = 3;
    private String sec;
    private String min;
    private Handler playHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case START_PLAY:
                    seekTimer.start();
                    isPlay = true;
                    playBackTimer.start();
                    getEvents(playBackTime*1000);
                    ivPlay.setImageResource(R.drawable.icon_live_stop);
                    activity.startPlay();
                    break;
                case STOP_PLAY:
                    seekTimer.stop();
                    isPlay = false;
                    playBackTimer.stop();
                    ivPlay.setImageResource(R.drawable.icon_play);
                    activity.stopPlay();
                    break;
                case CLOSE_PLAY:
                    seekTimer.stop();
                    isPlay = false;
                    playBackTimer.stop();
                    ivPlay.setImageResource(R.drawable.icon_play);
                    activity.stopPlay();
                    seekBar.setProgress(100);
                    playTime.setText(min+":"+sec+"/"+LivePlayNativeCoverDialog.this.min+":"+LivePlayNativeCoverDialog.this.sec);
                    break;
            }
        }
    };
    public LivePlayNativeCoverDialog(Context context, LiveListObject liveData, LivePlayNativeActivity activity) {
        super(context, R.style.NewDialog);
        this.context = context;
        this.liveData = liveData;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_play);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.width = Global.getInstance().SCREEN_WIDTH;
        dialogWindow.setAttributes(lp);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        EventBus.getDefault().register(this);
        onlineUserListAdapter = new OnlineUserListAdapter(context);
        liveEventListAdapter = new LiveEventListAdapter(context);
        giftGridAdapterList = new ArrayList<>();
        viewPagerList = new ArrayList<>();
        assignViews();
        initView();
        initViewListener();
        initTime();
        getOnlineUsers();
        initTickTimer();
        initGifts();
    }
    //设置总共播放时间
    public void setTotalTime(double totalTime){
        this.totalTime = totalTime;
        if(totalTime % 1000 > 0){
            this.totalTime = (((int)(totalTime/1000))+1)*1000;
        }
        int se  = ((int)(this.totalTime/1000)) % 60;
        int mi = ((int)(this.totalTime/1000)) / 60;
        min = String.valueOf(mi);
        sec = String.valueOf(se);
        if(se < 10){
            sec = "0"+se;
        }
        if(mi < 10){
            min = "0"+mi;
        }
        playTime.setText("00:00/"+min+":"+sec);
    }
   //设置播放状态
    public void setPlayStatus(int what){
        Message message = new Message();
        message.what = what;
        playHandler.dispatchMessage(message);
    }

    private void initTime() {
        if (liveData.status == 1) {
            eventTime = new Date().getTime();
        } else {
            playBackTime = liveData.stime;
            eventTime = liveData.stime;
        }
    }
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }
    @Override
    public void dismiss() {
        super.dismiss();
    }
    private void initGifts() {
        LiveAgent.giftList(new JsonArrayResponseHandler<GiftObject.Gift>() {
            @Override
            public void success(@NonNull ArrayList data) {
                gifts = data;
                if (gifts != null && gifts.size() != 0) {
                    showGifts();
                }
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            showTop();
            return true;
        }
        return super.onTouchEvent(event);
    }
    private void initViewListener() {
        ivMessage.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivGift.setOnClickListener(this);
        ivLove.setOnClickListener(this);
        sendMessage.setOnClickListener(new OnNoRepeatClickListener() {
            @Override
            public void onNoRepeatClick(View v) {
                if (!TextUtils.isEmpty(etMessage.getText().toString())) {
                    postNewMessageEvent(etMessage.getText().toString());
                }
            }
        });
        ivExit.setOnClickListener(this);
        root.setOnClickListener(this);
        lvEvent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    antionY = event.getRawY();

                } else if (event.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (Math.abs(event.getRawY() - antionY) < 10) {
                        if (inputIsShow) {
                            inputIsShow = false;
                            centerView.setVisibility(View.VISIBLE);
                            top.setVisibility(View.VISIBLE);
                            closeInputMethod();
                            etMessage.setText("");
                            llInput.setVisibility(View.GONE);
                            llEvent.setVisibility(View.VISIBLE);

                        }
                        if (giftIsShow) {
                            giftIsShow = false;
                            rlGift.setVisibility(View.GONE);
                            llEvent.setVisibility(View.VISIBLE);
                            lvEvent.setVisibility(View.VISIBLE);
                        }
                    }
                }
                return false;
            }
        });

        tvChongzhi.setOnClickListener(this);
        rvPhoto.setOnClickListener(this);
        onlineUserListAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<OnlineUserObject>() {
            @Override
            public void onItemClick(BaseListItemView<OnlineUserObject> itemView) {
                Intent userintent = new Intent(context, UserHomeActivity.class);
                userintent.putExtra(BundleParamKey.UID, itemView.get().uid);
                context.startActivity(userintent);
            }
        });
        vpGift.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateGiftPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlay){
                    activity.stopPlay();
                    seekTimer.stop();
                    playBackTimer.stop();
                    ivPlay.setImageResource(R.drawable.icon_play);
                }else{
                    activity.startPlay();
                    seekTimer.start();
                    playBackTimer.start();
                    ivPlay.setImageResource(R.drawable.icon_live_stop);
                }
                isPlay = !isPlay;
            }
        });
    }

    private void updateGiftPoint(int position) {
        for (int i = 0; i < llPage.getChildCount(); i++) {
            ImageView point = (ImageView) llPage.getChildAt(i);
            if (i == position) {
                point.setImageResource(R.drawable.white_point);
            } else {
                point.setImageResource(R.drawable.gray_point);
            }
        }
    }

    private void initTickTimer() {
        tickTimer = new TickTimer(5000, 5000, new Runnable() {

            @Override
            public void run() {
                getOnlineUsers();
                if (liveData.status == 1) {
                    getEvents(0);
                }
            }
        });
        playBackTimer = new TickTimer(1000, 1000, new Runnable() {
            @Override
            public void run() {
                playBackTime = playBackTime + 1;
                // TODO 直播不用playback，临时处理下，后续优化
                if (liveData.status != 1) {
                    showPlayBack(playBackTime);
                }
            }
        });
        giftTimer = new TickTimer(1000, 1000, new Runnable() {

            @Override
            public void run() {
                getGift();
            }
        });
        seekTimer = new TickTimer(0, 100, new Runnable() {

            @Override
            public void run() {

                if(alreadyPlayTimel <= totalTime){
                    int se  = ((int)(alreadyPlayTimel/1000)) % 60;
                    int mi = ((int)(alreadyPlayTimel/1000)) / 60;
                    String sec = String.valueOf(se);
                    String min = String.valueOf(mi);
                    if(se < 10){
                        sec = "0"+se;
                    }
                    if(mi < 10){
                        min = "0"+mi;
                    }
                    playTime.setText(min+":"+sec+"/"+LivePlayNativeCoverDialog.this.min+":"+LivePlayNativeCoverDialog.this.sec);
                }else{
                    playTime.setText(min+":"+sec+"/"+LivePlayNativeCoverDialog.this.min+":"+LivePlayNativeCoverDialog.this.sec);
                    seekBar.setProgress(100);
                }
                double pro = alreadyPlayTimel/totalTime*100;
                seekBar.setProgress((int)pro);
//                seekBar.setProgress(52);
                alreadyPlayTimel = alreadyPlayTimel + 100;
            }
        });
    }
    private void showPlayBack(long time) {
        int posi = liveEventListAdapter.getCount();
        if (currentMessage <= liveEventObjects.size() - 1) {
            List<LiveEventObject> showLiveEvent = new ArrayList<LiveEventObject>();
            for (int i = currentMessage; i < liveEventObjects.size(); i++) {
                LiveEventObject da = liveEventObjects.get(i);
                if (da.timestamp / 1000 == time) {
                    da.liveUid = liveData.live_uid;
                    showLiveEvent.add(da);
                    currentMessage++;
                }
            }
            liveEventListAdapter.addItems(showLiveEvent);
            liveEventListAdapter.notifyDataSetChanged();
            int count = liveEventListAdapter.getCount();
            if(count > posi){
                lvEvent.setSelection(count - 1);
            }
        } else {
            if (!isGetEvent) {
                isGetEvent = true;
                getEvents(0);
            }
        }

    }
    private void assignViews() {
        root = (LinearLayout) findViewById(R.id.root);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvUserNum = (TextView) findViewById(R.id.tv_user_num);
        rvPhoto = (RoundedImageView) findViewById(R.id.iv_photo);
        ivExit = (ImageView) findViewById(R.id.iv_exit);
        rvOnlineUser = (RecyclerView) findViewById(R.id.rv_online_user);
        lvEvent = (ListView) findViewById(R.id.lv_event);
        giftLl = (LinearLayout) findViewById(R.id.gift);
        ivMessage = (ImageView) findViewById(R.id.iv_message);
        ivShare = (ImageView) findViewById(R.id.iv_share);
        ivGift = (ImageView) findViewById(R.id.iv_gift);
        ivLove = (ImageView) findViewById(R.id.iv_love);
        llInput = (LinearLayout) findViewById(R.id.ll_input);
        llEvent = (LinearLayout) findViewById(R.id.ll_event);
        etMessage = (EditText) findViewById(R.id.ed_dis_detail);
        sendMessage = (Button) findViewById(R.id.bt_dis_detail_pub);
        rlGift = (RelativeLayout) findViewById(R.id.rl_gift);
        vpGift = (ViewPager) findViewById(R.id.vp_gift);
        llPage = (LinearLayout) findViewById(R.id.ll_page);
        tvChongzhi = (TextView) findViewById(R.id.tv_chongzhi);
        sendGift = (Button) findViewById(R.id.bu_send_gift);
        heartLayout = (HeartLayout) findViewById(R.id.heart);
        llEvent.setVisibility(View.VISIBLE);
        llInput.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvOnlineUser.setLayoutManager(layoutManager);
        rvOnlineUser.setAdapter(onlineUserListAdapter);
        sendGift.setOnClickListener(this);
        sendGift.setClickable(false);
        lvEvent.setAdapter(liveEventListAdapter);
        centerView = findViewById(R.id.center_view);
        top = (RelativeLayout) findViewById(R.id.top);
        seekBar = (SeekBar)findViewById(R.id.progresss);
        ivPlay = (ImageView)findViewById(R.id.iv_play);
        playTime = (TextView)findViewById(R.id.play_time);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                seekTimer.stop();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double position = seekBar.getProgress();
                liveEventObjects.clear();
                currentMessage = 0;
                if(alreadyPlayTimel > (position/100)*totalTime){
                    liveEventListAdapter.clear();
                    liveEventListAdapter.notifyDataSetChanged();
                }
                alreadyPlayTimel = (position/100)*totalTime;
                activity.seekTo(alreadyPlayTimel);
               playBackTime = (long)((alreadyPlayTimel/1000)+liveData.stime);
                playBackTimer.stop();
                activity.stopPlay();
            }
        });
    }

    private void getEvents(long time) {
        if (isRefreshing) {
            return;
        } else {
            isRefreshing = true;
        }
        page = Page.NEXT;
        LiveEventObject last = liveEventListAdapter.last();
        if (last != null && last.timestamp != 0) {
            eventTime = last.timestamp;
        }
        if(time != 0){
            eventTime = time;
        }
        LiveAgent.getEvents(liveData.liveId, page, eventTime, new JsonArrayResponseHandler<LiveEventObject>() {
            @Override
            public void success(@NonNull ArrayList<LiveEventObject> data) {
                if (liveData.status == 1) {
                    if (page == Page.NEW) {
                        liveEventListAdapter.clear();
                    }
                    for (LiveEventObject da : data) {
                        da.liveUid = liveData.live_uid;
                        if(!liveEventListAdapter.getItems().contains(da)){
                            liveEventListAdapter.getItems().add(da);
                        }
                    }

                    liveEventListAdapter.notifyDataSetChanged();
                    int count = liveEventListAdapter.getCount();
                    lvEvent.setSelection(count - 1);
                } else {
                    if (data == null || data.size() == 0) {
                        playBackTimer.stop();
                    } else {
                        liveEventObjects.clear();
                        currentMessage = 0;
                        liveEventObjects.addAll(data);
                    }
                }
            }

            @Override
            public void end() {
                super.end();
                isRefreshing = false;
                isGetEvent = false;
            }

            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
            }
        });
    }

    private void initView() {
        tvName.setText(liveData.nickname);
        tvUserNum.setText(Integer.toString(liveData.online_num));
        ImageLoaderUtil.updateImage(rvPhoto, liveData.live_headimg);
        if (liveData.status == 1) {
            ivMessage.setVisibility(View.VISIBLE);
            if(FunctionConfig.getInstance().isSupportLiveGift()) {
                ivGift.setVisibility(View.VISIBLE);
            } else {
                ivGift.setVisibility(View.GONE);
            }
            seekBar.setVisibility(View.GONE);
            playTime.setVisibility(View.GONE);
            ivPlay.setVisibility(View.GONE);
        } else {
            ivMessage.setVisibility(View.GONE);
            ivGift.setVisibility(View.GONE);
            seekBar.setVisibility(View.VISIBLE);
            playTime.setVisibility(View.VISIBLE);
            ivPlay.setVisibility(View.VISIBLE);
        }
    }

    private void getOnlineUsers() {
        LiveAgent.getOnlineUsers(liveData.liveId, new JsonResponseHandler<OnlineUserListDataObject>() {

            @Override
            public void success(@NonNull OnlineUserListDataObject data) {
                tvUserNum.setText(data.num + "人");
                onlineUserListAdapter.setItems(data.list);
                onlineUserListAdapter.notifyDataSetChanged();
//                int count = liveEventListAdapter.getCount();
//                lvEvent.setSelection(count - 1);
            }
        });
    }

    @Override
    public void cancel() {
        super.cancel();
        giftTimer.stop();
        tickTimer.stop();
        playBackTimer.stop();
        closeInputMethod();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeInputMethod();
        giftTimer.stop();
        tickTimer.stop();
        playBackTimer.stop();
        seekTimer.stop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (liveData.status == 1) {
            giftTimer.start();
        } else {
            playBackTimer.start();
        }
        tickTimer.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancel();
            activity.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getGift() {
        giftTimer.stop();
        if (giftList == null || giftList.size() == 0) {
            if (lasttime == 0L) {
                lasttime = new Date().getTime();
            }
        } else {
            lasttime = giftList.get(giftList.size() - 1).timestamp;
        }
        if (isShowing()) {
            LiveAgent.getGiftList(liveData.liveId, lasttime, new JsonArrayResponseHandler<GiftObject>() {
                @Override
                public void success(@NonNull ArrayList<GiftObject> data) {
                    giftList = data;
                    if (context == null) {
                        giftTimer.stop();
                        return;
                    }
                    if (data != null && data.size() != 0) {
                        int i = 0;
                        giftLl.removeAllViews();
                        for (GiftObject gif : data) {
                            View view = GiftView.creatGiftView(context, gif.user.headimgurl, gif.user.nickname, "送了一个" + gif.gift.name, gif.gift.url);
                            if (i == 0) {
                                giftLl.addView(view);
                            } else {
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(0, DensityUtil.dip2px(context, 10f), 0, 0);
                                view.setLayoutParams(lp);
                                giftLl.addView(view);
                            }
                            i++;
                        }
                        giftLl.invalidate();
                        int start = -DensityUtil.dip2px(context, 206f);
                        ValueAnimator animator = ValueAnimator.ofFloat(start, 0f);
                        animator.setDuration(500).start();
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float x = (Float) animation.getAnimatedValue();
                                giftLl.setTranslationX(x);
                            }
                        });
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (context != null) {
                                            int start = 0 - DensityUtil.dip2px(context, 206f);
                                            ValueAnimator animator = ValueAnimator.ofFloat(0f, start);
                                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                @Override
                                                public void onAnimationUpdate(ValueAnimator animation) {
                                                    float x = (Float) animation.getAnimatedValue();
                                                    giftLl.setTranslationX(x);
                                                }
                                            });
                                            animator.setDuration(500).start();
                                            animator.addListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {

                                                }
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    giftTimer.start();
                                                }
                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }
                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            });
                                        }
                                    }
                                }, 1000);
                            }
                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }
                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    } else {
                        if (context != null && isShowing()) {
                            giftTimer.start();
                        }
                    }
                }

                @Override
                protected void failed(FailedResult r) {
                    giftTimer.start();
                    super.failed(r);
                }
            });
        }
    }

    private void postNewMessageEvent(String content) {
        etMessage.setText("");
        String uid = AccountManager.getInstance().getUserId();
        LiveAgent.postNewEvent(uid, liveData.liveId, content, new SimpleJsonResponseHandler() {
            @Override
            public void success() {
                getEvents(0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_exit:
                activity.finish();
                break;
            case R.id.iv_message:
                if (!AccountManager.getInstance().isLogin()) {
                    AccountManager.getInstance().gotoLogin(activity);
                    return;
                }
                etMessage.requestFocus();
                showInputMethod();
                etMessage.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        showInputMethod();
                        llInput.setVisibility(View.VISIBLE);
                        llEvent.setVisibility(View.GONE);
//                        llInput.setVisibility(View.VISIBLE);
//                        centerView.setVisibility(View.GONE);
                        top.setVisibility(View.GONE);
                        inputIsShow = true;
                    }
                }, 100);

                break;
            case R.id.iv_share:
                gotoShare();
                break;
            case R.id.bt_dis_detail_pub:
                break;
            case R.id.root:
                if (inputIsShow) {
                    inputIsShow = false;
                    centerView.setVisibility(View.VISIBLE);
                    top.setVisibility(View.VISIBLE);
                    closeInputMethod();
                    etMessage.setText("");
                    llInput.setVisibility(View.GONE);
                    llEvent.setVisibility(View.VISIBLE);
                }
                if (giftIsShow) {
                    giftIsShow = false;
                    rlGift.setVisibility(View.GONE);
                    llEvent.setVisibility(View.VISIBLE);
                    lvEvent.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_gift:
                if (!AccountManager.getInstance().isLogin()) {
                    AccountManager.getInstance().gotoLogin(activity);
                    return;
                }
                giftIsShow = true;
                llEvent.setVisibility(View.GONE);
                rlGift.setVisibility(View.VISIBLE);
                lvEvent.setVisibility(View.GONE);
                getUserMoney();
                break;
            case R.id.bu_send_gift:
                if (money < Integer.parseInt(chooseGift.emoney)) {
                    LToast.showToast("狗粮不足，请去充值");
                } else {
                    sendGift();
                }
                break;
            case R.id.tv_chongzhi:
                Intent intent = new Intent(context, PayMoneyActivity.class);
                context.startActivity(intent);
                break;
            case R.id.iv_photo:
                Intent userintent = new Intent(context, UserHomeActivity.class);
                userintent.putExtra(BundleParamKey.UID, liveData.live_uid);
                context.startActivity(userintent);
                break;
            case R.id.iv_love:
                heartLayout.addFavor();
                break;
        }
    }

    private void sendGift() {
        LiveAgent.giveGift(chooseGift.id, liveData.live_uid, liveData.liveId, new SimpleJsonResponseHandler() {
            @Override
            public void success() {
                getUserMoney();
            }

            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
            }
        });
    }

    private void gotoShare() {
        String title;
        String content;
        String shareurl;
        String picurl;
        if (liveData.share != null) {
            title = liveData.share.title;
            content = liveData.share.desc;
            shareurl = liveData.share.url;
            picurl = liveData.share.pic != null ? liveData.share.pic : "";
            if (TextUtils.isEmpty(title)) {
                title = "分享来自刘文静的直播";
            }
            if (TextUtils.isEmpty(content)) {
//                                    content = "来"+getString(R.string.app_name)+"看直播吧";
                content = liveData.nickname + "正在直播";
            }
            new ShareActivity.Builder()
                    .context(context)
                    .title(title)
                    .shareWenyouType(ShareActivity.SHARE_WENYOU_FOR_LIVE)
                    .username(liveData.author)
                    .content(content)
                    .url(TextUtils.isEmpty(shareurl) ? URLDefine.SHARE_URL : shareurl)
                    .imgurl(picurl)
                    .liveId((int) liveData.liveId)
                    .buildAndShow();
        }
    }

    public void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            if (etMessage != null) {
                imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
            }

        }
    }

    public void showInputMethod() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
        if (etMessage != null) {
            imm.showSoftInput(etMessage, InputMethodManager.SHOW_FORCED);
        }

    }

    public void showTop() {
        if (inputIsShow) {
            inputIsShow = false;
            centerView.setVisibility(View.VISIBLE);
            top.setVisibility(View.VISIBLE);
            closeInputMethod();
            etMessage.setText("");
            llInput.setVisibility(View.GONE);
            llEvent.setVisibility(View.VISIBLE);
        }
    }


    public void onEventMainThread(PayEvent event) {
        getUserMoney();
    }

    private void getUserMoney() {
        MoneyAgent.getUserMoney(new AmbJsonResponseHandler<MoneyBean>() {
            @Override
            public void success(@Nullable MoneyBean data) {
                tvChongzhi.setText("充值:" + Integer.toString(data.money) + " >");
                money = data.money;
            }
        });
    }

    private void showGifts() {
        int page = 0;
        if (gifts.size() % 8 == 0) {
            page = gifts.size() / 8;
        } else {
            page = gifts.size() / 8 + 1;
        }
        for (int i = 0; i < page; i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.gift_page, null);
            GridView gridView = (GridView) view.findViewById(R.id.gv_gift);
            ImageView point = new ImageView(context);
            point.setImageResource(R.drawable.gray_point);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(DensityUtil.dip2px(context, 5f), 0, 0, 0);
            point.setLayoutParams(lp);
            llPage.addView(point);
            final List<GiftObject.Gift> pageli = new ArrayList<>();
            for (int j = i * 8; j < i * 8 + 8; j++) {
                if (j <= gifts.size() - 1) {
                    pageli.add(gifts.get(j));
                } else {
                    pageli.add(null);
                }
            }
            GiftGridAdapter giftGridAdapter = new GiftGridAdapter(context);
            giftGridAdapter.setGiftList(pageli);
            gridView.setAdapter(giftGridAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (pageli.get(position) != null) {
                        chooseGift = pageli.get(position);
                        setChooseOrClear();
                        sendGift.setClickable(true);
                        sendGift.setBackgroundResource(R.drawable.bg_send_touch);
                    }
                }
            });
            viewPagerList.add(view);
            giftGridAdapterList.add(giftGridAdapter);
        }
        updateGiftPoint(0);
        giftPageAdapter = new GiftPageAdapter();
        giftPageAdapter.setmListViews(viewPagerList);
        vpGift.setAdapter(giftPageAdapter);
    }

    private void setChooseOrClear() {
        for (GiftGridAdapter adapter : giftGridAdapterList) {
            adapter.setChooseorClear(chooseGift.id);
        }
    }
}
