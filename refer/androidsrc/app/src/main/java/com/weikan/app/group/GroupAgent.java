package com.weikan.app.group;

import android.net.Uri;

import com.weikan.app.account.AccountManager;
import com.weikan.app.group.bean.GroupListBean;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.bean.WenyouListData;

import java.util.HashMap;
import java.util.Map;

import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;
import platform.http.responsehandler.ResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;

/**
 * Created by Lee on 2017/01/09.
 */
public class GroupAgent {

    private static void groupHttp(String path, Map<String, String> params, ResponseHandler handler) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(path);
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        HttpUtils.get(builder.build().toString(), params, handler);
    }

    /**
     * 群组-添加关注
     */
    public static void groupFollow(String groupId, SimpleJsonResponseHandler handler) {
        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.GROUP_ID, groupId);
        groupHttp(URLDefine.GROUP_FOLLOW, params, handler);
    }

    /**
     * 群组-取消关注
     */
    public static void cancelGroupFollow(String groupId, SimpleJsonResponseHandler handler) {
        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.GROUP_ID, groupId);
        groupHttp(URLDefine.CANCEL_GROUP_FOLLOW, params, handler);
    }

    /**
     * 群组-获取全部群组
     */
    public static void allGroupList(int page, AmbJsonResponseHandler<GroupListBean> handler) {
        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.PAGE, page + "");
        groupHttp(URLDefine.ALL_GROUPS_LIST, params, handler);
    }

    /**
     * 群组-选择群组列表
     */
    public static void chooseGroupList(int page, AmbJsonResponseHandler<GroupListBean> handler) {
        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.PAGE, page + "");
        groupHttp(URLDefine.PUB_ARTICLE_GROUPS, params, handler);
    }

    /**
     * 群组-关注群组列表
     */
    public static void followGroupList(AmbJsonResponseHandler<GroupListBean> handler) {
        Map<String, String> params = new HashMap<>();
        groupHttp(URLDefine.USER_FOLLOW_GROUPS, params, handler);
    }

    /**
     * 群组-关注群组列表-下拉加载更多
     */
    public static void followGroupListMore(String lastTime, AmbJsonResponseHandler<GroupListBean> handler) {
        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.LAST_TIME, lastTime);
        groupHttp(URLDefine.USER_FOLLOW_GROUPS, params, handler);
    }

    /**
     * 群组-详情页文章列表
     */
    public static void groupArticleList(String groupId, AmbJsonResponseHandler<WenyouListData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.GROUP_ID, groupId);
        groupHttp(URLDefine.GROUP_ARTICLE_LIST, params, handler);
    }

    /**
     * 群组-详情页文章列表-下拉加载更多
     */
    public static void groupArticleListMore(String groupId, String lastTime, AmbJsonResponseHandler<WenyouListData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put(URLDefine.GROUP_ID, groupId);
        params.put(URLDefine.LAST_TIME, lastTime);
        groupHttp(URLDefine.GROUP_ARTICLE_LIST, params, handler);
    }


}
