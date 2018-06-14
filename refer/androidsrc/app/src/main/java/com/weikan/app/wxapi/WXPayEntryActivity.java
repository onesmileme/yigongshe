
package com.weikan.app.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weikan.app.Constants;
import com.weikan.app.R;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.wxapi.WechatPaymentEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by wutong on 1/1/16.
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, Constants.WECHAT_PAY_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {

                finish();
                EventBus.getDefault().post(new WechatPaymentEvent(0));
            } else if (resp.errCode == -2) {

                finish();
                EventBus.getDefault().post(new WechatPaymentEvent(1));
            } else {
                finish();
                EventBus.getDefault().post(new WechatPaymentEvent(1));
            }
        }
    }
}

