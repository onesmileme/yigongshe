package com.weikan.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.socialize.UMShareAPI;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.original.bean.CollectionEvent;
import com.weikan.app.personalcenter.ShareKanbarActivity;
import com.weikan.app.util.Global;
import com.weikan.app.util.LToast;
import com.weikan.app.util.ShareTools;
import com.weikan.app.util.URLDefine;

import de.greenrobot.event.EventBus;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/3/24
 * Time: 13:05
 */
public class ShareActivity extends BaseActivity implements View.OnClickListener {
    public static final int SHARE_WENYOU_FOR_LIVE = 0x1001;
    public static final int SHARE_WENYOU_FOR_NEWS = 0x1002;

    private int shareWenyouType = -1;
    private Bundle shareBundle;

    private String title;
    private String username;
    private String content;
    private String url;
    private String imgurl;
//    private String lanjingType;
    private String tid;
    private boolean isCollect = false;  //是否收藏true已收藏，false未收藏
    private  TextView shareCollect;
    private TextView shareTip;

    public static class Builder{
        private int shareWenyouType = -1;
        private int liveId;

        private String title;
        private String username;
        private String content;
        private String url;
        private String imgurl;
        private String tid;
        private boolean isCollect = false;  //是否收藏true已收藏，false未收藏

        private Context context;

        public Builder shareWenyouType(int type){
            shareWenyouType = type;
            return this;
        }
        public Builder liveId(int id){
            liveId = id;
            return this;
        }
        public Builder title(String t){
            title = t;
            return this;
        }
        public Builder username(String un){
            username = un;
            return this;
        }
        public Builder content(String t){
            content = t;
            return this;
        }
        public Builder url(String u){
            url = u;
            return this;
        }
        public Builder imgurl(String img){
            imgurl = img;
            return this;
        }
        public Builder tid(String t){
            tid = t;
            return this;
        }
        public Builder isCollect(boolean t){
            isCollect = t;
            return this;
        }
        public Builder context(Context c){
            context = c;
            return this;
        }

