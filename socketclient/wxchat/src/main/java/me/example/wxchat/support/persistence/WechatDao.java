package me.example.wxchat.support.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import me.example.wxchat.support.model.WeChatMessage;

/**
 * Created by xmai on 18-3-23.
 */
@Dao
public interface WechatDao {
    /**
     * 查询所有的信息记录
     * @return
     */
    @Query("SELECT *from wechat ORDER BY id ASC")
    Flowable<List<WeChatMessage>> getMessages();

    /**
     * 插入一条信息
     * @param bean
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(WeChatMessage bean);

    /**
     * 删除所有信息
     */
    @Query("DELETE FROM wechat")
    void deleteAllMessages();

    /**
     * 删除一条信息
     */
    @Query("DELETE FROM wechat WHERE id BETWEEN :minId AND :maxId")
    void deleteMsgById(int minId,int maxId);
}
