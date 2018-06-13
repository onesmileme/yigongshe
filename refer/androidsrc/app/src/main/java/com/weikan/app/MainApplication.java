package com.weikan.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.parser.HotParserConfig;
import com.alibaba.fastjson.parser.HotStrictMode;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;
import com.umeng.analytics.MobclickAgent;
import com.weikan.app.common.net.ErrNoHandlerImpl;
import com.weikan.app.face.FaceConversionUtil;
import com.weikan.app.news.TemplateConfig;
import com.weikan.app.util.AppUtils;
import com.weikan.app.util.URLDefine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import platform.http.HttpClient;
import platform.http.HttpConfig;
import platform.http.HttpUtils;
import platform.nanoinject.NanoInject;
import platform.push.InfoProvider;
import platform.push.SvenPush;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/3/8
 * Time: 14:00
 */
public class MainApplication extends MultiDexApplication {
    private static MainApplication mInstance = null;

    public MainApplication() {
        mInstance = this;
    }

    public static MainApplication getInstance() {
        return mInstance;
    }
    
    @Override
    public void onCreate() {
    	super.onCreate();
        AppUtils.init(this);

        if(isBackgroundProcess()){
            Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    String stacktrace = "";
                    try {
                        ex.printStackTrace();
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        ex.printStackTrace(pw);

                        for (Throwable var5 = ex.getCause(); var5 != null; var5 = var5.getCause()) {
                            var5.printStackTrace(pw);
                        }

                        stacktrace = sw.toString();
                        pw.close();
                        sw.close();
                    } catch (Exception var6) {
                        var6.printStackTrace();
                    }

                    MobclickAgent.reportError(mInstance, stacktrace);
                    MobclickAgent.onKillProcess(mInstance);
                }
            };
            Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
            // 调用一下友盟，注册 crashhandler
            MobclickAgent.getAgent();
            // 如果不是主进程，没必要走下面的逻辑，而且svenpush会重复创建
            return;
        }

        openPush();
        if(isMainProcess()) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    printCrashInformation(throwable);
                    AppManager.AppExit(getApplicationContext());
                }
            });
        } else {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }

        FaceConversionUtil.getInstace().getFileText(this);

        TemplateConfig.initConfig();

        initNanoInject();
        initHttp();

        Sketch.with(this).getConfiguration().setImageSizeCalculator(new ImageSizeCalculator() {
            @Override
            public boolean canUseReadModeByWidth(int imageWidth, int imageHeight) {
                return false;
            }
        });

        BuglyStrategy buglyStrategy = new BuglyStrategy();
        buglyStrategy.setAppChannel(AppUtils.getChannelName());

        Bugly.init(getApplicationContext(), "8d8ad29881", false, buglyStrategy);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void openPush(){
        /**
         * 极光推送
         */
//        JPushInterface.setDebugMode(false);
//        JPushInterface.init(this);


        // 注册自有推送
        SvenPush.getInstance().registerApp(this, new InfoProvider.IInfoProviderInterface() {
            @Override
            public HashMap<String,String> getCommonInfo() {
                return (HashMap<String, String>) new PhoneInfoProvider().getParams();
            }

            @Override
            public String getHost() {
                String url = URLDefine.HUN_WATER_HOST_API;
                return url.contains(":") ? url.substring(0, url.indexOf(":") ) : url;
            }

            @Override
            public int getPort() {
                String url = URLDefine.HUN_WATER_HOST_API;
                return url.contains(":") ?
                        Integer.valueOf(url.substring(url.indexOf(":") + 1, url.length())) : 80;
            }

            @Override
            public String getPath() {
                return URLDefine.LONG_LINK_PATH;
            }
        });
//        SvenPush.getInstance().registerPushListener(new PushReceiver());

        if(BuildConfig.DEBUG){
            SvenPush.setLogLevel(Log.VERBOSE);
        }
        SvenPush.iconResourceId = R.drawable.ic_launcher;
    }

    boolean isMainProcess() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo pInfo : manager.getRunningAppProcesses()) {
            if (TextUtils.equals(pInfo.processName, getApplicationInfo().packageName)) {
                if (pInfo.pid == android.os.Process.myPid()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void printCrashInformation(Throwable ex) {

        // ExceptionName：异常类名。例如：NullPointerException（Java的空引用异常），OutOfMemoryError（内存溢出异常）
        String exceptionName = ex.getClass().getSimpleName();

        // ExceptionMessage：异常信息。例如：未将对象的引用设置的对象的实例（C#的空引用异常）
        String exceptionMessage = ex.getMessage();

        Log.e("LanjingApplication", exceptionName + ": " + exceptionMessage);

        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        Log.e("LanjingApplication", stackTrace);
    }

    private void initNanoInject() {
        NanoInject.Builder builder = new NanoInject.Builder();
        builder.bindInstance(Application.class, this);
        NanoInject.init(builder);
    }

    private void initHttp() {
        if(BuildConfig.TEST_URL){
            URLDefine.HUN_WATER_HOST_API = URLDefine.HUN_WATER_HOST_API_TEST;
        } else {
            URLDefine.HUN_WATER_HOST_API = URLDefine.HUN_WATER_HOST_API_ONLINE;
        }
        // 改进后的Fastjson解析
        HotParserConfig.install();
        HotStrictMode.setIsEnable(false);

        HttpConfig.errNoHandler = new ErrNoHandlerImpl();
        HttpUtils.bindPhoneInfoProvider(new PhoneInfoProvider());

        HttpUtils.init(); // 保证HttpUtils在UI线程上初始化

        HttpClient.instance().setShouldRunOnProxy(true);

        if(BuildConfig.DEBUG){
            HttpUtils.setDebug(true);
        }
    }

    private int processMark = -1;
    private boolean isBackgroundProcess() {
        if (processMark == -1) {
            String processName = getProcessName(this, Process.myPid());

            if(processName != null && processName.contains(":")){
                processMark = 1;
            } else {
                processMark = 0;
            }
        }

        return processMark == 1;
    }

    public static String getProcessName(Context context, int pid){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null && !runningApps.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }

}
