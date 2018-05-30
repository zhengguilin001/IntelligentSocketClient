package com.xmaihh.phone.support.hardware;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/13.
     * LAC：Location Area Code，定位区域编码，2个字节长的十六进制BCD码（不包括0000和FFFE）
     * TAC：Tracking Area Code，追踪区域编码，
     * CID：Cell Identity，信元标识，2个字节
     * MCC：Mobile Country Code，移动国家代码，三位数，中国：460
     * MNC：Mobile Network Code，移动网络号，两位数（中国移动0，中国联通1，中国电信2）
     * BSSS：Base station signal strength，基站信号强度
 */

public class ModemUtils {
    public static final String TAG = ModemUtils.class.getSimpleName();

    //获取基站信息
    public static List<String> getTowerInfo(Context context) {
        int mcc = -1;
        int mnc = -1;
        int lac = -1;
        int cellId = -1;
        int rssi = -1;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getNetworkOperator();
        mcc = Integer.parseInt(operator.substring(0, 3));
        List<String> list = new ArrayList<String>();
        @SuppressLint("MissingPermission") List<CellInfo> infos = tm.getAllCellInfo();
        for (CellInfo info : infos) {
            if (info instanceof CellInfoCdma) {
                CellInfoCdma cellInfoCdma = (CellInfoCdma) info;
                CellIdentityCdma cellIdentityCdma = cellInfoCdma.getCellIdentity();
                mnc = cellIdentityCdma.getSystemId();
                lac = cellIdentityCdma.getNetworkId();
                cellId = cellIdentityCdma.getBasestationId();
                CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                rssi = cellSignalStrengthCdma.getCdmaDbm();
            } else if (info instanceof CellInfoGsm) {
                CellInfoGsm cellInfoGsm = (CellInfoGsm) info;
                CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
                mnc = cellIdentityGsm.getMnc();
                lac = cellIdentityGsm.getLac();
                cellId = cellIdentityGsm.getCid();
                CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                rssi = cellSignalStrengthGsm.getDbm();
            } else if (info instanceof CellInfoLte) {
                CellInfoLte cellInfoLte = (CellInfoLte) info;
                CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                mnc = cellIdentityLte.getMnc();
                lac = cellIdentityLte.getTac();
                cellId = cellIdentityLte.getCi();
                CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                rssi = cellSignalStrengthLte.getDbm();
            } else if (info instanceof CellInfoWcdma) {
                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) info;
                CellIdentityWcdma cellIdentityWcdma = null;
                CellSignalStrengthWcdma cellSignalStrengthWcdma = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
                    mnc = cellIdentityWcdma.getMnc();
                    lac = cellIdentityWcdma.getLac();
                    cellId = cellIdentityWcdma.getCid();
                    cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                    rssi = cellSignalStrengthWcdma.getDbm();
                }
            } else {
                Log.e(TAG, "get CellInfo error");
                return null;
            }
            String tower = String.valueOf(mcc) + "#" + String.valueOf(mnc) + "#" + String.valueOf(lac)
                    + "#" + String.valueOf(cellId) + "#" + String.valueOf(rssi);
            Log.d(TAG, "getTowerInfo: "+tower);
            list.add(tower);
        }
        if (list.size() > 6) {
            list = list.subList(0, 5);
        } else if (list.size() < 3) {
            int need = 3 - list.size();
            for (int i = 0; i < need; i++) {
                list.add("");
            }
        }
        return list;

    }
}
