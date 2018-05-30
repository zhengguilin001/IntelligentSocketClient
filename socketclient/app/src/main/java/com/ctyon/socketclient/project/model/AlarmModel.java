package com.ctyon.socketclient.project.model;

import java.util.List;

/**
 * Created by shipeixian on 18-5-26.
 */

public class AlarmModel {

    /**
     * ident : 361999
     * alarm : [{"duration":0,"week":"1111111","size":0,"start":"08:05","text":"闹钟","status":1},{"duration":0,"week":"0000000","size":0,"start":"08:00","text":"闹钟","status":1}]
     * imei : C5B20180200030
     * type : 30
     */

    private int ident;
    private String imei;
    private int type;
    private List<AlarmBean> alarm;

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<AlarmBean> getAlarm() {
        return alarm;
    }

    public void setAlarm(List<AlarmBean> alarm) {
        this.alarm = alarm;
    }

    public static class AlarmBean {
        /**
         * duration : 0.0
         * week : 1111111
         * size : 0
         * start : 08:05
         * text : 闹钟
         * status : 1
         */

        private double duration;
        private String week;
        private int size;
        private String start;
        private String text;
        private int status;

        public double getDuration() {
            return duration;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
