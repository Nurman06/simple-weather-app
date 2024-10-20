package com.example.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.weatherapp.data.remote.response.HourItem
import com.example.weatherapp.data.remote.response.WeatherResponse
import com.example.weatherapp.ui.WeatherScreen
import com.example.weatherapp.ui.WeatherUiState
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.android.gms.location.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val showPermissionDialog = mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLastLocation()
        } else {
            showPermissionDeniedDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherScreen(viewModel) { navigateToHourlyForecastDetail() }

                    if (showPermissionDialog.value) {
                        AlertDialog(
                            onDismissRequest = { showPermissionDialog.value = false },
                            title = { Text("Location Permission Required") },
                            text = { Text("This app needs location permission to provide accurate weather information. Please grant the permission in app settings.") },
                            confirmButton = {
                                Button(onClick = {
                                    showPermissionDialog.value = false
                                    openAppSettings()
                                }) {
                                    Text("Open Settings")
                                }
                            },
                            dismissButton = {
                                Button(onClick = {
                                    showPermissionDialog.value = false
                                    finish()
                                }) {
                                    Text("Exit App")
                                }
                            }
                        )
                    }
                }
            }
        }

        requestLocationPermission()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun navigateToHourlyForecastDetail() {
        val currentState = viewModel.uiState.value
        if (currentState is WeatherUiState.Success) {
            val next24Hours = getNext24Hours(currentState.data)
            val intent = Intent(this, HourlyForecastDetailActivity::class.java).apply {
                putExtra("hourly_data", next24Hours.toTypedArray())
            }
            startActivity(intent)
        } else {
            // Handle error state
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNext24Hours(weatherData: WeatherResponse): List<HourItem> {
        val currentTime = LocalDateTime.now()
        val startHour = currentTime.plusHours(1).withMinute(0).withSecond(0).withNano(0)
        val endHour = startHour.plusHours(23)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        val allHours = weatherData.forecast.forecastday.flatMap { it.hour }

        return allHours.filter { hourItem ->
            val itemTime = LocalDateTime.parse(hourItem.time, formatter)
            itemTime in startHour..endHour
        }
    }

    override fun onResume() {
        super.onResume()
        if (isLocationPermissionGranted()) {
            getLastLocation()
        }
    }

    private fun requestLocationPermission() {
        when {
            isLocationPermissionGranted() -> getLastLocation()
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> showPermissionDeniedDialog()
            else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showPermissionDeniedDialog() {
        showPermissionDialog.value = true
    }

    private fun openAppSettings() {
        val intent = android.content.Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            android.net.Uri.fromParts("package", packageName, null)
        )
        startActivity(intent)
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun getLastLocation() {
        if (isLocationPermissionGranted()) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let { viewModel.fetchWeatherData(it) } ?: requestLocationUpdates()
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(15000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.firstOrNull()?.let { location ->
                    viewModel.fetchWeatherData(location)
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        if (isLocationPermissionGranted()) {
            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            requestLocationPermission()
        }
    }
}