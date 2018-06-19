package com.ctyon.watch.ui.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctyon.watch.R;
import com.ctyon.watch.model.AlarmModel;
import com.ctyon.watch.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

//add by shipeixian for adjust ui begin
import android.view.Gravity;
import com.ctyon.watch.ui.activity.CreateAlarmActivity;
//add by shipeixian for adjust ui end

/**
 * Created by Administrator on 2018/1/27.
 */

/**
 * 闹钟列表
 */
public class AlarmAdapter extends BaseAdapter{

    private Context mContext;
    private List<AlarmModel> modelList = new ArrayList<>();

    public AlarmAdapter(Context context, List<AlarmModel> list){

        mContext = context;
        modelList = list;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_alarm_item, parent,false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        AlarmModel alarmModel = modelList.get(position);
        //holder.ivAlarm.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher_round));

        //时间在24小时制和12小时制分别显示，
        int hour = Integer.parseInt(DateUtil.getHour(alarmModel.getTime()));
        int minute = Integer.parseInt(DateUtil.getMinute(alarmModel.getTime()));
        if(!DateFormat.is24HourFormat(mContext)){
           if(hour >= 12){
               holder.tvAPm.setText("下午");
               holder.tvName.setText((hour-12<10 ? (hour-12 ==0? "12":"0"+(hour-12)) : (hour-12))+":"+(minute<10 ? "0"+minute : minute));
           }else{
               holder.tvAPm.setText("上午");
               holder.tvName.setText((hour <10 ? "0"+hour+":"+(minute<10 ? "0"+minute : minute):alarmModel.getTime()));
           }
        }else{
            if(hour == 0)
                holder.tvName.setText("0"+hour+":"+(minute<10 ? "0"+minute : minute));
            else
                holder.tvName.setText(alarmModel.getTime());
        }
        //add by shipeixian for adjust ui begin
        switch (alarmModel.getType()) {
            case 0:
                holder.tvAlarmWeek.setText("每天");
                holder.tvAlarmWeek.setTextSize(18);
                holder.tvAlarmWeek.setGravity(Gravity.RIGHT);
                holder.tvAlarmWeek.setPadding(15, 0, 10, 0);
                break;
            case -1:
                holder.tvAlarmWeek.setText("只响一次");
                holder.tvAlarmWeek.setTextSize(18);
                holder.tvAlarmWeek.setGravity(Gravity.RIGHT);
                holder.tvAlarmWeek.setPadding(15, 0, 10, 0);
                break;
            default:
                String weeksStr = CreateAlarmActivity.parseRepeat(alarmModel.getType(), 1);
                String[] weekArray = weeksStr.split(",");
                for (int i = 0; i < weekArray.length; i++) {
                    switch (Integer.parseInt(weekArray[i])) {
                        case 1:
                            weekArray[i] = "周一";
                            break;
                        case 2:
                            weekArray[i] = "周二";
                            break;
                        case 3:
                            weekArray[i] = "周三";
                            break;
                        case 4:
                            weekArray[i] = "周四";
                            break;
                        case 5:
                            weekArray[i] = "周五";
                            break;
                        case 6:
                            weekArray[i] = "周六";
                            break;
                        case 7:
                            weekArray[i] = "周日";
                            break;
                        default:
                            break;
                    }
                }
                String weekTip = "";
                for (int j = 0; j < weekArray.length; j++) {
                    if (j == weekArray.length - 1) {
                        weekTip += weekArray[j];
                    } else {
                        weekTip += weekArray[j] + "、";
                    }
                }
                holder.tvAlarmWeek.setText(weekTip);
                if(holder.tvAlarmWeek.length() <= 8) {
                       holder.tvAlarmWeek.setTextSize(14);
                       holder.tvAlarmWeek.setGravity(Gravity.RIGHT);
                       holder.tvAlarmWeek.setPadding(5, 0, 10, 0);           
                } else {
                       holder.tvAlarmWeek.setTextSize(13);
                       holder.tvAlarmWeek.setGravity(Gravity.LEFT);
                       holder.tvAlarmWeek.setPadding(20, 0, 3, 0);
                }
                break;
        }
        //add by shipeixian for adjust ui end
        //holder.tvAPm.setText();
        return convertView;
    }
    class ViewHolder{

        public TextView tvName;
        public TextView tvAPm;
        //add by shipeixian for adjust ui begin
        public TextView tvAlarmWeek;
        //add by shipeixian for adjust ui end
        public ImageView ivAlarm;

        public ViewHolder(View view){
            tvName = (TextView)view.findViewById(R.id.tv_alarm_list);
            tvAPm = (TextView)view.findViewById(R.id.tv_apm);
            ivAlarm = (ImageView)view.findViewById(R.id.iv_alarm_list);
            //add by shipeixian for adjust ui begin
            tvAlarmWeek = (TextView)view.findViewById(R.id.tv_alarm_week);
            //add by shipeixian for adjust ui end
        }
    }
}
