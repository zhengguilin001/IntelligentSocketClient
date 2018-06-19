package com.ctyon.watch;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.ctyon.watch.manager.AlarmManager;
import com.ctyon.watch.manager.AlarmSqliteHelper;
import com.ctyon.watch.model.AlarmModel;
import com.ctyon.watch.model.AlarmToken;
import com.ctyon.watch.observer.TokenObserver;
import com.ctyon.watch.utils.AlarmManagerUtil;
import com.ctyon.watch.utils.GsonUtil;
import com.ctyon.watch.utils.SafeHandler;
import com.ctyon.watch.utils.WarnUtils;

import java.util.List;


/**
 * Created by Administrator on 2018/1/25.
 */

public class App extends Application implements SafeHandler.HandlerContainer{

    private TokenObserver mTokenObserver;
    private Handler mHandler;
    private AlarmManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = new AlarmManager(this);
        AlarmSqliteHelper alarmHelper = AlarmSqliteHelper.getInstance(this);
        alarmHelper.close();
        mHandler = new SafeHandler<App>(this);
        initTokenObserver();
    }

    private void initTokenObserver() {
        mTokenObserver = new TokenObserver(mHandler, this);
         getContentResolver().registerContentObserver(Settings.Global.getUriFor("socket_client_alarm"),
        true, mTokenObserver);

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        getContentResolver().unregisterContentObserver(mTokenObserver);
        mHandler.removeCallbacksAndMessages(null);
        mTokenObserver=null;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 30:
                int cycle = 0;
                String weeksStr =null;
                String token = (String) msg.obj;
                Log.e("App","新token：" + token);
                AlarmToken alarmToken = GsonUtil.GsonToBean(token, AlarmToken.class);
                //Log.e("App","alarmToken：" + alarmToken.toString());
                List<AlarmToken.AlarmBean> alarmBeans = GsonUtil.jsonToList(GsonUtil.GsonString(alarmToken.getAlarm()), AlarmToken.AlarmBean.class);
                //Log.e("App","alarmBeans：" + alarmBeans.toString());
                for (AlarmToken.AlarmBean alarmBean : alarmBeans) {
                    AlarmModel model = new AlarmModel();
                    String time = alarmBean.getStart();//闹钟时间
                    String week = alarmBean.getWeek();//重复周期
                    if (week.equals("0000000")){
                        cycle = 0;//单次
                    }else if (week.equals("1111111")){
                        cycle = -1;//每天都响
                    }else {
                        char[] c = week.toCharArray();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < c.length; i++) {
                            if (c[i] == '1'){
                                sb.append(i+1).append(',');
                            }
                        }
                        sb.deleteCharAt(sb.length()-1);
                        weeksStr = String.valueOf(sb);
                        Log.e("App","weeksStr：" + weeksStr);
                    }
                    model.setOpen(true);
                    model.setTime(time);
                    model.setType(cycle);
                    model.setAlarm_week(weeksStr);
                    model.setOneTime(false);
                    manager.addAlarm(model);
                    setClock(model,time,cycle,weeksStr);
                }
                break;
        }
    }

    /**
     * 设定闹钟
     */
    private void setClock(AlarmModel model,String time,int cycle,String weeksStr) {
        int alarm_id = (int)manager.queryIdByTime(model);
        if (time != null) {
            String[] hour_minute = time.split(":");
            if (cycle == 0) {//是每天的闹钟
                AlarmManagerUtil.setAlarm(this, 0, Integer.parseInt(hour_minute[0]), Integer.parseInt
                        (hour_minute[1]), alarm_id, null, "闹钟响了", 2);
            } if(cycle == -1){//是只响一次的闹钟
                AlarmManagerUtil.setAlarm(this, 1, Integer.parseInt(hour_minute[0]), Integer.parseInt
                        (hour_minute[1]), alarm_id, null, "闹钟响了", 2);
            }else {//多选，周几的闹钟
                AlarmManagerUtil.setAlarm(this, 2, Integer.parseInt(hour_minute[0]), Integer
                        .parseInt(hour_minute[1]), alarm_id, weeksStr, "闹钟响了", 2);
            }
            WarnUtils.toast(this,"闹钟设置成功");
        }
    }

}
