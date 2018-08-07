package com.ygs.android.yigongshe.net;

public interface ApiStatus {

    public static int OK = 2000;

    public static int PARAM_ERROR = 4000;

    public static int NOT_EXISTS = 4004;

    public static int INNER_ERROR = 5000;

    public static int DUP_ERROR = 5001;

    public static int USER_NOT_EXISTS = 6000;

    public static int USER_PASSWORD_ERROR = 6001;

    public static int TOKEN_ERROR = 6002;

    public static int USER_DELTED = 6003;

    public static int COUNT_PER_PAGE = 20;
}
