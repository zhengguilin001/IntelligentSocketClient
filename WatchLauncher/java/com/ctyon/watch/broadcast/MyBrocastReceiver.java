package com.ctyon.watch.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;


import com.ctyon.watch.manager.AlarmManager;
import com.ctyon.watch.model.AlarmModel;
import com.ctyon.watch.ui.activity.CreateAlarmActivity;
import com.ctyon.watch.utils.AlarmManagerUtil;
import com.ctyon.watch.utils.WarnUtils;

public class MyBrocastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.ctyon.shawn.SET_ALARM")) {
            final AlarmManager manager = new AlarmManager(context);
            //   08:00,0/09:00,-1/12:00,65/  or  ""
            String alarmArgs  = intent.getStringExtra("alarmArgs");
            String rawAlarm = Settings.Global.getString(context.getContentResolver(), "socket_alarm");
            Settings.Global.putString(context.getContentResolver(), "socket_alarm", alarmArgs);

            //服务器旧的有几个闹钟,一顿删
            if (!TextUtils.isEmpty(rawAlarm)) {
                String[] splitOldAlarmStr = rawAlarm.split("/");
                if (splitOldAlarmStr.length > 0) {
                    for (String item : splitOldAlarmStr) {
                        String[] times = item.split(",");
                        long alarmId = manager.queryIdByTimeAndType(times[0], times[1]);
                        if (alarmId != -1) {
                            manager.deleteAlarmById(alarmId);
                        }
                    }
                }
            }
            //服务器新的几个闹钟,一顿加
            setClock(context, manager, alarmArgs);
        }
    }   
    private void setClock(Context context, AlarmManager manager, String alarmArgs) {
        if (TextUtils.isEmpty(alarmArgs)) {
            return;
        }
        String[] splitString = alarmArgs.split("/");
        for (String item : splitString) {
            String[] values = item.split(",");
            String[] times = values[0].split(":");
            AlarmModel model = new AlarmModel();
            model.setOpen(true);
            model.setTime(times[0] + ":" + times[1]);
            model.setType(Integer.parseInt(values[1]));
            String weeksStr = CreateAlarmActivity.parseRepeat(Integer.parseInt(values[1]), 1);
            model.setAlarm_week(weeksStr);
            model.setOneTime(false);
            manager.addAlarm(model);

            if (times[0] != null && times[1] != null) {
                if (Integer.parseInt(values[1]) == 0) {//是每天的闹钟
                    AlarmManagerUtil.setAlarm(context, 0, Integer.parseInt(times[0]), Integer.parseInt
                            (times[1]), -1, null, "闹钟响了", 2);
                }
                if (Integer.parseInt(values[1]) == -1) {//是只响一次的闹钟
                    AlarmManagerUtil.setAlarm(context, 1, Integer.parseInt(times[0]), Integer.parseInt
                            (times[1]), -1, null, "闹钟响了", 2);
                } else {//多选，周几的闹钟
                    AlarmManagerUtil.setAlarm(context, 2, Integer.parseInt(times[0]), Integer
                            .parseInt(times[1]), -1, weeksStr, "闹钟响了", 2);
                }
                WarnUtils.toast(context, "闹钟设置成功");
            }

        }
    }
}