package me.example.weather.project.request;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import me.example.weather.project.model.WeatherModel;

/**
 * Created by Administrator on 2018/3/16.
 */

public class ResultJsonDeser implements JsonDeserializer<WeatherModel> {
    @Override
    public WeatherModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        WeatherModel model = new WeatherModel();
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonArray jsonArray = jsonObject.get("HeWeather6").getAsJsonArray();
            JsonObject j = jsonArray.get(0).getAsJsonObject();
            String status = j.get("status").getAsString();
            Log.d("521", "deserialize: " + status);
            if (status.equals("ok")) {
                model = new Gson().fromJson(j.get("now").toString(), WeatherModel.class);
                return model;
            }
        }
        return null;
    }
}
