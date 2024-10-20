package com.example.weatherapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.R
import com.example.weatherapp.data.remote.response.HourItem
import com.example.weatherapp.data.remote.response.WeatherResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel, onMoreClick: () -> Unit) {
    val uiState by viewModel.uiState.observeAsState(WeatherUiState.Loading)

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()

        when (uiState) {
            is WeatherUiState.Loading -> LoadingIndicator()
            is WeatherUiState.Error -> ErrorMessage((uiState as WeatherUiState.Error).message)
            is WeatherUiState.Success -> {
                val weatherData = (uiState as WeatherUiState.Success).data
                WeatherContent(weatherData, onMoreClick)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BackgroundImage() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = getBackgroundImage()),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Gradient overlay
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

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = Color.White
        )
    }
}

@Composable
fun ErrorMessage(error: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        Text(
            text = error,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherContent(weatherData: WeatherResponse, onMoreClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RunningText(
            text = "${weatherData.location.name}, ${weatherData.location.region}, ${weatherData.location.country}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))

        WeatherInfoCard(weatherData)

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(getWeatherDetailItems(weatherData)) { item ->
                WeatherDetailCard(item)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HourlyForecastCard(weatherData, onMoreClick)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherInfoCard(weatherData: WeatherResponse) {
    val currentDate = LocalDateTime.now()
    val locale = Locale("id", "ID")

    val day = currentDate.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, locale)
    val date = currentDate.dayOfMonth
    val month = currentDate.month.getDisplayName(java.time.format.TextStyle.SHORT, locale)
    val hour = currentDate.hour
    val minute = currentDate.minute

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Date and Time
            Text(
                text = "$day, $date $month",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = String.format(locale, "%02d:%02d", hour, minute),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Temperature and Weather Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Temperature
                Text(
                    text = "${weatherData.current.tempC}°C",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )

                // Weather Icon and Condition
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val iconUrl = "https:${weatherData.current.condition.icon}"
                    Image(
                        painter = rememberAsyncImagePainter(iconUrl),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = weatherData.current.condition.text,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Weather Information
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                WeatherInfoRow("Terasa seperti", "${weatherData.current.feelslikeC}°C")
                WeatherInfoRow("Tinggi", "${weatherData.forecast.forecastday[0].day.maxtempC}°C")
                WeatherInfoRow("Rendah", "${weatherData.forecast.forecastday[0].day.mintempC}°C")
                WeatherInfoRow("Angin", "${weatherData.current.windDir}, ${weatherData.current.windKph} km/h")
            }
        }
    }
}

@Composable
fun WeatherInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WeatherDetailCard(item: Triple<String, String, String>) {
    Card(
        modifier = Modifier
            .size(120.dp, 100.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.third),
                contentDescription = item.first,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.second,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.first,
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

fun getWeatherDetailItems(weatherData: WeatherResponse): List<Triple<String, String, String>> {
    val baseIconUrl = "https:" // WeatherAPI.com icons start with //
    val defaultIcon = "${baseIconUrl}${weatherData.current.condition.icon}" // Use the current condition icon as default

    return listOf(
        createWeatherDetailItem("Kelembapan", "${weatherData.current.humidity}%", "${baseIconUrl}//cdn.weatherapi.com/weather/64x64/day/143.png"),
        createWeatherDetailItem("Titik Embun", "${weatherData.current.dewpointC}°C", defaultIcon),
        createWeatherDetailItem("Indeks UV", weatherData.current.uv.toString(), "${baseIconUrl}//cdn.weatherapi.com/weather/64x64/day/113.png"),
        createWeatherDetailItem("Visibilitas", "${weatherData.current.visKm} km", defaultIcon),
        createWeatherDetailItem("Tutupan Awan", "${weatherData.current.cloud}%", "${baseIconUrl}//cdn.weatherapi.com/weather/64x64/day/119.png"),
        createWeatherDetailItem("Tekanan", "${weatherData.current.pressureMb} mb", defaultIcon)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun getNext24Hours(weatherData: WeatherResponse): List<HourItem> {
    val currentTime = LocalDateTime.now()
    val startHour = currentTime.plusHours(1).withMinute(0).withSecond(0).withNano(0)
    val endHour = startHour.plusHours(23) // 23 jam setelah startHour untuk total 24 jam termasuk startHour

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    val allHours = weatherData.forecast.forecastday.flatMap { it.hour }

    return allHours.filter { hourItem ->
        val itemTime = LocalDateTime.parse(hourItem.time, formatter)
        itemTime in startHour..endHour
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyForecastCard(weatherData: WeatherResponse, onMoreClick: () -> Unit) {
    val next24Hours = remember { getNext24Hours(weatherData) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Prakiraan 24 Jam Ke Depan",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(next24Hours) { hourData ->
                        HourlyForecastItem(hourData)
                    }
                }
            }

            Text(
                text = "Lebih",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clickable { onMoreClick() }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyForecastItem(hourData: HourItem) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val itemTime = LocalDateTime.parse(hourData.time, formatter)
    val timeText = itemTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    val rainIconUrl = "https://cdn.weatherapi.com/weather/64x64/day/302.png" // Ikon awan dengan hujan

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Text(
            text = timeText,
            color = Color.White,
            fontSize = 12.sp
        )
        Image(
            painter = rememberAsyncImagePainter("https:${hourData.condition.icon}"),
            contentDescription = hourData.condition.text,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = "${hourData.tempC}°C",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(rainIconUrl),
                contentDescription = "Chance of rain",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${hourData.chanceOfRain}%",
                color = Color.Cyan,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun RunningText(
    text: String,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    color: Color
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val textLayoutResult = remember {
        textMeasurer.measure(
            text = text,
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = color
            )
        )
    }

    val textWidth = textLayoutResult.size.width.toFloat()
    val spacing = with(density) { 100.dp.toPx() }
    val totalWidth = textWidth + spacing

    val animationDuration = (totalWidth / 100).toInt() * 1000

    val infiniteTransition = rememberInfiniteTransition(label = "running_text_animation")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -totalWidth,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "running_text_offset"
    )

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(30.dp)
            .clipToBounds()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawTextOnCanvas(textLayoutResult, offset, color, true)
            drawTextOnCanvas(textLayoutResult, offset + totalWidth, color, true)
        }
    }
}

private fun DrawScope.drawTextOnCanvas(textLayoutResult: TextLayoutResult, offset: Float, color: Color, withShadow: Boolean = false) {
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            textSize = textLayoutResult.size.height.toFloat()
            this.color = color.toArgb()
            textAlign = android.graphics.Paint.Align.LEFT
            if (withShadow) {
                setShadowLayer(5f, 0f, 2f, Color.Black.toArgb())
            }
        }

        drawText(
            textLayoutResult.layoutInput.text.toString(),
            offset,
            textLayoutResult.size.height.toFloat(),
            paint
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

fun createWeatherDetailItem(title: String, value: String, iconUrl: String): Triple<String, String, String> {
    return Triple(title, value, iconUrl)
}