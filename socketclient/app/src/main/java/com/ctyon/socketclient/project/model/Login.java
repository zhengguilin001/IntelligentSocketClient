package com.ctyon.socketclient.project.model;

/**
 * Created by Administrator on 2018/3/12.
 */

public class Login {

    /**
     * timestamp : 1.520843515058086E9
     * timezone : 8
     * token : 529610
     * status : 1
     * type : 1
     * ident : 456873
     */

    private float timestamp;
    private int timezone;
    private String token;
    private int status;
    private int type;
    private int ident;

    public float getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(float timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }
}
