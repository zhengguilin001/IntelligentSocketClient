package com.ctyon.watch.model;

/**
 * Created by zx
 * On 2017/9/18
 */

public class CountCallLog {
    private String number;
    private String name;
    private int count;
    private int type;
    private String date;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "CountCallLog{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", type=" + type +
                ", date='" + date + '\'' +
                '}';
    }
}
