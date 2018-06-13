package com.weikan.app.news;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.weikan.app.news.bean.CategoryListData;
import com.weikan.app.original.bean.OriginalItem;

import java.util.Map;

import com.weikan.app.original.bean.OriginalItemData;
import com.weikan.app.util.URLDefine;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;

/**
 * 新闻模块关联的网络请求
 *
 * @author kailun on 16/1/8
 */
public class NewsAgent {

    /**
     * 取得新闻列表
     * @param type 类型
     * @param lastTime 之前的内容里最早的时间，Unix时间戳
     * @param refreshType 0表示下拉刷新，1表示上拉加载更多
     * @param handler handler
     */
    public static void getFeedList(int type, long lastTime, int rn, int refreshType,
                                 @NonNull JsonResponseHandler<OriginalItemData> handler) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.MULTI_TWEET_ARTICLE_LIST);

        Map<String, String> params = new ArrayMap<>();
        params.put("type", "" + type);
//        if (lastTime != 0) {
            params.put("last_time", "" + lastTime);
            params.put("rn", "" + rn);
//        }
        params.put("refresh_type", "" + refreshType);

        HttpUtils.get(builder.build().toString(), params, handler);
    }

    public static void getCategoryListConfig(@NonNull JsonResponseHandler<CategoryListData> handler) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.MULTI_TWEET_CATEGORYS);

        Map<String, String> params = new ArrayMap<>();
        HttpUtils.get(builder.build().toString(), params, handler);
    }

//    /**
//     * 收藏
//     * @param aid 新闻、专题、会议、活动id
//     * @param type 收藏类型
//     * @param handler handler
//     */
//    public static void postAddFav(int aid, int type, @NonNull SimpleJsonResponseHandler handler) {
//        String url = NetUtils.httpUrl(PathConsts.ADD_COLLECT);
//
//        Map<String, String> params = new HashMap<>();
//        params.put("type", "" + type);
//        params.put("aid", "" + aid);
//        HttpUtil.newGet(url, params, handler); // 接口给出的是Get
//    }
//
//    /**
//     * 取消收藏
//     * @param aid 新闻、专题、会议、活动id
//     * @param type 收藏类型
//     * @param handler handler
//     */
//    public static void postCancelFav(int aid, int type, @NonNull SimpleJsonResponseHandler handler) {
//        String url = NetUtils.httpUrl(PathConsts.CANCEL_COLLECT);
//
//        Map<String, String> params = new HashMap<>();
//        params.put("type", "" + type);
//        params.put("aid", "" + aid);
//        HttpUtil.newGet(url, params, handler); // 接口给出的是Get
//    }
}
