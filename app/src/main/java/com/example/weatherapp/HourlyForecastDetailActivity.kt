package com.example.weatherapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.data.remote.response.HourItem
import com.example.weatherapp.ui.theme.WeatherAppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HourlyForecastDetailActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HourlyForecastDetailScreen()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun HourlyForecastDetailScreen() {
        Box(modifier = Modifier.fillMaxSize()) {
            BackgroundImage()
            // Mengambil data per jam yang dikirim dari MainActivity
            val hourlyData = intent.getSerializableExtra("hourly_data") as Array<HourItem>?

            // Implementasikan RecyclerView atau LazyColumn di sini untuk menampilkan hourlyData
            if (hourlyData != null) {
                HourlyForecastList(hourlyData)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun HourlyForecastList(hourlyData: Array<HourItem>) {
        LazyColumn {
            items(hourlyData) { hourItem ->
                HourlyForecastCard(hourItem)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun HourlyForecastCard(hourItem: HourItem) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = formatDate(hourItem.time),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = formatTime(hourItem.time),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "${hourItem.tempC}°C",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter("https:${hourItem.condition.icon}"),
                            contentDescription = hourItem.condition.text,
                            modifier = Modifier.size(50.dp)
                        )
                        Text(
                            text = hourItem.condition.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        WeatherInfoText("Hujan", "${hourItem.chanceOfRain}%")
                        WeatherInfoText("Kelembapan", "${hourItem.humidity}%")
                        WeatherInfoText("Angin", "${hourItem.windKph} km/h")
                        WeatherInfoText("Visibilitas", "${hourItem.visKm} km")
                        WeatherInfoText("Pengendapan", "${hourItem.precipMm} mm")
                        WeatherInfoText("Titik embun", "${hourItem.dewpointC}°C")
                        WeatherInfoText("Salju", "${hourItem.snowCm} cm")
                        WeatherInfoText("Arah angin", hourItem.windDir)
                        WeatherInfoText("Tutupan awan", "${hourItem.cloud}%")
                        WeatherInfoText("Indeks UV", hourItem.uv.toString())
                    }
                }
            }
        }
    }

    @Composable
    fun WeatherInfoText(label: String, value: String) {
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDate(timeString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTime = LocalDateTime.parse(timeString, formatter)
        return dateTime.format(DateTimeFormatter.ofPattern("EEE, d MMM", Locale("id", "ID")))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatTime(timeString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTime = LocalDateTime.parse(timeString, formatter)
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun BackgroundImage() {
        // Implementasikan gambar latar belakang
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = getBackgroundImage()),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Overlay gradien
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun getBackgroundImage(): Int {
        val currentHour = LocalDateTime.now().hour

        return when (currentHour) {
            in 0..5 -> R.drawable.early_morning
            else -> R.drawable.night_clear
        }
    }
}