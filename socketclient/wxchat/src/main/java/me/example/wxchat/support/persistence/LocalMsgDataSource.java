package me.example.wxchat.support.persistence;

import java.util.List;

import io.reactivex.Flowable;
import me.example.wxchat.support.model.WeChatMessage;

/**
 * Created by xmai on 18-3-23.
 */

public class LocalMsgDataSource implements MessageDatasource {
    private final WechatDao dao;

    public LocalMsgDataSource(WechatDao wechatDao) {
        dao = wechatDao;
    }

    @Override
    public Flowable<List<WeChatMessage>> getMessages() {
        return dao.getMessages();
    }

    @Override
    public void insertOrUpdateMsg(WeChatMessage bean) {
        dao.insertMessage(bean);
    }

    @Override
    public void deleteAllMessages() {
        dao.deleteAllMessages();
    }

    @Override
    public void deleteMsgById(int minId, int maxId) {
        dao.deleteMsgById(minId, maxId);
    }
}
