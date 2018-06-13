package com.weikan.app.push;

import com.weikan.app.bean.PushObject;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/3/25
 * Time: 21:28
 */
public class PushEventObject {
    public PushObject object;
    public PushEventObject(PushObject o){
        object = o;
    }
}
