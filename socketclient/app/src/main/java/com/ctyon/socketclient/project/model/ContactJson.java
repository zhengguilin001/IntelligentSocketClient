package com.ctyon.socketclient.project.model;

import java.util.List;

/**
 * Created by shipeixian on 18-6-6.
 */

public class ContactJson {

    /**
     * last : 0
     * ident : 899056
     * contact : [{"phone":"13570463301","name":"测试1"}]
     * index : 0
     * type : 16
     */

    private int last;
    private int ident;
    private int index;
    private int type;
    private List<ContactBean> contact;

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<ContactBean> getContact() {
        return contact;
    }

    public void setContact(List<ContactBean> contact) {
        this.contact = contact;
    }

    public static class ContactBean {
        /**
         * phone : 13570463301
         * name : 测试1
         */

        private String phone;
        private String name;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
