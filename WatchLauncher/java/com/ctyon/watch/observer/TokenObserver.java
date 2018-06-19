package com.ctyon.watch.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;

/**
 * Created by zx
 * On 2018/3/28
 */

public class TokenObserver extends ContentObserver {
    private Context mContext;
    private Handler mHandler;  //此Handler用来更新UI线程
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public TokenObserver(Handler handler, Context context) {
        super(handler);
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        String token = Settings.Global.getString(mContext.getContentResolver(),
                "socket_client_alarm");
        mHandler.obtainMessage(30, token).sendToTarget();

    }
}
