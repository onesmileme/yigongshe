package com.weikan.app.live;

import android.support.annotation.NonNull;

import com.weikan.app.account.AccountManager;
import com.weikan.app.live.bean.MoneyBean;
import com.weikan.app.util.URLDefine;

import java.util.HashMap;
import java.util.Map;

import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;

import static com.weikan.app.common.net.CommonAgent.makeUrl;

/**
 * Created by ylp on 2016/11/15.
 */
public class MoneyAgent {
    public static void getUserMoney(@NonNull final AmbJsonResponseHandler<MoneyBean> handler){
        String url = makeUrl(URLDefine.MONEY_USERMONEY);
        Map<String, String> params = new HashMap<>();
        params.put("uid", AccountManager.getInstance().getUserId());
        HttpUtils.get(url, params, handler);
    }
}
