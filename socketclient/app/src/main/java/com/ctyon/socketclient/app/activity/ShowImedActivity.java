package com.ctyon.socketclient.app.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ctyon.socketclient.App;
import com.ctyon.socketclient.R;
import com.ctyon.socketclient.project.util.ScreenUtils;
import com.xmaihh.phone.support.hardware.DeviceUtils;

/**
 * Created by shipeixian on 18-6-1.
 */

public class ShowImedActivity extends Activity {

    private TextView showImed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(0,0);
        super.onCreate(savedInstanceState);

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_show_imed);

        showImed = (TextView) findViewById(R.id.showImed);
        showImed.setText("MEID    "+DeviceUtils.getIMEI(App.getsContext()));

        ScreenUtils.wakeUpAndUnlock(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

}
