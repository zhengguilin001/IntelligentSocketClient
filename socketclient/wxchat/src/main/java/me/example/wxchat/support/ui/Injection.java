package me.example.wxchat.support.ui;

import android.content.Context;

import me.example.wxchat.support.persistence.LocalMsgDataSource;
import me.example.wxchat.support.persistence.MessageDatabase;
import me.example.wxchat.support.persistence.MessageDatasource;

/**
 * Created by xmai on 18-3-23.
 */

public class Injection {
    public static MessageDatasource provideMsgDatasource(Context ctx){
        MessageDatabase database = MessageDatabase.getInstance(ctx);
        return new LocalMsgDataSource(database.wechatDao());
    }
    public static ViewModelFactory provideViewModelFactory(Context ctx){
        MessageDatasource datasource = provideMsgDatasource(ctx);
        return new ViewModelFactory(datasource);
    }
}
