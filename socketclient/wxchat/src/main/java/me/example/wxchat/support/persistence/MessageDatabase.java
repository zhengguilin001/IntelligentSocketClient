package me.example.wxchat.support.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import me.example.wxchat.support.model.WeChatMessage;

/**
 * Created by xmai on 18-3-23.
 */

@Database(entities = {WeChatMessage.class}, version = 1, exportSchema = false)
public abstract class MessageDatabase extends RoomDatabase{
    private static volatile MessageDatabase INSTANCE;
    public abstract WechatDao wechatDao();

    public static MessageDatabase getInstance (Context ctx){
        if(INSTANCE==null){
            synchronized (MessageDatabase.class){
                if(INSTANCE==null){
                    INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                            MessageDatabase.class,"wechat.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
