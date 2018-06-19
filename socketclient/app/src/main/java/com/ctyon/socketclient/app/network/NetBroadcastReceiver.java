package com.ctyon.socketclient.app.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * Created by shipeixian on 2018-05-24
 */

public class NetBroadcastReceiver extends BroadcastReceiver {

    private NetEvent netEvent;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //检查网络状态的类型
            int netWrokState = NetUtil.getNetWorkState(context);
            if (netEvent != null)
                // 接口回传网络状态的类型
                netEvent.onNetChange(netWrokState);
        }
        if (intent.getAction().equals("com.ctyon.shawn.UPLOAD_PHOTO")) {
            if (netEvent != null) {
                netEvent.onUploadPhoto();
            }
        }
        if ("unbind_watch".equals(intent.getAction())) {
            if (netEvent != null) {
                netEvent.onUnbindWatch();
            }
        }
        if ("show_imed".equals(intent.getAction())) {
            if (netEvent != null) {
                netEvent.onShowImed();
            }
        }
    }

    public void setNetEvent(NetEvent netEvent) {
        this.netEvent = netEvent;
    }

}