package me.example.wxchat.support.persistence;

import java.util.List;

import io.reactivex.Flowable;
import me.example.wxchat.support.model.WeChatMessage;

/**
 * Created by xmai on 18-3-23.
 */

public interface MessageDatasource {
    Flowable<List<WeChatMessage>> getMessages();
    void insertOrUpdateMsg(WeChatMessage bean);
    void deleteAllMessages();
    void deleteMsgById(int minId,int maxId);
}
