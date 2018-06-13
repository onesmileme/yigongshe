package com.weikan.app.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.weikan.app.Constants;
import com.weikan.app.MainApplication;
import com.weikan.app.R;
import com.weikan.app.ShareActivity;
import com.weikan.app.ShareListener;
import com.weikan.app.account.AccountManager;
import com.weikan.app.bean.ShareResultEvent;
import com.weikan.app.original.bean.ImageNtsObject;
import com.weikan.app.original.bean.UploadImageObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import platform.http.result.FailedResult;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/1/25
 * Time: 23:20
 */
public class ShareTools {

    public UMShareAPI umShareAPI;

    private String tid;
    private String text;
    private static  ShareTools inst = null;
    public static ShareTools getInstance(){
        if(inst==null){
            inst = new ShareTools();
        }
        return inst;
    }

    public void shareLive(Context context, String title, String desc, String url, String imgUrl){
        Intent intent = new Intent();
        intent.putExtra("title", title);
        intent.putExtra("content", TextUtils.isEmpty(desc)? title: desc);
        intent.putExtra("url", TextUtils.isEmpty(url) ? URLDefine.SHARE_URL : url);
        intent.putExtra("imgurl", imgUrl);
        intent.setClass(context, ShareActivity.class);
        context.startActivity(intent);
    }

    public void share(Context context,String title, String name, String content, String url, String imgurl,long liveId){
        Intent intent = new Intent();
        intent.putExtra("title", title);
        intent.putExtra("username", name);
        intent.putExtra("content", content);
        intent.putExtra("url", TextUtils.isEmpty(url) ? URLDefine.SHARE_URL : url);
        intent.putExtra("imgurl", imgurl);
        intent.setClass(context, ShareActivity.class);
        intent.putExtra("liveId",liveId);
        context.startActivity(intent);
    }

    public void init(Context context){
        umShareAPI = UMShareAPI.get(MainApplication.getInstance());
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("正在加载...");
//        ProgressDialog wxpd = new ProgressDialog(context);
//        wxpd.setMessage("正在调起微信...");
        Config.IsToastTip = false;
        Config.dialog = pd;
//        Config.wxdialog = wxpd;
//        Config.REDIRECT_URL = "您新浪后台的回调地址";

        mShareListener = new UMShareListener() {

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                LToast.showToast("分享成功");
                EventBus.getDefault().post(new ShareResultEvent(true,tid));
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//                EventBus.getDefault().post(new ShareResultEvent(false,tid));
                LToast.showToast("分享失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                LToast.showToast("取消分享");
            }
        };
        initSocialSDK();
    }


    /**
     * 初始化SDK，添加一些平台
     */
    private void initSocialSDK() {

        PlatformConfig.setWeixin(Constants.WECHAT_APP_ID, Constants.WECHAT_SECRET_KEY);
//        PlatformConfig.setSinaWeibo("885356157", "2dfb7a404ffc718672d7e6a45055ac94");
        PlatformConfig.setSinaWeibo(Constants.WEIBO_APP_ID, Constants.WEIBO_SECRET_KEY);
        PlatformConfig.setQQZone(Constants.QQ_APP_ID, Constants.QQ_APP_KEY);

    }

