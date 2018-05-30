package me.example.weather.project.request;

import retrofit2.Retrofit;

/**
 * Created by Administrator on 2018/3/16.
 */

public class NetWorkManager {
    private Retrofit mRetrofit;

    public weatherServer getmWeatherServer() {
        return mWeatherServer;
    }

    private weatherServer mWeatherServer;

    public NetWorkManager() {
        this.mRetrofit = RetrofitBuilder.buildRetrofit();
        this.mWeatherServer = mRetrofit.create(weatherServer.class);
    }

    public static  NetWorkManager getInstance() {
        return NetWorkManagerHolder.INSTANCE;
    }

    private static class NetWorkManagerHolder {
        private static final NetWorkManager INSTANCE = new NetWorkManager();
    }
}
