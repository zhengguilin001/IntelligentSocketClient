package com.ctyon.watch.ui.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctyon.watch.R;
import com.ctyon.watch.manager.AlarmManager;
import com.ctyon.watch.model.AlarmModel;
import com.ctyon.watch.ui.view.SelectRemindCyclePopup;
import com.ctyon.watch.ui.view.SelectRemindWayPopup;
import com.ctyon.watch.ui.view.TimePickerView;
import com.ctyon.watch.utils.AlarmManagerUtil;
import com.ctyon.watch.utils.DateUtil;
import com.ctyon.watch.utils.LogUtils;
import com.ctyon.watch.utils.ScreenUtils;
import com.ctyon.watch.utils.WarnUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ctyon.watch.utils.DateUtil.get24HourTime;
import static com.ctyon.watch.utils.DateUtil.getTime;

/**
 * 创建闹钟界面
 */
public class CreateAlarmActivity extends BaseActivity implements View.OnClickListener{
    private TextView date_tv;
    private RelativeLayout repeat_rl, ring_rl;
    private TextView tv_repeat_value, tv_ring_value;
    private LinearLayout allLayout;
    private int cycle;
    private int ring;
    private PopupWindow popupWindow;
    List<String> hourList = new ArrayList<>();
    List<String> minutList = new ArrayList<>();
    private AlarmManager manager;
    private AlarmModel model;
    private String alarm_hour;
    private String alarm_minute;
    private TextView tv_create_back;
    private TextView tv_create_sure;
    private boolean chooseTime = false;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_alarm);
    }

    @Override
    protected void loadData() {

        //初始化小时数据
        if(DateFormat.is24HourFormat(this)){
            for(int i=0; i<24; i++){
                if(i<10)
                    hourList.add("0"+i);
                else
                    hourList.add(i+"");
            }
        }else{
            for(int i=1; i<=12; i++){
                if(i<10)
                    hourList.add("0"+i);
                else
                    hourList.add(i+"");
            }
        }
        //初始化分钟数据
        for(int i=0; i<60; i++){
            if(i<10)
                minutList.add("0"+i);
            else
                minutList.add(i+"");
        }

        manager = new AlarmManager(this);
        Intent intent = getIntent();
        long alarm_id = intent.getLongExtra("alarm_id",-1);
        model = manager.queryIsRepeatById(alarm_id);
    }

    @Override
    protected void initComponentEvent() {
        ring_rl.setOnClickListener(this);
        repeat_rl.setOnClickListener(this);
        tv_create_back.setOnClickListener(this);
        tv_create_sure.setOnClickListener(this);
        if(model != null)
            date_tv.setText(model.getTime());
        date_tv.setOnClickListener(this);
    }

    @Override
    protected void initComponentView() {
        allLayout = (LinearLayout) findViewById(R.id.all_layout);
        date_tv = (TextView) findViewById(R.id.date_tv);
        repeat_rl = (RelativeLayout) findViewById(R.id.repeat_rl);
        ring_rl = (RelativeLayout) findViewById(R.id.ring_rl);
        tv_repeat_value = (TextView) findViewById(R.id.tv_repeat_value);
        tv_ring_value = (TextView) findViewById(R.id.tv_ring_value);
        tv_create_back = (TextView) findViewById(R.id.tv_create_back);
        tv_create_sure = (TextView) findViewById(R.id.tv_create_sure);

        //初始化默认值
        tv_ring_value.setText("铃声和震动");
        ring = 2;

        tv_repeat_value.setText("每天");
        cycle = 0;
    }

    /**
     * 显示窗口
     */
    private void showPopWindow(){
        popupWindow = new PopupWindow(this);
        View pop = LayoutInflater.from(this).inflate(R.layout.pop_time_picker,null);

        TextView tvCancel = (TextView)pop.findViewById(R.id.tv_cancel);
        TextView tvSure = (TextView)pop.findViewById(R.id.tv_sure);
        final TextView tvAm = (TextView)pop.findViewById(R.id.tv_am);
        final TextView tvPm = (TextView)pop.findViewById(R.id.tv_pm);
        TimePickerView hourPicker = (TimePickerView)pop.findViewById(R.id.hour_pv);
        TimePickerView minutePicker = (TimePickerView)pop.findViewById(R.id.minute_pv);
        hourPicker.setData(hourList);
        minutePicker.setData(minutList);
        if(DateFormat.is24HourFormat(this)){
            tvAm.setVisibility(View.GONE);
            tvPm.setVisibility(View.GONE);
        }

        if(model != null){
            hourPicker.setSelected(DateUtil.getHour(model.getTime()));
            minutePicker.setSelected(DateUtil.getMinute(model.getTime()));
        }else{
            alarm_hour = DateUtil.getHour(get24HourTime());
            alarm_minute = DateUtil.getMinute(getTime(new Date()));
            LogUtils.e(alarm_hour+":"+alarm_minute);
            if(!DateFormat.is24HourFormat(this)){
                int hour = Integer.parseInt(alarm_hour);
                switch (hour){
                    case 0:
                        hourPicker.setSelected("12");
                        tvAm.setSelected(true);
                        break;
                    case 12:
                        hourPicker.setSelected("12");
                        break;
                    default:
                        if(hour >12)
                            tvPm.setSelected(true);
                        hourPicker.setSelected(hour -12 >0 ? (hour-12 <10 ? "0"+(hour-12) : ""+(hour-12)) : (hour<10?"0"+alarm_hour:alarm_hour));
                        break;
                }

            }else
                hourPicker.setSelected(alarm_hour);
            minutePicker.setSelected(alarm_minute);
        }
        hourPicker.setOnSelectListener(new TimePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                if(!DateFormat.is24HourFormat(CreateAlarmActivity.this)){
                    if(tvPm.isSelected())
                        alarm_hour = (Integer.parseInt(text)+12)==24?"00":String.valueOf(Integer.parseInt(text)+12);
                    else
                        alarm_hour = text;
                }
                else
                    alarm_hour = text;
                LogUtils.e("当前选择的是"+alarm_hour);
            }
        });
        minutePicker.setOnSelectListener(new TimePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                alarm_minute = text;
            }
        });
        tvSure.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        int screenHeight = ScreenUtils.getScreenHeight(this);
        popupWindow.setHeight(screenHeight*3/5);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.AnimBottom);
        popupWindow.setContentView(pop);
        popupWindow.showAtLocation(LayoutInflater.from(this).inflate(R.layout.activity_alarm,null), Gravity.BOTTOM,0,0);
    }
    /**
     * 关闭窗口
     */
    private void closePopWindow(){
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.repeat_rl:
                selectRemindCycle();
                break;
            case R.id.ring_rl:
                selectRingWay();
                break;
            case R.id.tv_sure:
                chooseTime = true;
                date_tv.setText(alarm_hour+":"+alarm_minute);
                closePopWindow();
                break;
            case R.id.tv_cancel:
                closePopWindow();
                break;
            case R.id.tv_create_sure:

                if(!chooseTime){
                    WarnUtils.toast(this,"还未选择时间哦～!");
                    break;
                }
                if(model != null)
                    manager.deleteAlarmById(model.getAlramId());
                model = new AlarmModel();
                model.setOpen(true);
                model.setTime(alarm_hour+":"+alarm_minute);
                model.setType(cycle);
                String weeksStr = parseRepeat(cycle, 1);
                model.setAlarm_week(weeksStr);
                model.setOneTime(false);
                manager.addAlarm(model);
                setClock();
                finish();
                break;
            case R.id.tv_create_back:
                finish();
                break;
            case R.id.date_tv:
                showPopWindow();
                break;
            default:
                break;
        }
    }

    /**
     * 设定闹钟
     */
    private void setClock() {
        int alarm_id = (int)manager.queryIdByTime(model);
        if (alarm_hour != null && alarm_minute != null) {
            if (cycle == 0) {//是每天的闹钟
                AlarmManagerUtil.setAlarm(this, 0, Integer.parseInt(alarm_hour), Integer.parseInt
                        (alarm_minute), alarm_id, null, "闹钟响了", ring);
            } if(cycle == -1){//是只响一次的闹钟
                AlarmManagerUtil.setAlarm(this, 1, Integer.parseInt(alarm_hour), Integer.parseInt
                        (alarm_minute), alarm_id, null, "闹钟响了", ring);
            }else {//多选，周几的闹钟
                String weeksStr = parseRepeat(cycle, 1);
                String[] weeks = weeksStr.split(",");
                AlarmManagerUtil.setAlarm(this, 2, Integer.parseInt(alarm_hour), Integer
                        .parseInt(alarm_minute), alarm_id, weeksStr, "闹钟响了", ring);
            }
            WarnUtils.toast(this,"闹钟设置成功");
        }
    }

    public void selectRemindCycle() {
        final SelectRemindCyclePopup fp = new SelectRemindCyclePopup(this);
        fp.showPopup(allLayout);
        fp.setOnSelectRemindCyclePopupListener(new SelectRemindCyclePopup
                .SelectRemindCyclePopupOnClickListener() {

            @Override
            public void obtainMessage(int flag, String ret) {
                switch (flag) {
                    // 星期一
                    case 0:

                        break;
                    // 星期二
                    case 1:

                        break;
                    // 星期三
                    case 2:

                        break;
                    // 星期四
                    case 3:

                        break;
                    // 星期五
                    case 4:

                        break;
                    // 星期六
                    case 5:

                        break;
                    // 星期日
                    case 6:

                        break;
                    // 确定
                    case 7:
                        int repeat = Integer.valueOf(ret);
                        tv_repeat_value.setText(parseRepeat(repeat, 0));
                        cycle = repeat;
                        fp.dismiss();
                        break;
                    case 8:
                        tv_repeat_value.setText("每天");
                        cycle = 0;
                        fp.dismiss();
                        break;
                    case 9:
                        tv_repeat_value.setText("只响一次");
                        cycle = -1;
                        fp.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }
    /**
     * 选择提醒类型
     */
    public void selectRingWay() {
        SelectRemindWayPopup fp = new SelectRemindWayPopup(this);
        fp.showPopup(allLayout);
        fp.setOnSelectRemindWayPopupListener(new SelectRemindWayPopup
                .SelectRemindWayPopupOnClickListener() {

            @Override
            public void obtainMessage(int flag) {
                switch (flag) {
                    // 震动
                    case 0:
                        tv_ring_value.setText("震动");
                        ring = 0;
                        break;
                    // 铃声
                    case 1:
                        tv_ring_value.setText("铃声");
                        ring = 1;
                        break;
                    case 2:
                        tv_ring_value.setText("铃声和震动");
                        ring = 2;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * @param repeat 解析二进制闹钟周期
     * @param flag   flag=0返回带有汉字的周一，周二cycle等，flag=1,返回weeks(1,2,3)
     * @return
     */
    public static String parseRepeat(int repeat, int flag) {
        String cycle = "";
        String weeks = "";
        if (repeat == 0) {
            repeat = 127;
        }
        if (repeat % 2 == 1) {
            cycle = "周一";
            weeks = "1";
        }
        if (repeat % 4 >= 2) {
            if ("".equals(cycle)) {
                cycle = "周二";
                weeks = "2";
            } else {
                cycle = cycle + "," + "周二";
                weeks = weeks + "," + "2";
            }
        }
        if (repeat % 8 >= 4) {
            if ("".equals(cycle)) {
                cycle = "周三";
                weeks = "3";
            } else {
                cycle = cycle + "," + "周三";
                weeks = weeks + "," + "3";
            }
        }
        if (repeat % 16 >= 8) {
            if ("".equals(cycle)) {
                cycle = "周四";
                weeks = "4";
            } else {
                cycle = cycle + "," + "周四";
                weeks = weeks + "," + "4";
            }
        }
        if (repeat % 32 >= 16) {
            if ("".equals(cycle)) {
                cycle = "周五";
                weeks = "5";
            } else {
                cycle = cycle + "," + "周五";
                weeks = weeks + "," + "5";
            }
        }
        if (repeat % 64 >= 32) {
            if ("".equals(cycle)) {
                cycle = "周六";
                weeks = "6";
            } else {
                cycle = cycle + "," + "周六";
                weeks = weeks + "," + "6";
            }
        }
        if (repeat / 64 == 1) {
            if ("".equals(cycle)) {
                cycle = "周日";
                weeks = "7";
            } else {
                cycle = cycle + "," + "周日";
                weeks = weeks + "," + "7";
            }
        }
        return flag == 0 ? cycle : weeks;
    }
}
