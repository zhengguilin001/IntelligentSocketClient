package com.ctyon.watch.model;

/**
 * Created by Administrator on 2018/1/27.
 */

public class AlarmModel {

    private long  alramId;

    private String time;

    private String description;

    private int type;

    private boolean isOpen;

    private boolean isOneTime;

    private String alarm_week;

    public String getAlarm_week() {
        return alarm_week;
    }

    public void setAlarm_week(String alarm_week) {
        this.alarm_week = alarm_week;
    }

    public long getAlramId() {
        return alramId;
    }

    public void setAlramId(long alramId) {
        this.alramId = alramId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isOneTime() {
        return isOneTime;
    }

    public void setOneTime(boolean oneTime) {
        isOneTime = oneTime;
    }
}
