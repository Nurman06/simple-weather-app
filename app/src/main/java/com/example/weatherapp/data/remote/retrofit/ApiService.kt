package com.example.weatherapp.data.remote.retrofit

import com.example.weatherapp.data.remote.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("forecast.json")
    suspend fun getHourlyWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int,
        @Query("hour") hour: Int? = null
    ): WeatherResponse
}