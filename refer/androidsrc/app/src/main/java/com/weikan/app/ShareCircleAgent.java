package com.weikan.app;

import android.support.annotation.NonNull;

import com.weikan.app.account.AccountManager;
import com.weikan.app.util.URLDefine;

import java.util.HashMap;
import java.util.Map;

import platform.http.HttpUtils;
import platform.http.responsehandler.SimpleJsonResponseHandler;

import static com.weikan.app.common.net.CommonAgent.makeUrl;

/**
 * Created by Lee on 2016/12/25.
 */
public class ShareCircleAgent {
    /**
     * 分享到文友圈
     */
    public static void shareToCircle(String articleId, String content, @NonNull final SimpleJsonResponseHandler handler) {
        String url = makeUrl(URLDefine.SHARE_ARTICLE_MOMENT);
        Map<String, String> params = new HashMap<>();
        params.put("article_id", articleId);
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("content", content);
        HttpUtils.urlEncodedPost(url, params, handler);
    }
}
