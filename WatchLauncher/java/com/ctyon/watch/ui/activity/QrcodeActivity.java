package com.ctyon.watch.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;
import com.ctyon.watch.R;

/**
 * Created by shipeixian on 18-5-15.
 */

public class QrcodeActivity extends Activity {

    private TextView deviceTokenCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        initView();
        initData();
    }

    private void initView() {
        deviceTokenCode = (TextView) findViewById(R.id.device_token_code);
    }

    private void initData() {
        try {
            String code = Settings.Global.getString(getContentResolver(), "socket_client_shawn_token");
            if (!TextUtils.isEmpty(code)) {
                deviceTokenCode.setText("注册码:"+code);
            }
        } catch (Exception e) {
            
        }
    }

    //add by shipeixian for cancel animation begin
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    //add by shipeixian for cancel animation end

}
