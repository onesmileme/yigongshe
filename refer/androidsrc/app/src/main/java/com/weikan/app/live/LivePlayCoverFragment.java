package com.weikan.app.live;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.ShareActivity;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseFragment;
import com.weikan.app.common.adater.BaseRecyclerAdapter;
import com.weikan.app.common.net.JsonArrayResponseHandler;
import com.weikan.app.common.net.Page;
import com.weikan.app.common.util.TickTimer;
import com.weikan.app.common.widget.BaseListItemView;
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
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.URLDefine;
import com.weikan.app.view.GiftView;
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
 * Created by liujian on 16/8/31.
 */
public class LivePlayCoverFragment extends BaseFragment implements View.OnClickListener{
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
   private  Page page = null;
    private TickTimer tickTimer= null;
    private TickTimer giftTimer= null;
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
    private RelativeLayout root;
    private View view;
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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();

        EventBus.getDefault().register(LivePlayCoverFragment.this);
        onlineUserListAdapter = new OnlineUserListAdapter(context);
        liveEventListAdapter = new LiveEventListAdapter(context);
        liveData = (LiveListObject) getArguments().getSerializable("liveData");
        giftGridAdapterList = new ArrayList<>();
        viewPagerList = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragement_live_play_corver, null);
        assignViews(view);
        initView();
        initViewListener();
        getOnlineUsers();
        initTickTimer();
        initGifts();
        return view;
    }
    private void initGifts(){
        LiveAgent.giftList(new JsonArrayResponseHandler<GiftObject.Gift>() {
            @Override
            public void success(@NonNull ArrayList data) {
                gifts = data;
                if(gifts != null && gifts.size() != 0){
                    showGifts();
                }
            }
        });
    }
    private void initViewListener(){
        ivMessage.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivGift.setOnClickListener(this);
        ivLove.setOnClickListener(this);
        sendMessage.setOnClickListener(this);
        ivExit.setOnClickListener(this);
        root.setOnClickListener(this);
        view.setOnClickListener(this);
        tvChongzhi.setOnClickListener(this);
        rvPhoto.setOnClickListener(this);
        onlineUserListAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<OnlineUserObject>() {
            @Override
            public void onItemClick(BaseListItemView<OnlineUserObject> itemView) {
                Intent userintent = new Intent(context, UserHomeActivity.class);
                userintent.putExtra(BundleParamKey.UID,itemView.get().uid);
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
    }
    private void updateGiftPoint(int position){
        for(int i = 0; i < llPage.getChildCount();i++){
            ImageView point = (ImageView)llPage.getChildAt(i);
            if(i == position){
                point.setImageResource(R.drawable.white_point);
            }else{
                point.setImageResource(R.drawable.gray_point);
            }
        }
    }
    private void initTickTimer(){
        tickTimer = new TickTimer(5000, 5000, new Runnable() {

            @Override
            public void run() {
                getOnlineUsers();
                getEvents();
            }
        });
        giftTimer = new TickTimer(1000,1000, new Runnable() {

            @Override
            public void run() {
                getGift();
            }
        });
    }
    private void  assignViews(View view){
        root = (RelativeLayout)view.findViewById(R.id.root);
        tvName = (TextView)view.findViewById(R.id.tv_name);
        tvUserNum = (TextView)view.findViewById(R.id.tv_user_num);
        rvPhoto = (RoundedImageView)view.findViewById(R.id.iv_photo);
        ivExit = (ImageView)view.findViewById(R.id.iv_exit);
        rvOnlineUser = (RecyclerView)view.findViewById(R.id.rv_online_user);
        lvEvent = (ListView) view.findViewById(R.id.lv_event);
        giftLl = (LinearLayout)view.findViewById(R.id.gift) ;
        ivMessage = (ImageView)view.findViewById(R.id.iv_message);
        ivShare = (ImageView)view.findViewById(R.id.iv_share);
        ivGift = (ImageView)view.findViewById(R.id.iv_gift);
        ivLove = (ImageView)view.findViewById(R.id.iv_love);
        llInput = (LinearLayout)view.findViewById(R.id.ll_input);
        llEvent = (LinearLayout)view.findViewById(R.id.ll_event);
        etMessage = (EditText)view.findViewById(R.id.ed_dis_detail) ;
        sendMessage = (Button) view.findViewById(R.id.bt_dis_detail_pub);
        rlGift = (RelativeLayout)view.findViewById(R.id.rl_gift);
        vpGift = (ViewPager)view.findViewById(R.id.vp_gift);
        llPage = (LinearLayout)view.findViewById(R.id.ll_page);
        tvChongzhi = (TextView)view.findViewById(R.id.tv_chongzhi);
        sendGift = (Button)view.findViewById(R.id.bu_send_gift);
        heartLayout = (HeartLayout)view.findViewById(R.id.heart);
        this.view = view.findViewById(R.id.view);
        llEvent.setVisibility(View.VISIBLE);
        llInput.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvOnlineUser.setLayoutManager(layoutManager);
        rvOnlineUser.setAdapter(onlineUserListAdapter);
        sendGift.setOnClickListener(this);
        sendGift.setClickable(false);
        lvEvent.setAdapter(liveEventListAdapter);
        this.view.setVisibility(View.GONE);
    }
    private long eventTime = new Date().getTime();
    private void getEvents() {
        if (isRefreshing) {
            return;
        } else {
            isRefreshing = true;
        }
         LiveEventObject last = liveEventListAdapter.last();
        if(last != null && last.timestamp != 0){
            eventTime = last.timestamp;
        }
        page = Page.NEXT;
        LiveAgent.getEvents(liveData.liveId, page, eventTime, new JsonArrayResponseHandler<LiveEventObject>() {
            @Override
            public void success(@NonNull ArrayList<LiveEventObject> data) {
                if (page == Page.NEW) {
                    liveEventListAdapter.clear();
                }
                liveEventListAdapter.addItems(data);
                liveEventListAdapter.notifyDataSetChanged();
                int count = liveEventListAdapter.getCount();
                lvEvent.setSelection(count - 1);
            }

            @Override
            public void end() {
                super.end();
                isRefreshing = false;
            }

            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
            }
        });
    }
    private void initView(){
        tvName.setText(liveData.nickname);
        tvUserNum.setText(Integer.toString(liveData.online_num));
        ImageLoaderUtil.updateImage(rvPhoto,liveData.live_headimg);
        if(liveData.status == 1){
            ivMessage.setVisibility(View.VISIBLE);
            ivGift.setVisibility(View.VISIBLE);
        }else{
            ivMessage.setVisibility(View.GONE);
            ivGift.setVisibility(View.GONE);
        }
    }
    private void getOnlineUsers() {
        LiveAgent.getOnlineUsers(liveData.liveId,new  JsonResponseHandler<OnlineUserListDataObject>() {

            @Override
            public void success(@NonNull OnlineUserListDataObject data) {
                tvUserNum.setText(data.num+"人");
                onlineUserListAdapter.setItems(data.list);
                onlineUserListAdapter.notifyDataSetChanged();
                int count = liveEventListAdapter.getCount();
                lvEvent.setSelection(count - 1);
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        giftTimer.stop();
        tickTimer.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(liveData.status == 1){
            giftTimer.start();
        }
        tickTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        giftTimer.stop();
        tickTimer.stop();
    }
    private void getGift(){
        giftTimer.stop();
        if(giftList == null || giftList.size()==0){
            if(lasttime == 0L){
                lasttime = new Date().getTime();
            }
        }else{
            lasttime = giftList.get(giftList.size()-1).timestamp;
        }
        if(!isDetached()){
            LiveAgent.getGiftList(liveData.liveId,lasttime,new  JsonArrayResponseHandler<GiftObject>() {
                @Override
                public void success(@NonNull ArrayList<GiftObject> data) {
                    giftList = data;
                    if(context == null){
                        giftTimer.stop();
                        return;
                    }
                    if(data != null && data.size() != 0){
                        int i = 0;
                        giftLl.removeAllViews();
                        for( GiftObject gif : data){
                            View view = GiftView.creatGiftView(context,gif.user.headimgurl,gif.user.nickname,"送了一个"+gif.gift.name,gif.gift.url);
                            if(i == 0){
                                giftLl.addView(view);
                            }else{
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(0, DensityUtil.dip2px(context,10f), 0, 0);
                                view.setLayoutParams(lp);
                                giftLl.addView(view);
                            }
                            i++;
                        }
                        giftLl.invalidate();
                        int start = -DensityUtil.dip2px(context,206f);
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
                                        if(context != null){
                                            int start = 0-DensityUtil.dip2px(context,206f);
                                            ValueAnimator animator = ValueAnimator.ofFloat(0f,start);
                                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                @Override
                                                public void onAnimationUpdate(ValueAnimator animation) {
                                                    float x = (Float) animation.getAnimatedValue();
                                                    giftLl.setTranslationX(x);
                                                }
                                            });

                                            animator.setDuration(500).start();
                                            animator.addListener(new Animator.AnimatorListener(){
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
                                },1000);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }else{
                        if(context != null && !isDetached()){
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
        String uid = AccountManager.getInstance().getUserId();
        LiveAgent.postNewEvent(uid, liveData.liveId, content, new  SimpleJsonResponseHandler() {
            @Override
            public void success() {
                etMessage.setText("");
                getEvents();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_exit:
                getActivity().finish();
                break;
            case R.id.iv_message:
                if(!AccountManager.getInstance().isLogin()){
                    AccountManager.getInstance().gotoLogin(getActivity());
                    return;
                }
                view.setVisibility(View.VISIBLE);
                llEvent.setVisibility(View.GONE);
                llInput.setVisibility(View.VISIBLE);
                etMessage.requestFocus();
                inputIsShow = true;
                showInputMethod();
                break;
            case R.id.iv_share:
                gotoShare();
                break;
            case R.id.bt_dis_detail_pub:
                if(!TextUtils.isEmpty(etMessage.getText().toString())){
                    postNewMessageEvent(etMessage.getText().toString());
                }
                break;
            case R.id.root:
                if(inputIsShow){
                    view.setVisibility(View.GONE);
                    inputIsShow = false;
                    closeInputMethod();
                    etMessage.setText("");
                    llInput.setVisibility(View.GONE);
                    llEvent.setVisibility(View.VISIBLE);
                }
                if(giftIsShow){
                    view.setVisibility(View.GONE);
                    giftIsShow = false;
                    rlGift.setVisibility(View.GONE);
                    llEvent.setVisibility(View.VISIBLE);
                    lvEvent.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.view:
                if(inputIsShow){
                    view.setVisibility(View.GONE);
                    inputIsShow = false;
                    closeInputMethod();
                    etMessage.setText("");
                    llInput.setVisibility(View.GONE);
                    llEvent.setVisibility(View.VISIBLE);
                }
                if(giftIsShow){
                    view.setVisibility(View.GONE);
                    giftIsShow = false;
                    rlGift.setVisibility(View.GONE);
                    llEvent.setVisibility(View.VISIBLE);
                    lvEvent.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_gift:
                if(!AccountManager.getInstance().isLogin()){
                    AccountManager.getInstance().gotoLogin(getActivity());
                    return;
                }
                view.setVisibility(View.VISIBLE);
                giftIsShow = true;
                llEvent.setVisibility(View.INVISIBLE);
                rlGift.setVisibility(View.VISIBLE);
                lvEvent.setVisibility(View.INVISIBLE);
                getUserMoney();
                break;
            case R.id.bu_send_gift:
                sendGift();
                break;
            case R.id.tv_chongzhi:
                Intent intent = new Intent(context,PayMoneyActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_photo:
                Intent userintent = new Intent(context, UserHomeActivity.class);
                userintent.putExtra(BundleParamKey.UID,liveData.live_uid);
                context.startActivity(userintent);
                break;
            case R.id.iv_love:
                heartLayout.addFavor();
                break;
        }
    }
    private void sendGift(){
        LiveAgent.giveGift(chooseGift.id, liveData.live_uid,liveData.liveId, new SimpleJsonResponseHandler() {
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
        if(!AccountManager.getInstance().isLogin()){
            AccountManager.getInstance().gotoLogin(getActivity());
            return;
        }
        String title;
        String content;
        String shareurl;
        String picurl;
        if (liveData.share != null) {
            title = liveData.share.title;
            content = liveData.share.desc;
            shareurl = liveData.share.url;
            picurl = liveData.share.icon != null ? liveData.share.icon : "";
            if (TextUtils.isEmpty(title)) {
                title = "分享来自刘文静的直播";
            }
            if (TextUtils.isEmpty(content)) {
//                                    content = "来"+getString(R.string.app_name)+"看直播吧";
                content = "";
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
    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            if (etMessage != null) {
                imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
            }

        }
    }

    private void showInputMethod() {
        InputMethodManager imm = (InputMethodManager)context. getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
        if (etMessage != null) {
            imm.showSoftInput(etMessage, InputMethodManager.SHOW_FORCED);
        }

    }

    private boolean isShowInput(){
        InputMethodManager imm = (InputMethodManager)context. getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive(etMessage)||imm.isActive(etMessage);
    }
    public void onEventMainThread(PayEvent event) {
        getUserMoney();
    }
    private void getUserMoney(){
        MoneyAgent.getUserMoney(new AmbJsonResponseHandler<MoneyBean>() {
            @Override
            public void success(@Nullable MoneyBean data) {
                tvChongzhi.setText("充值:"+Integer.toString(data.money)+" >");
            }
        });
    }
    private void showGifts(){
        int page = 0;
        if(gifts.size() % 8 == 0){
            page = gifts.size()/8;
        }else{
           page = gifts.size()/8 + 1;
        }
        for(int i = 0 ; i < page ;i++){
            View view = LayoutInflater.from(context).inflate(R.layout.gift_page,null);
            GridView gridView = (GridView)view.findViewById(R.id.gv_gift) ;
            ImageView point = new ImageView(context);
            point.setImageResource(R.drawable.gray_point);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(DensityUtil.dip2px(context,5f), 0, 0, 0);
            point.setLayoutParams(lp);
            llPage.addView(point);
            final List<GiftObject.Gift> pageli = new ArrayList<>();
            for(int j = i*8; j < i*8+8 ; j++){
                if(j <= gifts.size() - 1){
                    pageli.add(gifts.get(j));
                }else{
                    pageli.add(null);
                }
            }
            GiftGridAdapter giftGridAdapter = new GiftGridAdapter(context);
            giftGridAdapter.setGiftList(pageli);
            gridView.setAdapter(giftGridAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(pageli.get(position) != null){
                        chooseGift = pageli.get(position);
                        setChooseOrClear();
                        sendGift.setClickable(true);
                        sendGift.setBackgroundResource(R.drawable.bg_send_click);
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
    private void setChooseOrClear(){
        for(GiftGridAdapter adapter : giftGridAdapterList){
            adapter.setChooseorClear(chooseGift.id);
        }
    }

}
