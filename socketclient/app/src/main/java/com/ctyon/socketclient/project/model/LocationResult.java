package com.ctyon.socketclient.project.model;

/**
 * Created by shipeixian on 18-6-28.
 */

public class LocationResult {

    /**
     * type : 6
     * ident : 99999
     * status : 1
     * loc_ident : 1
     * loc_lon : 113
     * loc_lat : 22
     */

    private int type;
    private int ident;
    private int status;
    private int loc_ident;
    private String loc_lon;
    private String loc_lat;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLoc_ident() {
        return loc_ident;
    }

    public void setLoc_ident(int loc_ident) {
        this.loc_ident = loc_ident;
    }

    public String getLoc_lon() {
        return loc_lon;
    }

    public void setLoc_lon(String loc_lon) {
        this.loc_lon = loc_lon;
    }

    public String getLoc_lat() {
        return loc_lat;
    }

    public void setLoc_lat(String loc_lat) {
        this.loc_lat = loc_lat;
    }
}