        public void buildAndShow(){
            Intent intent = new Intent(context, ShareActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("username", username);
            intent.putExtra("content", content);
            intent.putExtra("url", TextUtils.isEmpty(url) ? URLDefine.SHARE_URL : url);
            intent.putExtra("imgurl", imgurl);
            intent.putExtra("liveId",liveId);
            intent.putExtra("isCollect",isCollect);
            intent.putExtra("shareWenyouType",shareWenyouType);
            context.startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus(false);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            shareBundle = bundle;
            title = bundle.getString("title");
            content = bundle.getString("content");
            username = bundle.getString("username");
            url = bundle.getString("url");
            imgurl = bundle.getString("imgurl");
            tid = bundle.getString("tid");
            isCollect = bundle.getBoolean("isCollect");
//            lanjingType = bundle.getString("lanjingType");
            shareWenyouType = bundle.getInt("shareWenyouType");


            if(TextUtils.isEmpty(title)){
                title = "";
            }
            if(TextUtils.isEmpty(content)){
                content = " ";
            }

        } else {
            finish();
        }

//        if(TextUtils.isEmpty(lanjingType)){
//            setContentView(R.layout.activity_share_no_lanjing);
//        } else {
//            setContentView(R.layout.activity_share);
//        }
        setContentView(R.layout.activity_share_no_lanjing);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        findViewById(R.id.tv_share_ciclre).setOnClickListener(this);
        findViewById(R.id.tv_share_weixin).setOnClickListener(this);
        findViewById(R.id.tv_share_sina).setOnClickListener(this);
        if(TextUtils.isEmpty(Constants.WEIBO_APP_ID)){
            findViewById(R.id.tv_share_sina).setVisibility(View.GONE);
        }
        findViewById(R.id.tv_share_qq).setOnClickListener(this);
        if(TextUtils.isEmpty(Constants.QQ_APP_ID)){
            findViewById(R.id.tv_share_qq).setVisibility(View.GONE);
        }
        findViewById(R.id.view).setOnClickListener(this);
        findViewById(R.id.tv_share_kanbar).setOnClickListener(this);
        TextView wenyouquan = (TextView)findViewById(R.id.tv_share_wenyouquan);
        wenyouquan.setText(R.string.tabHost3Title);
        Drawable drawable = getApkIcon();
        if(drawable != null){
            drawable.setBounds(0, 0, Global.dpToPx(ShareActivity.this,30), Global.dpToPx(ShareActivity.this,30));
            wenyouquan.setCompoundDrawables(null, drawable, null, null);//
        }
        wenyouquan.setOnClickListener(this);
        if(shareWenyouType <=0){
            wenyouquan.setVisibility(View.GONE);
        }else{
            wenyouquan.setVisibility(View.VISIBLE);
        }
         shareCollect = (TextView)findViewById(R.id.tv_share_collect);
         shareTip = (TextView)findViewById(R.id.tv_share_tip);
        shareCollect.setOnClickListener(this);
        shareTip.setOnClickListener(this);
        if(!isCollect){
            shareCollect.setText("收藏");
        }else{
            shareCollect.setText("取消收藏");
        }
        if(!TextUtils.isEmpty(tid)){
            shareCollect.setVisibility(View.VISIBLE);
            shareTip.setVisibility(View.GONE);
        }else{
            shareTip.setVisibility(View.GONE);
            shareCollect.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected boolean openTranslucentStatus(){
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_share_kanbar:
                gotoShareKanbarList();
                finish();
                break;
            case R.id.tv_share_ciclre:
                ShareTools.getInstance().shareCircle(this,title,content,url,tid,imgurl);
                finish();
                break;
            case R.id.tv_share_weixin:
                ShareTools.getInstance().shareWeixin(this,title,content,url,imgurl,tid);
                finish();
                break;
            case R.id.tv_share_qq:
                ShareTools.getInstance().shareQQ(this,title,content,url,tid,imgurl);
//                finish();
                break;
            case R.id.tv_share_sina:
                // sso要回调后在finish
                ShareTools.getInstance().shareSina(this,title,content,url,tid, imgurl);
                break;
            case R.id.view:
            	finish();
            	break;
            case R.id.tv_share_wenyouquan:
                if(!AccountManager.getInstance().isLogin()){
                    AccountManager.getInstance().gotoLogin(this);
                    return;
                }
                if(shareWenyouType == SHARE_WENYOU_FOR_LIVE) {
                    int liveId = shareBundle.getInt("liveId");
                    showLoadingDialog();
                    ShareTools.getInstance().shareWenYouQuanForLive(imgurl, username + "正在直播，<a href='appfac://live_play?liveid=" + Long.toString(liveId) + "'>快来围观>></a>", new ShareListener() {
                        @Override
                        public void onSuccess() {
                            hideLoadingDialog();
                            LToast.showToast("分享成功");
                            finish();
                        }

                        @Override
                        public void onFail() {
                            hideLoadingDialog();
                            LToast.showToast("分享失败");
                        }
                    });
                } else if (shareWenyouType == SHARE_WENYOU_FOR_NEWS){
                    Intent intent = new Intent(this,ShareToCircleActivity.class);
                    intent.putExtra("articleId",tid);
                    intent.putExtra("imgUrl",imgurl);
                    intent.putExtra("shareTitle",content);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.tv_share_collect:
                if(!AccountManager.getInstance().isLogin()){
                    AccountManager.getInstance().gotoLogin(this);
                    return;
                }
                showLoadingDialog();
                ShareTools.getInstance().shareCollect(tid, isCollect, new ShareListener() {
                    @Override
                    public void onSuccess() {

                        hideLoadingDialog();
                        if(!isCollect){
                            LToast.showToast("收藏成功");
                            shareCollect.setText("取消收藏");
                        }else{
                            LToast.showToast("取消收藏成功");
                            shareCollect.setText("收藏");
                        }
                        isCollect = !isCollect;
                        CollectionEvent event = new CollectionEvent();
                        event.flag = isCollect;
                        EventBus.getDefault().post(event);
                        finish();
                    }

                    @Override
                    public void onFail() {
                        hideLoadingDialog();
                        if(!isCollect){
                            LToast.showToast("收藏失败");
                        }else{
                            LToast.showToast("取消收藏失败");
                        }
                    }
                });
                break;
            case R.id.tv_share_tip:
                showLoadingDialog();
                ShareTools.getInstance().shareTip(tid, new ShareListener() {
                    @Override
                    public void onSuccess() {
                        hideLoadingDialog();
                        LToast.showToast("举报成功");
                    }

                    @Override
                    public void onFail() {
                        hideLoadingDialog();
                        LToast.showToast("举报失败");
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        finish();
    }

    private void gotoShareKanbarList(){
        Intent intent = new Intent(this, ShareKanbarActivity.class);
        intent.putExtras(getIntent().getExtras());
        startActivity(intent);
    }
    public  Drawable getApkIcon() {
        PackageInfo info;
        try {
            info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            // 当前版本的包名
            String packageNames = info.packageName;
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(packageNames, 0);
            return applicationInfo.loadIcon(getPackageManager());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
