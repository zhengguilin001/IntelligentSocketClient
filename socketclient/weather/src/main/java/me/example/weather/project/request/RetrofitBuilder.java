package me.example.weather.project.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import me.example.weather.project.model.WeatherModel;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018/3/16.
 */

public class RetrofitBuilder {
    private static Retrofit mRetrofit;

    public synchronized static Retrofit buildRetrofit() {
        if (mRetrofit == null) {
            Interceptor mTokenInterceptor = chain -> {
                Request originalRequest = chain.request();
                Request authorised = originalRequest.newBuilder()
                        .header("Authorization-Access-Token", "Config.ACCESS_TOKEN")
//                            .header("Authorization-Access-User", Config.USER_ID)
                        .build();
                return chain.proceed(authorised);
            };
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            Gson gson = new GsonBuilder()  //防止返回错误类型解析出错
                    .registerTypeHierarchyAdapter(WeatherModel.class, new ResultJsonDeser())
                    .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            // 构建 OkHttpClient 时,将 OkHttpClient.Builder() 传入 with() 方法,进行初始化配置
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .retryOnConnectionFailure(true)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .addNetworkInterceptor(mTokenInterceptor)
//                    .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize))
                    .build();
            mRetrofit = new Retrofit.Builder().client(client)
                    .baseUrl("https://free-api.heweather.com")
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return mRetrofit;
    }
}
