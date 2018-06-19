package com.ctyon.watch.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zx
 * On 2017/10/19
 */

public class Conversation implements Parcelable {
    private long id;
    private String date;
    private String number;//短信号码
    private String name;//短信名字
    private String address;//当联系人名字不存在时为号码，否则为名字
    private int unReadCount;

    public Conversation() {

    }

    protected Conversation(Parcel in) {
        id = in.readLong();
        date = in.readString();
        number = in.readString();
        name = in.readString();
        address = in.readString();
        unReadCount = in.readInt();
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(date);
        dest.writeString(number);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeInt(unReadCount);
    }
}
