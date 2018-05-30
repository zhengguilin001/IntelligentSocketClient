package me.example.wxchat.support.persistence;

import android.content.Context;

import com.wx.common.support.utils.ThreadManager;

import me.example.wxchat.support.model.WeChatMessage;
import me.example.wxchat.support.ui.Injection;

/**
 * Created by xmai on 18-3-26.
 */

public class DbUtils {
    /**
     * 插入新消息
     * @param msg
     */
    public static void insertMsg(Context ctx,WeChatMessage msg){
        ThreadManager.getThreadPollProxy().execute(() ->
                Injection.provideMsgDatasource(ctx.getApplicationContext())
                        .insertOrUpdateMsg(msg));
    }

}
