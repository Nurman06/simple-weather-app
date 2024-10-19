package com.example.weatherapp.ui

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.response.WeatherResponse
import com.example.weatherapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData<WeatherUiState>()
    val uiState: LiveData<WeatherUiState> = _uiState

    fun fetchWeatherData(location: Location) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val response = ApiConfig.getApiService().getHourlyWeather(
                    apiKey = "6050a955c1c148458d9120239241710",
                    location = "${location.latitude},${location.longitude}",
                    days = 2
                )
                _uiState.value = WeatherUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}