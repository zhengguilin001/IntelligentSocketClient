package com.ctyon.watch.model;

import java.util.List;

/**
 * Created by zx
 * On 2018/3/29
 */

public class AlarmToken {

    /**
     * type : 30
     * ident : 656196
     * alarm : [{"start":"08:00","week":"1111000","text":"闹钟","duration":0,"size":0,"status":1}]
     */

    private int type;
    private int ident;
    private List<AlarmBean> alarm;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public List<AlarmBean> getAlarm() {
        return alarm;
    }

    public void setAlarm(List<AlarmBean> alarm) {
        this.alarm = alarm;
    }

    public static class AlarmBean {
        /**
         * start : 08:00
         * week : 1111000
         * text : 闹钟
         * duration : 0
         * size : 0
         * status : 1
         */

        private String start;
        private String week;
        private String text;
        private int duration;
        private int size;
        private int status;

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "AlarmBean{" +
                    "start='" + start + '\'' +
                    ", week='" + week + '\'' +
                    ", text='" + text + '\'' +
                    ", duration=" + duration +
                    ", size=" + size +
                    ", status=" + status +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AlarmToken{" +
                "type=" + type +
                ", ident=" + ident +
                ", alarm=" + alarm +
                '}';
    }
}
