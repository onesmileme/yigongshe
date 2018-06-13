package com.weikan.app.request;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.TextureView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.weikan.app.Constants;
import com.weikan.app.MainApplication;
import com.weikan.app.account.AccountManager;
import com.weikan.app.request.bean.TopicItem;
import com.weikan.app.request.bean.TopicObject;
import com.weikan.app.util.SharePrefsUtils;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.bean.WenyouListData;
import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;
import platform.http.responsehandler.ConfusedJsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import platform.http.result.FailedResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 独立的后台任务,比较挫，先这么用着
 * Created by liujian on 16/7/30.
 */
public class BackgroundTaskAgent {

    /**
     * 更新热门话题列表并存储
     */
    public static void updateTopic() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.TOPIC_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());

        HttpUtils.get(builder.build().toString(), params, new ConfusedJsonResponseHandler<TopicObject>() {
            @Override
            public void success(@Nullable TopicObject data) {
                if(data!=null && data.data!=null && data.data.size()>0) {
                    new SharePrefsUtils(MainApplication.getInstance(), Constants.PREF_COMMON_NAME)
                            .getEditor().putString(Constants.PREF_TOPIC, JSONArray.toJSONString(data.data)).commit();
                } else {
                    new SharePrefsUtils(MainApplication.getInstance(), Constants.PREF_COMMON_NAME)
                            .getEditor().putString(Constants.PREF_TOPIC, "").commit();
                }
            }

            @Override
            protected void failed(FailedResult r) {
                r.setIsHandled(true);
            }
        });
    }

    public static List<TopicItem> getTopicData(){
        List<TopicItem> ret  = null;
        String s = new SharePrefsUtils(MainApplication.getInstance(), Constants.PREF_COMMON_NAME).getString(Constants.PREF_TOPIC,"");
        if(!TextUtils.isEmpty(s)){
            try {
                ret = JSONArray.parseArray(s, TopicItem.class);
            } catch (JSONException e){

            }
        }
        return ret;
    }
}
