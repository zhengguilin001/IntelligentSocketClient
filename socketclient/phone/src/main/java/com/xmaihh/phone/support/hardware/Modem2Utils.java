package com.xmaihh.phone.support.hardware;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.xmaihh.phone.project.model.Modem;

import java.util.List;

/**
 * Created by Administrator on 2018/3/15.
 */

public class Modem2Utils {
    //private static final String TAG = ModemUtils.class.getSimpleName();
    private static final String TAG = "shipeixian";
    //"cells":[{"mcc":460,"mnc":0,"lac":10342,"ci":45736962,"rxlev":8}]

    public static Modem getModemCell(Context ctx) {
        //      通过MNC判断
        TelephonyManager telManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        /** 获取SIM卡的IMSI码
         * SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile Subscriber Identification Number）是区别移动用户的标志，
         * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
         * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
         * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
         * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
         */
        @SuppressLint("MissingPermission") String imsi = telManager.getSubscriberId() + "getNetworkOperatorName=" + telManager.getNetworkOperatorName() + "\n";//直接获取移动运营商名称
        // 返回值MCC + MNC
        String operator = telManager.getNetworkOperator();
        int mcc = Integer.parseInt(operator.substring(0, 3));
        int mnc = Integer.parseInt(operator.substring(3));
        int lac = 0;
        int cellId = 0;
        try {

            @SuppressLint("MissingPermission") GsmCellLocation location = (GsmCellLocation) telManager.getCellLocation();
            lac = location.getLac();
            cellId = location.getCid();
            Log.i(TAG, "基站来了1 MCC = " + mcc + "\t MNC = " + mnc + "\t LAC = " + lac + "\t CID = " + cellId);
        } catch (Exception e) {

        }

        /*try {
            // 中国电信获取LAC、CID的方式
            @SuppressLint("MissingPermission") CdmaCellLocation location1 = (CdmaCellLocation) telManager.getCellLocation();
            lac = location1.getNetworkId();
            cellId = location1.getBaseStationId();
            cellId /= 16;
            Log.i(TAG, "基站来了2 MCC = " + mcc + "\t MNC = " + mnc + "\t LAC = " + lac + "\t CID = " + cellId);
        } catch (Exception e) {

        }*/

        if (imsi != null) {
//            if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {//因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
//                //中国移动
//            } else if (imsi.startsWith("46001")) {
//                //中国联通
//            } else if (imsi.startsWith("46003")) {
//                //中国电信
//            }
            if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007") || imsi.startsWith("46001")) {
                // 中国移动和中国联通获取LAC、CID的方式
                @SuppressLint("MissingPermission") GsmCellLocation location = (GsmCellLocation) telManager.getCellLocation();
                lac = location.getLac();
                cellId = location.getCid();
                Log.i(TAG, "不是中国电信 MCC = " + mcc + "\t MNC = " + mnc + "\t LAC = " + lac + "\t CID = " + cellId);
            } else if (imsi.startsWith("46003")) {
                // 中国电信获取LAC、CID的方式
                @SuppressLint("MissingPermission") CdmaCellLocation location1 = (CdmaCellLocation) telManager.getCellLocation();
                lac = location1.getNetworkId();
                cellId = location1.getBaseStationId();
                cellId /= 16;
                Log.i(TAG, "中国电信 MCC = " + mcc + "\t MNC = " + mnc + "\t LAC = " + lac + "\t CID = " + cellId);
            }
        }
        // 获取邻区基站信息
        @SuppressLint("MissingPermission") List<NeighboringCellInfo> infos = telManager.getNeighboringCellInfo();
        StringBuffer sb = new StringBuffer("总数 : " + infos.size() + "\n");
        for (NeighboringCellInfo info1 : infos) { // 根据邻区总数进行循环
            sb.append(" LAC : " + info1.getLac()); // 取出当前邻区的LAC
            sb.append(" CID : " + info1.getCid()); // 取出当前邻区的CID
            sb.append(" BSSS : " + (-113 + 2 * info1.getRssi()) + "\n"); // 获取邻区基站信号强度
        }
        Log.i(TAG, " 获取邻区基站信息:" + sb.toString());

        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        //  通过联网方式判断
        @SuppressLint("MissingPermission") NetworkInfo info2 = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Log.i(TAG,
                "getDetailedState=" + info2.getDetailedState() + "\n" +
                        "getReason=" + info2.getReason() + "\n" +
                        "getSubtype=" + info2.getSubtype() + "\n" +
                        "getSubtypeName=" + info2.getSubtypeName() + "\n" +
                        "getExtraInfo=" + info2.getExtraInfo() + "\n" +
                        "getTypeName=" + info2.getTypeName() + "\n" +
                        "getType=" + info2.getType()
        );

        Modem modem = new Modem();
        modem.setMcc(mcc);
        modem.setMnc(mnc);
        modem.setLac(lac);
        modem.setCi(cellId);
        modem.setRxlev(8);
        return modem;
    }
}
