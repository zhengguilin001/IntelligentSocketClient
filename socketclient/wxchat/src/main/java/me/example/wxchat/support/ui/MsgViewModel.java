package me.example.wxchat.support.ui;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import me.example.wxchat.support.model.WeChatMessage;
import me.example.wxchat.support.model.WxchatMessageBean;
import me.example.wxchat.support.persistence.MessageDatasource;

/**
 * Created by xmai on 18-3-23.
 */

public class MsgViewModel extends ViewModel {
    private final MessageDatasource mDataSource;

    public MsgViewModel(MessageDatasource mDataSouce) {
        this.mDataSource = mDataSouce;
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public Flowable<List<WeChatMessage>> getMessages() {
        return mDataSource.getMessages()
                .map(msgs -> msgs);
    }
    /**
     * 插入或更新一条数据
     *
     * @param bean
     * @return
     */
    public Completable insertOrupdateReport(WeChatMessage bean) {
        return Completable.fromAction(() ->
                mDataSource.insertOrUpdateMsg(bean));
    }
    /**
     * 删除数据库
     *
     * @return
     */
    public Completable deleteAllReports() {
        return Completable.fromAction(() ->
                mDataSource.deleteAllMessages());
    }

    /**
     * 删除单条数据
     */
    public Completable deleteReportById(int minId, int maxId) {
        return Completable.fromAction(() ->
                mDataSource.deleteMsgById(minId, maxId));
    }
}
