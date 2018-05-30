package me.example.weather.project.request;

import io.reactivex.Observable;
import me.example.weather.project.model.WeatherModel;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2018/3/15.
 */

public interface weatherServer {
    @FormUrlEncoded
    @POST("/s6/weather?key=3f6e463c945347cb94f6be990a9969b8")
    Observable<WeatherModel> getWeather(@Field("location") String location);
}
