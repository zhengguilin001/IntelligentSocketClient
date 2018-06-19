package com.ctyon.watch.broadcast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ctyon.watch.ui.activity.contact.ContactsManager;


/**
 * Created by zx
 * On 2017/12/25
 */

public class SimStateReceiver extends BroadcastReceiver {
    private static boolean firstIn = true;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals("android.intent.action.SIM_STATE_CHANGED")) {
            final ContactsManager contactsManager = ContactsManager.getInstance(context);
                    TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            int state = tm.getSimState();

            switch (state) {
                case TelephonyManager.SIM_STATE_READY:
                    if (firstIn){
                        Log.e("SimStateReceiver",state + " Sim卡可用");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                contactsManager.copySimContactsToDb();
                                context.sendBroadcast(new Intent("com.android.ctyon.sim"));
                            }
                        }).start();

                        firstIn = false;
                    }
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                case TelephonyManager.SIM_STATE_ABSENT:
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    Log.e("SimStateReceiver",state + "Sim卡不可用");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            contactsManager.deleteSimContactsFromDb();
                        }
                    }).start();
                    break;
            }
        }
    }


}
