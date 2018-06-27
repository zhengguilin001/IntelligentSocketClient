package com.xmaihh.phone.project.model;

/**
 * Created by Administrator on 2018/3/15.
 * 基站信息
 * LAC：Location Area Code，定位区域编码，2个字节长的十六进制BCD码（不包括0000和FFFE）
 * TAC：Tracking Area Code，追踪区域编码，
 * CID：Cell Identity，信元标识，2个字节
 * MCC：Mobile Country Code，移动国家代码，三位数，中国：460
 * MNC：Mobile Network Code，移动网络号，两位数（中国移动0，中国联通1，中国电信2）
 * BSSS：Base station signal strength，基站信号强度
 */

public class Modem {

    /**
     * mcc : 456873  //国家编号
     * mnc : 2     //运营商编号
     * lac : 6752    //小区编号
     * ci : 60       //基站编号
     * rxlev : -1    //信号频点,该值减去110 后得到信号强度,一般为负数
     */

    private int mcc;
    private int mnc;
    private int lac;
    private int ci;
    private int rxlev;

    //add by shipeixian begin
    private int sid;
    private int nid;
    private int bid;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }
    //add by shipeixian end

    public int getMcc() {
        return mcc;
    }

    public void setMcc(int mcc) {
        this.mcc = mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getCi() {
        return ci;
    }

    public void setCi(int ci) {
        this.ci = ci;
    }

    public int getRxlev() {
        return rxlev;
    }

    public void setRxlev(int rxlev) {
        this.rxlev = rxlev;
    }

    @Override
    public String toString() {
        return "Modem{" +
                "mcc=" + mcc +
                ", mnc=" + mnc +
                ", lac=" + lac +
                ", ci=" + ci +
                ", rxlev=" + rxlev +
                '}';
    }
}
