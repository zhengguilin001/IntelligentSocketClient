package com.ctyon.socketclient.app.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.ctyon.socketclient.app.server.SocketService;

/**
 * * 接收系统开机广播的接收器
 * #<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = BootBroadcastReceiver.class.getSimpleName();
    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i(TAG, "Boot this system , BootBroadcastReceiver onReceive()");

        if (intent.getAction().equals(ACTION_BOOT)) {
            Log.i(TAG, "BootBroadcastReceiver onReceive(), Do thing!");
            context.startService(new Intent(context, SocketService.class));
        }else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d(TAG, "onReceive: "+"网络发生变化");
            context.startService(new Intent(context,SocketService.class));
        }else if(intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)){
            Log.d(TAG, "onReceive: "+"监测开机铃声，当launcher还没有完全启动时该广播已经发出");
            context.startService(new Intent(context, SocketService.class));
        }
    }

    /**
     * 获取当前的网络状态 ：没有网络0：WIFI网络1：3G网络2：2G网络3
     *
     * @param context
     * @return
     */
    private static int getAPNType(Context context) {
        int netType = 0;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;// wifi
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager mTelephony = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    && !mTelephony.isNetworkRoaming()) {
                netType = 2;// 3G
            } else {
                netType = 3;// 2G
            }
        }
        return netType;
    }
}
