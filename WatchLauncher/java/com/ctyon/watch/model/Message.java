package com.ctyon.watch.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zx
 * On 2017/10/31
 */

public class Message implements Parcelable {

    private long id;
    private long thread_id;
    private String address;
    private String time;
    private String date;
    private String body;
    /***
     * 短信状态
     * 信息状态，默认是-1
     * 待发送32；
     * 发送失败64；
     */
    private int status;
    /***
     * 短信类型
     * 1:接收;2:发送;3:发送中
     */
    private int type;

    public Message() {

    }

    protected Message(Parcel in) {
        id = in.readLong();
        thread_id = in.readLong();
        address = in.readString();
        time = in.readString();
        date = in.readString();
        body = in.readString();
        status = in.readInt();
        type = in.readInt();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getThread_id() {
        return thread_id;
    }

    public void setThread_id(long thread_id) {
        this.thread_id = thread_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(thread_id);
        dest.writeString(address);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeString(body);
        dest.writeInt(status);
        dest.writeInt(type);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", thread_id=" + thread_id +
                ", address='" + address + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", body='" + body + '\'' +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
