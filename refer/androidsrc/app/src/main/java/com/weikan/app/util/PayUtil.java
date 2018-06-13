package com.weikan.app.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weikan.app.Constants;
import com.weikan.app.MainApplication;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.bean.PrepayObject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import platform.http.HttpUtils;
import platform.http.result.ErrNoFailedResult;

/**
 * Created by ylp on 2016/10/11.
 */

public class PayUtil {


    private IWXAPI api;

//    private AsyncHttpResponseHandler prepayHandler = new TextHttpResponseHandler() {
//        @Override
//        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//            Toast.makeText(OriginalMyAdmireActivity.this, "微信支付失败。", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onSuccess(int i, Header[] headers, String s) {
//            if (s == null || s.length() == 0) {
//                Toast.makeText(OriginalMyAdmireActivity.this, "微信支付失败。", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            PrepayObject result = null;
//            try {
//                result = JSONObject.parseObject(s, PrepayObject.class);
//            } catch (com.alibaba.fastjson.JSONException ex) {
//                ex.printStackTrace();
//            }
//            if (result == null || result.errno != 0 || result.data == null) {
//                if (result != null && result.msg != null && result.msg.length() > 0) {
//                    Toast.makeText(OriginalMyAdmireActivity.this, result.msg, Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//            if (api == null) {
//                api = WXAPIFactory.createWXAPI(OriginalMyAdmireActivity.this, Constants.WECHAT_PAY_APP_ID);
//                if (!api.registerApp(Constants.WECHAT_PAY_APP_ID)) {
//                    Toast.makeText(OriginalMyAdmireActivity.this, "无法支付", Toast.LENGTH_SHORT).show();
//                }
//            }
//            PayReq request = new PayReq();
//            //request.appId = Constants.WECHAT_APP_ID;
//            request.appId = result.data.content.appid;
//            request.partnerId = result.data.content.out_trade_no;
//            request.prepayId = result.data.content.prepay_id;
//            request.nonceStr = result.data.content.nonce_str;
//            request.timeStamp = result.data.content.timeStamp;
//            request.packageValue = "Sign=WXPay";
//            String sign = "appid=" + request.appId
//                    + "&noncestr=" + request.nonceStr
//                    + "&package=" + request.packageValue
//                    + "&partnerid=" + request.partnerId
//                    + "&prepayid=" + request.prepayId
//                    + "&timestamp=" + request.timeStamp
//                    + "&key=" + result.data.content.key;
//            request.sign = MD5Util.encode(sign).toUpperCase();
//            if (api.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
//                Toast.makeText(OriginalMyAdmireActivity.this, "无法支付", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (!api.sendReq(request)) {
//                Toast.makeText(OriginalMyAdmireActivity.this, "无法支付", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_original_myadmire);
//        // 标题栏
//        findViewById(R.id.iv_titlebar_back).setOnClickListener(this);
//        ((TextView) findViewById(R.id.tv_titlebar_title)).setText("我要赞赏");
//        // 主体页面
//        findViewById(R.id.original_myadmire_20_button).setOnClickListener(this);
//        findViewById(R.id.original_myadmire_50_button).setOnClickListener(this);
//        findViewById(R.id.original_myadmire_100_button).setOnClickListener(this);
//        findViewById(R.id.original_myadmire_200_button).setOnClickListener(this);
//        tid = getIntent().getStringExtra("tid");
//        Intent intent = getIntent();
//        if (intent != null) {
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                if (bundle.containsKey("oa_nick_name")) {
//                    oaNickName = bundle.getString("oa_nick_name");
//                }
//            }
//        }
//        EventBus.getDefault().register(this);
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.iv_titlebar_back: {
//                finish();
//                break;
//            }
//            case R.id.original_myadmire_20_button: {
//                pay(20);
//                break;
//            }
//            case R.id.original_myadmire_50_button: {
//                pay(50);
//                break;
//            }
//            case R.id.original_myadmire_100_button: {
//                pay(100);
//                break;
//            }
//            case R.id.original_myadmire_200_button: {
//                pay(200);
//                break;
//            }
//            default: {
//                break;
//            }
//        }
//    }
    private PayUtil(){}
    private static PayUtil instance;
    public static PayUtil getInstance(){
        if(instance == null){
            instance = new PayUtil();
        }
        return instance;
    }
    public  void pay(int amount, final BaseActivity activity) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.PAY_UNIFIEDORDER);
        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getDemoUserId());
        params.put("amount", Integer.toString(amount));
        HttpUtils.get(builder.build().toString(), params, new platform.http.responsehandler.AmbJsonResponseHandler<PrepayObject>(){

//            @Override
//            public void success(@Nullable PrepayObject result) {
//                if (result == null || result.errno != 0 || result.data == null) {
//                if (result != null && result.errmsg != null && result.errmsg.length() > 0) {
//                    LToast.showToast(result.errmsg);
//                }else{
//                    LToast.showToast("无法支付");
//                }
//                return;
//            }
//            if (api == null) {
//                api = WXAPIFactory.createWXAPI(context, Constants.WECHAT_PAY_APP_ID);
//                if (!api.registerApp(Constants.WECHAT_PAY_APP_ID)) {
//                    LToast.showToast("无法支付");
//                }
//            }
//            PayReq request = new PayReq();
//            //request.appId = Constants.WECHAT_APP_ID;
//            request.appId = result.data.content.appid;
//            request.partnerId = result.data.content.out_trade_no;
//            request.prepayId = result.data.content.prepay_id;
//            request.nonceStr = result.data.content.nonce_str;
//            request.timeStamp = result.data.content.timeStamp;
//            request.packageValue = "Sign=WXPay";
//            String sign = "appid=" + request.appId
//                    + "&noncestr=" + request.nonceStr
//                    + "&package=" + request.packageValue
//                    + "&partnerid=" + request.partnerId
//                    + "&prepayid=" + request.prepayId
//                    + "&timestamp=" + request.timeStamp
//                    + "&key=" + result.data.content.key;
//            request.sign = MD5Util.encode(sign).toUpperCase();
//            if (api.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
//                LToast.showToast("无法支付");
//                return;
//            }
//            if (!api.sendReq(request)) {
//                LToast.showToast("无法支付");
//                return;
//            }
//        }

            @Override
            public void success(@Nullable PrepayObject data) {
                activity.hideLoadingDialog();
                if(data == null || data.content == null){
                    LToast.showToast("无法支付");
                    return;
                }
                if (api == null) {
                api = WXAPIFactory.createWXAPI(MainApplication.getInstance().getApplicationContext(), data.content.appid);
                if (!api.registerApp(data.content.appid)) {
                    LToast.showToast("无法支付");
                    return;
                }
            }
            PayReq request = new PayReq();
//            request.appId = Constants.WECHAT_APP_ID;
            request.appId = data.content.appid;
            request.partnerId = data.content.out_trade_no;
            request.prepayId = data.content.prepay_id;
            request.nonceStr = data.content.nonce_str;
            request.timeStamp = data.content.timeStamp;
            request.packageValue = "Sign=WXPay";
            String sign = "appid=" + request.appId
                    + "&noncestr=" + request.nonceStr
                    + "&package=" + request.packageValue
                    + "&partnerid=" + request.partnerId
                    + "&prepayid=" + request.prepayId
                    + "&timestamp=" + request.timeStamp
                    + "&key=" + data.content.key;
            request.sign = MD5Util.encode(sign).toUpperCase();
            if (api.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
                LToast.showToast("无法支付");
                return;
            }
            if (!api.sendReq(request)) {
                LToast.showToast("无法支付");
                return;
            }
            }

            @Override
            protected void errNoFailed(ErrNoFailedResult r) {
                activity.hideLoadingDialog();
                LToast.showToast("无法支付");
                super.errNoFailed(r);
            }
        });
    }

//    public void onEventMainThread(WechatPaymentEvent event) {
//        finish();
//    }
}
