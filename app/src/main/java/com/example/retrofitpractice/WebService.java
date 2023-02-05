package com.example.retrofitpractice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WebService {
    @GET("/v1/current.json?")
    Call<WeatherModel> products(@Query("key") String apiKey,
                                      @Query("q") String city,
                                      @Query("aqi") String aqi);
}
