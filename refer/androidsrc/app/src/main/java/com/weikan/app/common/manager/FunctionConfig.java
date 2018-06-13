package com.weikan.app.common.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import com.weikan.app.MainApplication;
import com.weikan.app.common.Model.AppConfigObtainEvent;
import com.weikan.app.common.net.bean.AppConfigObject;
import com.weikan.app.util.PrefDefine;
import de.greenrobot.event.EventBus;

import java.io.*;

/**
 * app的所有功能配置都在这里
 * Created by liujian on 17/1/6.
 */
public class FunctionConfig {

    private final static String CONFIGDATA = "appconfigdata";

    private AppConfigObject configObject;

    private static class Holder {
        static final FunctionConfig inst = new FunctionConfig();
    }

    private FunctionConfig(){
        if(configObject ==null){
            SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(PrefDefine.PREF_FILE,
                    Context.MODE_PRIVATE);
            String data = sp.getString(CONFIGDATA, null);
            if (data != null) {
                Object obj = null;
                try {
                    ByteArrayInputStream bIn = new ByteArrayInputStream(Base64.decode(data, Base64.DEFAULT));
                    ObjectInputStream objIn = new ObjectInputStream(bIn);
                    obj = objIn.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (obj != null && obj instanceof AppConfigObject) {
                    configObject = (AppConfigObject) obj;
                }
            }
        }
    }

    public static FunctionConfig getInstance(){
        return Holder.inst;
    }

    /**
     * 是否支持群组功能
     * @return
     */
    public boolean isSupportWenyouGroup(){
        if (configObject != null) {
            return configObject.need_group == 1;
        }
        return false;
    }

    /**
     * 是否支持直播里面的礼物
     * @return
     */
    public boolean isSupportLiveGift(){
        if (configObject != null) {
            return configObject.need_live_gift == 1;
        }
        return false;
    }

    /**
     * 是否支持直播
     * @return
     */
    public boolean isSupportLive(){
        if (configObject != null) {
            return configObject.need_live == 1;
        }
        return false;
    }

    public void setConfig(AppConfigObject object){
        if (object == null) {
            return;
        }
        this.configObject = object;
        ByteArrayOutputStream bOut = null;
        ObjectOutputStream objOut = null;
        try {
            bOut = new ByteArrayOutputStream();
            objOut = new ObjectOutputStream(bOut);
            objOut.writeObject(configObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 写入config信息
        if (objOut != null) {
            SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(PrefDefine.PREF_FILE,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(CONFIGDATA, Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT));
            editor.apply();
        }
        EventBus.getDefault().post(new AppConfigObtainEvent());
    }
}