    public void shareWeixin(Activity activity,String title,String content,String url,String imgurl,String tid){
        //设置微信好友分享内容
        ShareContent weixinContent = new ShareContent();
//设置分享文字
        weixinContent.mText = content;
//设置title
        weixinContent.mTitle = title;
//设置分享内容跳转URL
        weixinContent.mTargetUrl = url;
        if(TextUtils.isEmpty(imgurl)) {
            weixinContent.mMedia = new UMImage(activity, R.drawable.ic_launcher);
        } else {
            weixinContent.mMedia = new UMImage(activity, imgurl);
        }
        this.tid = tid;

        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN)
                .setShareContent(weixinContent)
                .setCallback(mShareListener)
                .share();
    }

    public void shareQQ(Activity activity,String title,String content,String url,String tid,String imgurl){
        ShareContent weixinContent = new ShareContent();
//设置分享文字
        weixinContent.mText = content;
//设置title
        weixinContent.mTitle = title;
//设置分享内容跳转URL
        weixinContent.mTargetUrl = url;
        if(TextUtils.isEmpty(imgurl)) {
            weixinContent.mMedia = new UMImage(activity, R.drawable.ic_launcher);
        } else {
            weixinContent.mMedia = new UMImage(activity, imgurl);
        }
        this.tid = tid;

        new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ)
                .setShareContent(weixinContent)
                .setCallback(mShareListener)
                .share();
    }

    public void shareSina(Activity activity,String title,String content,String url,String tid, String imgurl){
        ShareContent weixinContent = new ShareContent();
//设置分享文字
        weixinContent.mText = content;
//设置title
//        weixinContent.mTitle = title;
//设置分享内容跳转URL
        weixinContent.mTargetUrl = url;
//        if(!TextUtils.isEmpty(imgurl)) {
//            weixinContent.mMedia = new UMImage(activity, imgurl);
//        }
        this.tid = tid;

        new ShareAction(activity).setPlatform(SHARE_MEDIA.SINA)
                .setShareContent(weixinContent)
                .setCallback(mShareListener)
                .share();
    }
    public void shareCollect(final String tid, boolean status, final ShareListener shareListener) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        if(status){
            builder.encodedPath(URLDefine.COLLECTION_CANCLE);
        }else{
            builder.encodedPath(URLDefine.COLLECTION_ADD);
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        params.put("last_cid", Integer.toString(0));
        HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {
            @Override
            public void success() {
                shareListener.onSuccess();
            }

            @Override
            protected void failed(FailedResult r) {
                super.failed(r);
                shareListener.onFail();
            }
        });
    }
    public void shareTip(final String tid,  final ShareListener shareListener) {

    }
    private ShareListener shareListener;
    public void shareWenYouQuanForLive(final String path, String text, final ShareListener shareListener) {
        this.text = text;
        this.shareListener = shareListener;
        new Thread(){
            @Override
            public void run() {
                try {
                URL url = null;
                    url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10 * 1000);
                conn.setConnectTimeout(10 * 1000);
                conn.setRequestMethod("GET");
                InputStream is = null;
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    is = conn.getInputStream();
                } else {
                    is = null;
                }
                if (is == null){
                    throw new RuntimeException("stream is null");
                } else {
                    try {
                        byte[] bs = new byte[1024];
                        OutputStream os = new FileOutputStream(Environment.getExternalStorageDirectory() +"/share.jpg");
                        // 开始读取
                        int len;
                        while ((len = is.read(bs)) != -1) {
                            os.write(bs, 0, len);
                        }
                        sendUploadImgRequest(Environment.getExternalStorageDirectory()+"/share.jpg");
                    } catch (Exception e) {
                        shareListener.onFail();
                        e.printStackTrace();
                    }
                    is.close();
                }
                } catch (Exception e) {
                    shareListener.onFail();
                    e.printStackTrace();
                }
            }

        }.start();
    }

    public void sendUploadImgRequest(@NonNull String path) {
        File imageFile = new File(path);
        photo = path;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.UPLOAD_TWEET_PIC);

        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());

        Map<String, File> fileParams = new HashMap<>();
        fileParams.put("file", imageFile);


       ImgHttpResponseHandler uploadImgHandler = new ImgHttpResponseHandler();
        uploadImgHandler.setOriginalPath(path);

        HttpUtils.multipartPost(builder.build().toString(), params, fileParams, uploadImgHandler);
    }
    Map<String, String> pathMap = new HashMap<>();
   UploadStatusManager uploadStatusManager = new UploadStatusManager();
    private class ImgHttpResponseHandler extends JsonResponseHandler<UploadImageObject> {
        private String originalPath;
        @Override
        public void success(@NonNull UploadImageObject data) {
            String url = JSONObject.toJSONString(data.img);
            pathMap.put(originalPath, url);
            uploadStatusManager.put(originalPath,UploadStatus.Succeed);
        }

        @Override
        protected void failed(FailedResult r) {
            super.failed(r);
            r.setIsHandled(true);
            uploadStatusManager.put(originalPath, UploadStatus.Failed);
        }

        public void setOriginalPath(String originalPath) {
            this.originalPath = originalPath;
        }


    }
    private enum UploadStatus {
        None, ING, Failed, Succeed
    }
    private class UploadStatusManager {
        Map<String, UploadStatus> uploadStatusMap = new HashMap<>();

        public UploadStatus get(String photo) {
            return uploadStatusMap.get(photo);
        }

        public void put(String photo, UploadStatus status) {
            uploadStatusMap.put(photo, status);

            // 只要有一个失败了，就通知失败
            if (status == UploadStatus.Failed) {

                return;
            }

            // 全部成功，才算通知成功
            boolean allSucceed = true;
            if (status == UploadStatus.Succeed) {
                for (UploadStatus s: uploadStatusMap.values()) {
                    if (s != UploadStatus.Succeed) {
                        allSucceed = false;
                        break;
                    }
                }
            } else {
                allSucceed = false;
            }
            if (allSucceed) {
                sendRequest( text);
            }
        }
    }
    /*
         * 得到图片字节流 数组大小
         * */
    public void shareCircle(Activity activity,String title,String content,String url,String tid,String imgurl){
        ShareContent weixinContent = new ShareContent();
//设置分享文字
        weixinContent.mText = title;
//设置title
        weixinContent.mTitle = title;
//设置分享内容跳转URL
        weixinContent.mTargetUrl = url;
        if(TextUtils.isEmpty(imgurl)) {
            weixinContent.mMedia = new UMImage(activity, R.drawable.ic_launcher);
        } else {
            weixinContent.mMedia = new UMImage(activity, imgurl);
        }
        this.tid = tid;

        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareContent(weixinContent)
                .setCallback(mShareListener)
                .share();
    }

    /**
     * 分享监听器
     */
    UMShareListener mShareListener ;

    /**
     * 微信登陆，流程判断如下：
     * 1. 已授权，直接获取个人相关信息，用户端不会调起微信客户端界面
     * 2. 未授权，先调起微信客户端授权，后获取个人信息，如果获取失败，也算登陆失败
     * @param mContext
     * @param listener  封装了listener，获取成功失败的结果
     */
    public void LoginQQ(final Activity mContext, final OnThirdLoginListener listener){
//        if(OauthHelper.isAuthenticatedAndTokenNotExpired(mContext, SHARE_MEDIA.QQ) ){
//            //获取相关授权信息
//            getPlatformInfo(mContext, listener);
//        } else {

        LToast.showToast("开始获取授权");
            umShareAPI.doOauthVerify(mContext, SHARE_MEDIA.QQ, new UMAuthListener() {
                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
//                    LToast.showToast("授权完成");
//                    saveLoginParam(mContext, "login_type", "qq");
                    saveLoginParam(mContext, "qq_openid", (String) map.get("openid"));
                    //获取相关授权信息
                    getQQPlatformInfo(mContext, listener);
                }

                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                    LToast.showToast("授权错误");
                    if (listener != null) {
                        listener.onLoginFailed();
                    }
                    throwable.printStackTrace();
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {
                    LToast.showDebugToast("授权取消");
                }
            });
