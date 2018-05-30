package me.example.weather.app.activity;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import me.example.weather.R;
import me.example.weather.project.model.WeatherModel;
import me.example.weather.project.request.NetWorkManager;
import me.xmai.global.config.Constants;

public class WeatherActivity extends AppCompatActivity {
    private CompositeDisposable mDisposables = new CompositeDisposable();
    private TextView tv_city;
    private TextView tv_temp;
    private TextView tv_weather;
    private TextView tv_wind;
    private TextView tv_level;
    private TextView tv_time;

    private String str_city = "";
    private String str_temp = "N/A";
    private String str_weather ="";
    private String str_wind = "";
    private String str_level = "";
    private String str_time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                 /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_weather);

        tv_city = findViewById(R.id.tv_city);
        tv_temp = findViewById(R.id.tv_temperature);
        tv_weather = findViewById(R.id.tv_weather);
        tv_wind = findViewById(R.id.tv_wind);
        tv_level = findViewById(R.id.tv_windlevel);
        tv_time = findViewById(R.id.tv_time);
        String city = Settings.Global.getString(getContentResolver(),
                Constants.MODEL.SETTINGS.GLOBAL_CITY);
        city = (city==null?"北京":city);
        String weatherjson = Settings.Global.getString(getContentResolver(),
                Constants.MODEL.SETTINGS.GLOBAL_WEATHER);
        if(weatherjson==null){
            getWeather(city);
        }else{
//            String json = "\"禅城区,多云,25度,西南风,5~6级,2018-03-28 14:00:00更新\"";
            String [] temp = null;
            temp = weatherjson.split(",");
            if (temp!=null&&temp.length==6){
                str_city = temp[0];
                str_weather = temp[1];
                str_temp = temp[2];
                str_wind = temp[3];
                str_level = temp[4];
                str_time = temp[5];
            }
        }
        setViewWeather();
    }

    private void getWeather(String city) {
        Observable<WeatherModel> flowable = NetWorkManager
                .getInstance().getmWeatherServer().getWeather(city);

        DisposableObserver<WeatherModel> observer =
                new DisposableObserver<WeatherModel>() {
                    @Override
                    public void onNext(WeatherModel value) {
                        if (value != null) {
                            Log.d("521", "onNext: " + value.toString());
                            str_city = city;
                            str_weather = value.getCond_txt();
                            str_temp = value.getTmp();
                            str_wind = value.getWind_dir();
                            str_level = value.getWind_sc()+"级风";
                            str_time = "刚刚更新";
                            setViewWeather();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("521", "onError: " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                };
        flowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(60, TimeUnit.SECONDS)
                .distinct()
                .subscribeWith(observer);
        mDisposables.add(observer);
    }

    public static void main(String... args){
        String s3 = "禅城区,多云,25度,西南风,5~6级,2018-03-28 14:00:00更新";
        String [] temp = null;
        temp = s3.split(",");
        System.out.println(temp.length);
        for (String s:temp){
            System.out.println(s);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposables.clear();
    }

    private void setViewWeather(){
        tv_city.setText(str_city);
        tv_weather.setText(str_weather);
        tv_temp.setText(str_temp);
        tv_wind.setText(str_wind);
        tv_level.setText(str_level);
        tv_time.setText(str_time);
    }

    //add by shipeixian for cancel animation begin
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    //add by shipeixian for cancel animation end
}
