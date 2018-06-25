package com.ctyon.watch.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ctyon.watch.manager.AlarmManager;
import com.ctyon.watch.model.AlarmModel;
import com.ctyon.watch.model.CommonConstants;
import com.ctyon.watch.ui.activity.ClockAlarmActivity;
import com.ctyon.watch.utils.DateUtil;
import com.ctyon.watch.utils.LogUtils;

import java.util.Calendar;

import static com.ctyon.watch.model.CommonConstants.ALARM_RING_ACTION;

/**
 * Created by Administrator on 2018/1/30.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //do nothing
        if(1 == 1) {
            return;
        }
        if(intent == null)
            return;
        if(intent.getAction().equals(ALARM_RING_ACTION)){
            long alarm_id = intent.getIntExtra("alarm_id",-1);
            String alarm_title = intent.getStringExtra("alarm_title");
            String alarm_week = intent.getStringExtra("alarm_week");
            int ringValue = intent.getIntExtra("soundOrVibrator", 2);
            AlarmManager manager = new AlarmManager(context);
            AlarmModel model = manager.queryIsRepeatById(alarm_id);
            if(model == null)
                return;
            if(!model.isOpen())
                return;
            //判断是否为24小时制，来重新组成time字符串
            String alarm_time = model.getTime();
//            if(!DateFormat.is24HourFormat(context)){
//                int hour = Integer.parseInt(DateUtil.getHour(model.getTime()));
//                int minute = Integer.parseInt(DateUtil.getMinute(model.getTime()));
//                alarm_time = hour -12 >0 ? (hour-12 <10 ? "0"+(hour-12)+":"+minute : ""+(hour-12))+":"+minute : alarm_time;
//            }
            //分日期
            if(alarm_week != null){
                String[] weekStr = alarm_week.split(",");
                Calendar c = Calendar.getInstance();
                int now_week = c.get(Calendar.DAY_OF_WEEK);
                for(String week : weekStr){
                    if(Integer.parseInt(week) == now_week){
                        sendAlarmIntent(alarm_time,context,alarm_id,alarm_title, ringValue);
                    }
                }
            }else{
                sendAlarmIntent(alarm_time,context,alarm_id,alarm_title, ringValue);
            }

        }
    }

    private void sendAlarmIntent(String alarm_time,Context context,long alarm_id,String alarm_title, int ringValue){
        String new_time = DateUtil.get24HourTime();
        LogUtils.e("接收到时间为:"+alarm_time+"现在的时间为:"+new_time);
        if(alarm_time.equals(new_time) && !CommonConstants.IS_ALARM_RING){
            LogUtils.e("接收到闹钟id:"+alarm_id+"时间为:"+alarm_time+"现在的时间为:"+new_time);
            Intent startIntent = new Intent(context, ClockAlarmActivity.class);
            startIntent.putExtra("alarm_id",alarm_id);
            startIntent.putExtra("alarm_title",alarm_title);
            startIntent.putExtra("soundOrVibrator", ringValue);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        }else
            LogUtils.e("时间不对");
    }
}
