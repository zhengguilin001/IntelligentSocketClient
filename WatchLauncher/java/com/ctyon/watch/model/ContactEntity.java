package com.ctyon.watch.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zx
 * On 2017/9/11
 */

public class ContactEntity implements Parcelable {
    private String name;
    private String number;
    private long id;

    public ContactEntity() {
    }


    protected ContactEntity(Parcel in) {
        name = in.readString();
        number = in.readString();
        id = in.readLong();
    }

    public static final Creator<ContactEntity> CREATOR = new Creator<ContactEntity>() {
        @Override
        public ContactEntity createFromParcel(Parcel in) {
            return new ContactEntity(in);
        }

        @Override
        public ContactEntity[] newArray(int size) {
            return new ContactEntity[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(number);
        dest.writeLong(id);
    }

    @Override
    public String toString() {
        return "ContactEntity{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", id=" + id +
                '}';
    }
}