//        }
    }

    private void getQQPlatformInfo(final Activity mContext, final OnThirdLoginListener listener){
        LToast.showDebugToast("获取平台数据开始...");
        umShareAPI.getPlatformInfo(mContext, SHARE_MEDIA.QQ, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> info) {
                if ( info != null) {
                    StringBuilder sb = new StringBuilder();
                    Set<String> keys = info.keySet();
                    for (String key : keys) {
                        sb.append(key + "=" + info.get(key).toString() + "\r\n");
                    }
                    LLog.i(sb.toString());
                    saveLoginParam(mContext, "qq_screen_name", (String) info.get("screen_name"));
                    saveLoginParam(mContext, "qq_gender", info.get("gender") + "");
                    saveLoginParam(mContext, "qq_profile_image_url", (String) info.get("profile_image_url"));
                    saveLoginParam(mContext, "qq_city", info.get("city") + "");
                    saveLoginParam(mContext, "qq_province", info.get("province") + "");
                    saveLoginParam(mContext, "qq_access_token", info.get("access_token"));
                    saveLoginParam(mContext, "qq_refresh_token", "");

//                    String openid = getLoginParam(mContext, "openid");

                    // 登陆请求改为上层
//                    AccountManager.getInstance().sendUserLoginRequest(mContext, token[0], (String) info.get("profile_image_url"),
//                            "qq", (String) info.get("screen_name"), openid, "");

                    if (listener != null) {
                        listener.onLoginSuccess();
                    }
                } else {
                    LLog.e("发生错误：" + i);
                    if (listener != null) {
                        listener.onLoginFailed();
                    }
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                LLog.e("发生错误：" + i);
                if (listener != null) {
                    listener.onLoginFailed();
                }
                throwable.printStackTrace();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }
        });
    }

    /**
     * 微信登陆，流程判断如下：
     * 1. 已授权，直接获取个人相关信息，用户端不会调起微信客户端界面
     * 2. 未授权，先调起微信客户端授权，后获取个人信息，如果获取失败，也算登陆失败
     * @param mContext
     * @param listener  封装了listener，获取成功失败的结果
     */
    public void LoginWeixin(final Activity mContext, final OnThirdLoginListener listener){
        if(umShareAPI.isAuthorize(mContext, SHARE_MEDIA.WEIXIN) ){
            //获取相关授权信息
            getPlatformInfo(mContext, listener);
        } else {
            umShareAPI.doOauthVerify(mContext, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                    //获取相关授权信息
                    getPlatformInfo(mContext, listener);
                }

                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                    if (listener != null) {
                        listener.onLoginFailed();
                    }
                    throwable.printStackTrace();
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {
                    if (listener != null) {
                        listener.onLoginFailed();
                    }
                }
            });
        }
    }

    private void getPlatformInfo(final Activity mContext, final OnThirdLoginListener listener){
        umShareAPI.getPlatformInfo(mContext, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> info) {
                if ( info != null) {

                    StringBuilder sb = new StringBuilder();
                    Set<String> keys = info.keySet();
                    for (String key : keys) {
                        sb.append(key + "=" + info.get(key).toString() + "\r\n");
                    }
                    LLog.i(sb.toString());
                    saveLoginParam(mContext, "weixin_openid", (String) info.get("openid"));
                    saveLoginParam(mContext, "weixin_unionid", (String) info.get("unionid"));
                    saveLoginParam(mContext, "weixin_nickname", (String) info.get("screen_name"));
                    saveLoginParam(mContext, "weixin_sex", info.get("gender") + "");
                    saveLoginParam(mContext, "weixin_headimgurl", (String) info.get("profile_image_url"));
                    saveLoginParam(mContext, "weixin_city", (String) info.get("city"));
                    saveLoginParam(mContext, "weixin_province", (String) info.get("province"));
                    saveLoginParam(mContext, "weixin_access_token", info.get("access_token"));
                    saveLoginParam(mContext, "weixin_refresh_token", info.get("refresh_token"));

//                    AccountManager.getInstance().sendUserLoginRequest(mContext, token[0], (String) info.get("headimgurl"),
//                            "weixin", (String) info.get("nickname"), (String) info.get("openid"), (String) info.get("unionid"));

                    if (listener != null) {
                        listener.onLoginSuccess();
                    }
                } else {
                    LLog.e("发生错误：info == null");
                    if (listener != null) {
                        listener.onLoginFailed();
                    }
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                LLog.e("发生错误：" + i);
                if (listener != null) {
                    listener.onLoginFailed();
                }
                throwable.printStackTrace();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                if (listener != null) {
                    listener.onLoginFailed();
                }
            }
        });
    }

    public void exitThirdLogin(final Activity mContext, final OnThirdExitListener listener){
        String platform = getLoginParam(mContext,"login_type");
        SHARE_MEDIA mediatype = SHARE_MEDIA.WEIXIN;
        if("weixin".equals(platform)){
            mediatype = SHARE_MEDIA.WEIXIN;
        } else if("qq".equals(platform)){
            mediatype = SHARE_MEDIA.QQ;
        }
        umShareAPI.deleteOauth(mContext, mediatype, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                if (listener != null) {
                    listener.onExitFinish();
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }

        });
    }

    public void saveLoginParam(Context context, String key, String value){
        SharedPreferences sp = context.getSharedPreferences(PrefDefine.PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getLoginParam(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(PrefDefine.PREF_FILE, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public interface OnThirdLoginListener{
        void onLoginSuccess();
        void onLoginFailed();
    }

    public interface OnThirdExitListener{
        void onExitFinish();
    }

    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }
    String photo = null;
    private void sendRequest(String desc) {
        ArrayList<ImageNtsObject> pics = new ArrayList<>();
        pics.add(JSONObject.parseObject(pathMap.get(photo), ImageNtsObject.class));
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.WENYOU_PUB);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put("desc", desc);
        if(pics.size()>0) {
            params.put("pics", JSONArray.toJSONString(pics));
        }
        params.put("desc", desc);
        HttpUtils.urlEncodedPost(builder.build().toString(), params, new SimpleJsonResponseHandler() {
            @Override
            public void success() {
                shareListener.onSuccess();
            }

            @Override
            public void end() {

            }
        });
    }
}
