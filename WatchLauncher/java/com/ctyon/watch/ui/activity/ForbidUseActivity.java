package com.ctyon.watch.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.ctyon.watch.R;
import android.app.Instrumentation;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

/**
 * Created by shipeixian on 18-5-10.
 */

public class ForbidUseActivity extends Activity {

    //add by shipeixian for avoid powerkey conflict begin
    private boolean  isBackKeyPress = false;
    //add by shipeixian for avoid powerkey conflict end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forbiduse);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void sendKeyEvent(final int keyCode) {
        final Instrumentation instrumentation = new Instrumentation();
        new Thread(new Runnable() {

            @Override
            public void run () {
                instrumentation.sendKeyDownUpSync(keyCode);
            }
        }).start();
    }
   
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 3:
                    sendKeyEvent(26);
                    isBackKeyPress = false;
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            if (isBackKeyPress) {
                return super.onKeyDown(keyCode, event);
            }
            android.telecom.TelecomManager telecomManager = (android.telecom.TelecomManager) getSystemService(android.content.Context.TELECOM_SERVICE);
            if (!telecomManager.isInCall()) {
                //add by shipeixian for reback lockscreen view begin
                isBackKeyPress = true;
                sendKeyEvent(26);
                handler.sendEmptyMessageDelayed(3,60);
                //add by shipeixian for reback lockscreen view end
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //add by shipeixian for cancel animation begin
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    //add by shipeixian for cancel animation end

}
