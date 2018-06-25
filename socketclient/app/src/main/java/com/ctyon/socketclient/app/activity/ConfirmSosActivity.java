package com.ctyon.socketclient.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctyon.socketclient.App;
import com.ctyon.socketclient.R;
import com.ctyon.socketclient.project.util.ScreenUtils;
import com.xmaihh.phone.support.hardware.DeviceUtils;

import java.lang.reflect.Method;

/**
 * Created by shipeixian on 18-6-1.
 */

public class ConfirmSosActivity extends Activity {


    private TextView sosTitle;
    private TextView sosTip;
    private TextView confirm;
    private TextView cancel;
    private RelativeLayout bottomZone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(0,0);
        super.onCreate(savedInstanceState);

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_confirm_sos);


        initView();

        ScreenUtils.wakeUpAndUnlock(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    private void initView() {
        sosTitle = (TextView) findViewById(R.id.sosTitle);
        sosTip = (TextView) findViewById(R.id.sosTip);
        confirm = (TextView) findViewById(R.id.confirmButton);
        cancel = (TextView) findViewById(R.id.cancelButton);
        bottomZone= (RelativeLayout) findViewById(R.id.bottomZone);

        sosTitle.setText("拨打SOS号码");
        sosTip.setText("您还没设置SOS号码哦!");
        confirm.setText("确定");
        cancel.setText("取消");

        String sosString = android.provider.Settings.Global.getString(getContentResolver(),"socket_client_sos_number");
        if (TextUtils.isEmpty(sosString)) {
            sosTip.setVisibility(View.VISIBLE);
            bottomZone.setVisibility(View.GONE);
        } else {
            bottomZone.setVisibility(View.VISIBLE);
            sosTip.setVisibility(View.GONE);
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String sosString = android.provider.Settings.Global.getString(getContentResolver(),"socket_client_sos_number");
                    if (sosString.length() > 1) {
                        String[] sosNumbers = sosString.split("/");
                        java.util.Random random = new java.util.Random();
                        int index = random.nextInt(sosNumbers.length);
                        //now set the sos_status 1, else it is 0
                        android.provider.Settings.Global.putInt(getContentResolver(),"socket_client_sos_status", 1);
                        Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", Uri.parse("tel:"+sosNumbers[0]));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        sendSMS(sosNumbers[0], "您关注的宝贝发出sos紧急求救信息，请尽快确认宝贝的安全！");
                    }
                } catch(Exception e) {
                    Log.i("shipeixian", "Exception:" + e.toString());
                }
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmSosActivity.this.finish();
            }
        });

    }


    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void sendSMS(String phoneNumber, String message) {
        android.telephony.SmsManager manager = android.telephony.SmsManager.getDefault();
        Intent intent1 =  new Intent("com.ctyon.shawn.SMS.SENT");
        Intent intent2 = new Intent("com.ctyon.shawn.SMS.DELIVERY");
        android.app.PendingIntent pi1 = android.app.PendingIntent.getBroadcast(this, 0, intent1 , 0);
        android.app.PendingIntent pi2 = android.app.PendingIntent.getBroadcast(this, 0, intent2 , 0);
        manager.sendTextMessage(phoneNumber, null, message, pi1, pi2);
    }

}
