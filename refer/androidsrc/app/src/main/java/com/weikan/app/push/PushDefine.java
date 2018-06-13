package com.weikan.app.push;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/2/3
 * Time: 13:36
 */
public class PushDefine {
    /**
     * 信息流红点
     */
//    public static final int PUSH_TYPE_NEWS_POINT = 1;
//    /**
//     * 社区红点
//     */
//    public static final int PUSH_TYPE_DIS_POINT = 2;
//
//    /**
//     * 消息红点
//     */
//    public static final int PUSH_TYPE_MSG_POINT = 3;
//
//    /**
//     * 我的消息红点
//     */
//    public static final int PUSH_TYPE_MY_MSG = 6;



    public static final int PUSH_TYPE_NOTIFICATION = 1;

    public static final int PUSH_TYPE_ORIGINAL_POINT = 2;

    public static final int PUSH_TYPE_TIME_LIMIT_POINT = 3;

    public static final int PUSH_TYPE_BILL_BOARD_POINT = 4;

    public static final int PUSH_TYPE_NEW_CONSULT_POINT = 5;

    public static final int PUSH_TYPE_TODO_CONSULT_POINT = 6;

    public static final int PUSH_TYPE_NEW_FLASH_SALE_POINT = 7;

    public static final int PUSH_TYPE_TODO_FLASH_SALE_POINT = 8;

    public static final int PUSH_TYPE_CONTRACT_POINT = 9;

    public static final int PUSH_TYPE_ADMIRE_REVENUE_POINT = 10;

    public static final int PUSH_TYPE_ORDER_REVENUE_POINT = 11;

    public static final int PUSH_TYPE_APPRECIATION_POINT = 12;

    public static final int PUSH_TYPE_SYS_MSG_POINT = 13;






    public static final String SCHEMA = "appfac";//通用schema

    public static final String PATH_INDEX = "index";//跳转首页

    public static final String PATH_WAP = "h5";//跳转WEP页

    public static final String PATH_TWEET = "tweet";//跳转文章详情页
    public static final String PATH_TWEETDETAIL = "tweetdetail";//跳转文章详情页

    public static final String PATH_MOMENTS = "moments";//跳转文友圈详情页

    public static final String PATH_TOPICS = "topic";//跳转话题列表页

    public static final String GROUP_DETAIL = "group_detail";//跳转群组详情页

    public static final String PATH_LIVE_PLAY = "live_play";//跳转直播回放

    public static final String PATH_SMSG = "sys_msg"; // 跳转到我的消息列表

    public static final String PATH_MYFANS = "my_fans"; // 跳转到我的粉丝列表

    public static final String PATH_TALK = "private_msg"; // 跳转到私信界面

    public static final String PATH_USER_VERIFY = "user_verify"; // 用户认证信息，需要个人界面
//
//    public static final String PATH_MSG_ZAN = "msgzan";//
//
//    public static final String PATH_MSGCOMMENT = "msgcomment";//

}
