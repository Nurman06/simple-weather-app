package com.example.weatherapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.data.remote.response.HourItem
import com.example.weatherapp.ui.theme.WeatherAppTheme
import java.time.LocalDateTime
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import java.text.SimpleDateFormat
import java.util.*

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

    @Composable
    fun HourlyForecastList(hourlyData: Array<HourItem>) {
        // Implementasikan LazyColumn untuk menampilkan data per jam
        LazyColumn {
            items(hourlyData) { hourItem ->
                HourlyForecastCard(hourItem)
            }
        }
    }

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
                // Date and time row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = formatTime(hourItem.time),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = hourItem.time.substring(11, 16), // Extract HH:mm
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

                // Weather info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Icon and condition
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

                    // Right: Weather details
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Hujan: ${hourItem.chanceOfRain}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Kelembapan: ${hourItem.humidity}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Angin: ${hourItem.windKph} km/h",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Visibilitas: ${hourItem.visKm} km",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    private fun formatTime(timeString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE, d MMM", Locale("id", "ID"))

        return try {
            val date = inputFormat.parse(timeString)
            outputFormat.format(date as Date)
        } catch (e: Exception) {
            e.printStackTrace()
            timeString
        }
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