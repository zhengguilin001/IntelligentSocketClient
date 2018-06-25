package com.ctyon.socketclient.project.model;

import java.util.List;

/**
 * Created by shipeixian on 18-5-10.
 */

public class ForbidUseModel {


    /**
     * type : 28
     * ident : 9
     * disturb : [{"start":"08:00","end":"18:00","week":"1111100","status":1}]
     */

    private int type;
    private int ident;
    private List<DisturbBean> disturb;

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

    public List<DisturbBean> getDisturb() {
        return disturb;
    }

    public void setDisturb(List<DisturbBean> disturb) {
        this.disturb = disturb;
    }

    public static class DisturbBean {
        /**
         * start : 08:00
         * end : 18:00
         * week : 1111100
         * status : 1
         */

        private String start;
        private String end;
        private String week;
        private int status;

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
