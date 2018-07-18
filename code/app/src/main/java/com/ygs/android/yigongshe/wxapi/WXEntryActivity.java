package com.ygs.android.yigongshe.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * Created by ruichao on 2018/7/18.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
  private IWXAPI api;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    handleIntent(getIntent());
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    handleIntent(intent);
  }

  private void handleIntent(Intent intent) {
    SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
    String result = "";
    switch (resp.errCode) {
      case BaseResp.ErrCode.ERR_OK:
        result = "发送成功";
        break;
      case BaseResp.ErrCode.ERR_USER_CANCEL:
        result = "发送取消";
        break;
      case BaseResp.ErrCode.ERR_AUTH_DENIED:
        result = "发送被拒绝";
        break;
      default:
        result = "发送返回";
        break;
    }

    Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    finish();
  }

  //sendReq是第三方app主动发送消息给微信，发送完成之后会切回到第三方app界面。
  @Override public void onReq(BaseReq baseReq) {
    int type = baseReq.getType();
  }

  @Override public void onResp(BaseResp baseResp) {
    String result = "";
    switch (baseResp.errCode) {
      case BaseResp.ErrCode.ERR_OK:
        result = "发送成功";
        break;
      case BaseResp.ErrCode.ERR_USER_CANCEL:
        result = "发送取消";
        break;
      case BaseResp.ErrCode.ERR_AUTH_DENIED:
        result = "发送被拒绝";
        break;
      default:
        result = "发送返回";
        break;
    }
    if (BaseResp.ErrCode.ERR_OK != baseResp.errCode) {
      Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }
    finish();
  }
}
