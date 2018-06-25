package com.ctyon.watch.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.ctyon.watch.ui.activity.contact.ContactsManager;
import android.os.Message;

import android.provider.ContactsContract;
import java.util.ArrayList;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.database.Cursor;
import java.util.List;
import com.ctyon.watch.manager.AlarmManager;
import com.ctyon.watch.model.AlarmModel;
import com.ctyon.watch.utils.DateUtil;
import java.util.Date;
import com.ctyon.watch.ui.activity.ClockAlarmActivity;
import android.provider.Settings;


public class AlarmService extends Service {

    private List<AlarmModel> list = new ArrayList<>();
    private AlarmManager manager;
    
    @Override
    public IBinder onBind(Intent intent) {
       
        return null;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 4444) {
                stopSelf();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.d("shipeixian", "AlarmService onCreate");
        manager = new AlarmManager(getApplicationContext());
        final String nowTime = com.ctyon.watch.utils.DateUtil.getTime(new java.util.Date());
     
        new Thread(new Runnable() {
            @Override
            public void run() {
                list.clear();
                list.addAll(manager.queryAll());
                if(list.size() > 0) {
                     android.util.Log.i("shipeixian", "有"+list.size()+"个闹钟");
                     for (AlarmModel item : list) {
                         android.util.Log.i("shipeixian", "type = "+item.getType()+" time = "+item.getTime()+" alarm_week = "+item.getAlarm_week()+" isOpen = "+item.isOpen());
                         if(item.isOpen()) {
                             Settings.Global.putInt(getContentResolver(), "isAlarmSet", 1);
                         }
                         //每天或者周期性闹钟
                         if(item.getTime().equals(nowTime) && item.getAlarm_week()!=null && item.getAlarm_week().contains(getWeek())) {
                             //do something
                             Intent startIntent = new Intent(AlarmService.this, ClockAlarmActivity.class);
                             startIntent.putExtra("alarm_id",item.getAlramId());
                             startIntent.putExtra("alarm_title","闹钟响了");
                             startIntent.putExtra("soundOrVibrator", 2);
                             startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                             startActivity(startIntent);
                             break;
                         }
                         //只响一次的闹钟
                         if(item.getTime().equals(nowTime) && item.getType() == -1) {
                             if(item.isOpen()) {
                                 Intent startIntent = new Intent(AlarmService.this, ClockAlarmActivity.class);
                                 startIntent.putExtra("alarm_id",item.getAlramId());
                                 startIntent.putExtra("alarm_title","闹钟响了");
                                 startIntent.putExtra("soundOrVibrator", 2);
                                 startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                 startActivity(startIntent);
                                 manager.updateToCloseAlarm(item);
                                 break;
                             }
                         }
                     }
                } else {
                    Settings.Global.putInt(getContentResolver(), "isAlarmSet", 0);
                }
                handler.sendEmptyMessage(4444);
            }
        }).start();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        android.util.Log.d("shipeixian", "AlarmService onStartCommand");
        
        return START_NOT_STICKY;
    }

    @Override
    public void onStart(final Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.util.Log.d("shipeixian", "AlarmService onDestroy");
    }

    /*获取星期几*/
    private String getWeek() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int i = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int week = 0;
        switch (i) {
            case 1:
                //"星期日"
                week = 7;
                break;
            case 2:
                //"星期一"
                week = 1;
                break;
            case 3:
                //"星期二"
                week = 2;
            case 4:
                //"星期三"
                week = 3;
                break;
            case 5:
                //"星期四"
                week = 4;
                break;
            case 6:
                //"星期五"
                week = 5;
                break;
            case 7:
                //"星期六"
                week = 6;
                break;
            default:
                break;
        }
        return week+"";
    }    

}
