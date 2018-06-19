package com.ctyon.watch.ui.activity.calllogs;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ctyon.watch.R;
import com.ctyon.watch.model.CountCallLog;
import com.ctyon.watch.utils.ContactsUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//add by shipeixian for call when click record item begin
import android.widget.AdapterView;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.ArrayList;
//add by shipeixian for call when click record item end

//add by shipeixian for adjust ui begin
import android.widget.Button;
import com.ctyon.watch.ui.activity.BaseActivity;
//add by shipeixian for adjust ui end

/**
 * Created by zx
 * On 2018/1/27
 */

public class CalllogActivity extends BaseActivity {
    private CallLogManager callLogManager;
    private ExecutorService singleThreadExecutor;
    private ListView lvCallLogs;
    private LinearLayout llNoCallLog;
    private List<CountCallLog> callLogs;
    private CallLogAdapter callLogAdapter;

    //add by shipeixian for adjust ui begin
    private Button goBackButton;

    @Override
    protected void setContentView() {
        
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void initComponentEvent() {

    }

    @Override
    protected void initComponentView() {

    }
    //add by shipeixian for adjust ui end

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calllog);
        initView();
        callLogManager = CallLogManager.getInstance(this);
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    private void initView() {
        lvCallLogs = (ListView)findViewById(R.id.lvCallLogs);
        llNoCallLog = (LinearLayout) findViewById(R.id.llNoCallLog);
        //add by shipeixian for call when click record item begin
        lvCallLogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountCallLog countCallLog = (CountCallLog) callLogAdapter.getItem(position);
                Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", Uri.parse("tel:"+countCallLog.getNumber()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        //add by shipeixian for call when click record item end

        //add by shipeixian for delete call record begin
        lvCallLogs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CalllogActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否删除该记录");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final List<CountCallLog> deleteItem = new ArrayList<CountCallLog>();
                        deleteItem.add(callLogs.get(position));
                        callLogs.remove(callLogs.get(position));
                        handler.sendEmptyMessageDelayed(1, 50);
                        callLogManager.deleteCallLogs(deleteItem);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
        //add by shipeixian for delete call record end

        //add by shipeixian for adjust ui begin
        goBackButton = (Button) findViewById(R.id.backButton);
        goBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CalllogActivity.this.finish();
            }
        });
        //add by shipeixian for adjust ui end

    }

    @Override
    protected void onResume() {
        super.onResume();
        queryCallLogs();
    }
    /***
     * 查找通话记录
     */
    private void queryCallLogs() {
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callLogs = callLogManager.queryCountCallLogs();
                setName(callLogs);
                //handler.sendEmptyMessage(1);
            }
        });
    }

    private void setName(final List<CountCallLog> callLogs) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (CountCallLog callLog : callLogs) {
                    //add by shipeixian for Exception begin
                    try {
                         String name = ContactsUtil.getName(callLog.getNumber(),CalllogActivity.this);
                         callLog.setName(name);
                    } catch (Exception e) {

                    }
                    //add by shipeixian for Exception end
                }
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    //Log.e("CallMainActivity", callLogs.toString());
                    if (callLogs.size()>0){
                        lvCallLogs.setVisibility(View.VISIBLE);
                        llNoCallLog.setVisibility(View.GONE);
                        if (callLogAdapter == null){
                            callLogAdapter = new CallLogAdapter(CalllogActivity.this, callLogs);
                            lvCallLogs.setAdapter(callLogAdapter);
                        }else {
                            callLogAdapter.updateListView(callLogs);
                        }
                    }else {
                        lvCallLogs.setVisibility(View.GONE);
                        llNoCallLog.setVisibility(View.VISIBLE);
                    }
                    break;

            }
        }
    };

    //add by shipeixian for cancel animation begin
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    //add by shipeixian for cancel animation end

}
