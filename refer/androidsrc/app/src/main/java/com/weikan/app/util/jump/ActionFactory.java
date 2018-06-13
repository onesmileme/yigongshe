package com.weikan.app.util.jump;

import com.weikan.app.push.PushDefine;
import com.weikan.app.util.jump.actionImpl.ErrorAction;
import com.weikan.app.util.jump.actionImpl.GroupDetailAction;
import com.weikan.app.util.jump.actionImpl.OriginalDetailAction;
import com.weikan.app.util.jump.actionImpl.TopicListAction;

import java.util.HashMap;

/**
 * Created by Lee on 2016/9/27.
 */
public class ActionFactory {
    private static HashMap<String, IAction> actionMap = new HashMap<>();

    /**
     *
     */


    /**
     * 增加schema步骤：
     * 1.与schema相对应的执行动作Action
     * 2.在这里绑定
     */
    static {
        actionMap.put(PushDefine.PATH_TWEET, new OriginalDetailAction());
        actionMap.put(PushDefine.PATH_TOPICS, new TopicListAction());
        actionMap.put(PushDefine.GROUP_DETAIL, new GroupDetailAction());
    }

    public static IAction createAction(String path) {
        if (actionMap.containsKey(path)) {
            return actionMap.get(path);
        }
        return new ErrorAction();
    }
}
