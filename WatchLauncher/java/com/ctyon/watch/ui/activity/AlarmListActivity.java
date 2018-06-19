package com.ctyon.watch.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ctyon.watch.R;
import com.ctyon.watch.manager.AlarmManager;
import com.ctyon.watch.model.AlarmModel;
import com.ctyon.watch.ui.adapter.AlarmAdapter;
import com.ctyon.watch.utils.DateUtil;
import com.ctyon.watch.utils.LogUtils;
import com.ctyon.watch.utils.WarnUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//add by shipeixian for adjust ui begin
import android.widget.Button;
//add by shipeixian for adjust ui end

/**
 * 闹钟主界面
 */
public class AlarmListActivity extends BaseActivity {

    private ListView mListView;
    private List<AlarmModel> list = new ArrayList<>();
    private AlarmManager manager;
    private LinearLayout mCreate;
    private AlarmAdapter adapter;
    private boolean isLongPress = false;
    private TextView mBack;

    //add by shipeixian for adjust ui begin
    private Button goBackButton, addAlarmButton;
    private TextView tipText;
    //add by shipeixian for adjust ui end

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_alarm_list);
    }

    @Override
    protected void loadData() {

        manager = new AlarmManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        list.addAll(manager.queryAll());
        adapter.notifyDataSetChanged();
        String new_time = DateUtil.getTime(new Date());
        LogUtils.e("现在时间："+new_time);
        //add by shipeixian for adjust ui begin
        if(list.size() == 0) {
             tipText.setVisibility(View.VISIBLE);
        } else {
             tipText.setVisibility(View.GONE);
        }
        //add by shipeixian for adjust ui end
    }

    @Override
    protected void initComponentEvent() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(isLongPress)
                    return;
                AlarmModel alarmModel = list.get(position);
                Intent intent = new Intent(view.getContext(),CreateAlarmActivity.class);
                intent.putExtra("alarm_id",alarmModel.getAlramId());
                startActivity(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                isLongPress = true;
                final AlarmModel model = list.get(position);
                AlertDialog dialog = new AlertDialog.Builder(view.getContext()).setMessage("是否删除时间为"+model.getTime()+"的闹钟")
                        .setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                adapter.notifyDataSetChanged();
                                manager.deleteAlarmById(model.getAlramId());
                                dialog.dismiss();
                                //add by shipeixian for adjust ui begin
                                if(list.size() == 0) {
                                     tipText.setVisibility(View.VISIBLE);
                                }
                                //add by shipeixian for adjust ui end
                            }
                        }).setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isLongPress = false;
                    }
                });
                return false;
            }
        });
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long size = manager.countOfAlarm();
                if(size > 4){
                    WarnUtils.toast(v.getContext(),"闹钟最多创建五个哦!");
                    return;
                }
                Intent intent = new Intent(AlarmListActivity.this,CreateAlarmActivity.class);
                intent.putExtra("alarm_id",-1L);
                startActivity(intent);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        //add by shipeixian for adjust ui begin
        goBackButton = (Button) findViewById(R.id.backButton);
        goBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlarmListActivity.this.finish();
            }
        });
        addAlarmButton = (Button) findViewById(R.id.addAlarm);
        addAlarmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                long size = manager.countOfAlarm();
                if(size > 4){
                    WarnUtils.toast(view.getContext(),"闹钟最多创建五个哦!");
                    return;
                }
                Intent intent = new Intent(AlarmListActivity.this,CreateAlarmActivity.class);
                intent.putExtra("alarm_id",-1L);
                startActivity(intent);
            }
        });
        tipText = (TextView) findViewById(R.id.tv_tip);
        tipText.setText("您还没有添加闹钟\n快去添加吧 Y(^_^)Y");
        //add by shipeixian for adjust ui end
    }

    @Override
    protected void initComponentView() {

        mListView = (ListView)findViewById(R.id.alarm_list);
        mCreate = (LinearLayout)findViewById(R.id.create_alarm);
        mBack = (TextView)findViewById(R.id.tv_list_back);
        adapter = new AlarmAdapter(this,list);
        mListView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.closeDB();
    }

    //add by shipeixian for cancel animation begin
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    //add by shipeixian for cancel animation end
}
