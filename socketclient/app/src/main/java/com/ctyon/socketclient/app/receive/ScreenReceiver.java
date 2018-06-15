package com.ctyon.socketclient.app.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

/**
 * Created by shipeixian on 18-6-14.
 */

public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.i("shipeixian", "屏幕点亮");
            //setPreferredNetworkMode(context, 10);
        }
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i("shipeixian", "屏幕熄灭");
            //setPreferredNetworkMode(context, 5);
        }*/

    }

    //4g通用：10   2G CDMA w/o EVDO：5
    private void setPreferredNetworkMode(Context context, int nwMode) {
        final int phoneSubId = 2;
        final int phoneId = 0;
        android.provider.Settings.Global.putInt(context.getContentResolver(),"preferred_network_mode" + phoneSubId, nwMode);
        if(putIntAtIndex(context.getContentResolver(), "preferred_network_mode", phoneId, nwMode)) {
            Log.i("shipeixian", "网络模式切换成功=="+nwMode);
        }
    }

    public boolean putIntAtIndex(android.content.ContentResolver cr, String name, int index, int value) {
        String data = "";
        String valArray[] = null;
        String v = android.provider.Settings.Global.getString(cr, name);

        if (index == Integer.MAX_VALUE) {
            throw new RuntimeException("putIntAtIndex index == MAX_VALUE index=" + index);
        }
        if (index < 0) {
            throw new RuntimeException("putIntAtIndex index < 0 index=" + index);
        }
        if (v != null) {
            valArray = v.split(",");
        }

        // Copy the elements from valArray till index
        for (int i = 0; i < index; i++) {
            String str = "";
            if ((valArray != null) && (i < valArray.length)) {
                str = valArray[i];
            }
            data = data + str + ",";
        }

        data = data + value;

        // Copy the remaining elements from valArray if any.
        if (valArray != null) {
            for (int i = index+1; i < valArray.length; i++) {
                data = data + "," + valArray[i];
            }
        }
        return android.provider.Settings.Global.putString(cr, name, data);
    }
}
