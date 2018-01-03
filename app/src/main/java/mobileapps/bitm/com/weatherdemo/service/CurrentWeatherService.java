package mobileapps.bitm.com.weatherdemo.service;

import mobileapps.bitm.com.weatherdemo.model.WeatherResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Mobile App Develop on 1/2/2018.
 */

public interface CurrentWeatherService {
    @GET
    Call<WeatherResponse> getWeatherResponse(@Url String url);
}
