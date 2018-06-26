package com.ctyon.socketclient.app.network;

/**
 * Created by shipeixian on 2018-05-24
 */

public interface NetEvent {
    void onNetChange(int netMobile);
    void onUploadPhoto(boolean isUploadSucess);
    void onUnbindWatch();
    void onShowImed();
    void onReallyUnbindWatch();
}