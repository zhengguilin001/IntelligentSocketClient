package me.example.wxchat.support.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

/**
 * Created by xmai on 18-3-23.
 */
@Entity(tableName = "wechat")
public class WeChatMessage {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private String secid;               //服务器Id
    @ColumnInfo
    private int MessageSenderType;      //用户类型:儿童　｜　家长
    @ColumnInfo
    private int MessageSendStatus;      //消息发送状态:送达　｜　发送中 | 发送失败
    @ColumnInfo
    private int MessageReadStatus;      //消息读取状态:已读　｜　未读
    @ColumnInfo
    private int MessageType ;           //消息类型:文字 | 语音　｜　图片
    @ColumnInfo
    private boolean showMeaageTime;     //是否显示时间
    @ColumnInfo
    private long MessageTime;           //消息时间
    @ColumnInfo
    private String logoUrl;             //用户头像url
    @ColumnInfo
    private String MessageText;         //文本消息内容
    @ColumnInfo
    private int duringTime;             //语音消息持续时长
    @ColumnInfo
    private String voiceUrl;            //语音文件地址url
    @ColumnInfo
    private String imageUrl;            //图片文件地址url

    @ColumnInfo
    private String value1;              //保留字段１
    @ColumnInfo
    private String value2;              //保留字段２
    @ColumnInfo
    private String value3;              //保留字段３
    @ColumnInfo
    private String value4;              //保留字段４
    @ColumnInfo
    private String value5;              //保留字段５


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSecid() {
        return secid;
    }

    public void setSecid(String secid) {
        this.secid = secid;
    }

    public int getMessageSenderType() {
        return MessageSenderType;
    }

    public void setMessageSenderType(int MEssageSenderType) {
        this.MessageSenderType = MEssageSenderType;
    }

    public int getMessageSendStatus() {
        return MessageSendStatus;
    }

    public void setMessageSendStatus(int messageSendStatus) {
        MessageSendStatus = messageSendStatus;
    }

    public int getMessageReadStatus() {
        return MessageReadStatus;
    }

    public void setMessageReadStatus(int messageReadStatus) {
        MessageReadStatus = messageReadStatus;
    }

    public int getMessageType() {
        return MessageType;
    }

    public void setMessageType(int messageType) {
        MessageType = messageType;
    }

    public boolean isShowMeaageTime() {
        return showMeaageTime;
    }

    public void setShowMeaageTime(boolean showMeaageTime) {
        this.showMeaageTime = showMeaageTime;
    }

    public long getMessageTime() {
        return MessageTime;
    }

    public void setMessageTime(long messageTime) {
        MessageTime = messageTime;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getMessageText() {
        return MessageText;
    }

    public void setMessageText(String messageText) {
        MessageText = messageText;
    }

    public int getDuringTime() {
        return duringTime;
    }

    public void setDuringTime(int duringTime) {
        this.duringTime = duringTime;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    public String getValue5() {
        return value5;
    }

    public void setValue5(String value5) {
        this.value5 = value5;
    }
}
