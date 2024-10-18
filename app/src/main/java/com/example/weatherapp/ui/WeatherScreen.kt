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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.R
import com.example.weatherapp.data.remote.response.WeatherResponse
import java.util.Locale
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.ui.text.style.TextAlign
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.observeAsState(WeatherUiState.Loading)

    Box(modifier = Modifier.fillMaxSize()) {
        // Selalu tampilkan latar belakang
        BackgroundImage()

        // Konten di atas latar belakang
        when (uiState) {
            is WeatherUiState.Loading -> LoadingIndicator()
            is WeatherUiState.Error -> ErrorMessage((uiState as WeatherUiState.Error).message)
            is WeatherUiState.Success -> {
                val weatherData = (uiState as WeatherUiState.Success).data
                WeatherContent(weatherData)
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
fun WeatherContent(weatherData: WeatherResponse) {
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
        TransparentWeatherCard(weatherData)
        Spacer(modifier = Modifier.height(16.dp))
        DailyTemperatureRange(
            maxTempC = weatherData.forecast.forecastday[0].day.maxtempC,
            minTempC = weatherData.forecast.forecastday[0].day.mintempC,
            feelsLikeC = weatherData.current.feelslikeC
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransparentWeatherCard(weatherData: WeatherResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CurrentDateTime()
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = weatherData.current.condition.text,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    AsyncImage(
                        model = "https:${weatherData.current.condition.icon}",
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${weatherData.current.tempC}°C",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.White
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentDateTime() {
    val context = LocalContext.current
    val currentDateTime = remember { mutableStateOf(LocalDateTime.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale("id", "ID"))
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    DisposableEffect(context) {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                currentDateTime.value = LocalDateTime.now()
            }
        }

        context.registerReceiver(receiver, intentFilter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Column {
        Text(
            text = currentDateTime.value.format(dateFormatter).capitalize(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Text(
            text = currentDateTime.value.format(timeFormatter),
            fontSize = 14.sp,
            color = Color.White
        )
    }
}

fun String.capitalize(): String {
    return this.split(" ").joinToString(" ") { it.capitalize(Locale.getDefault()) }
}

@Composable
fun DailyTemperatureRange(maxTempC: Any, minTempC: Any, feelsLikeC: Any) {
    Text(
        text = "Day $maxTempC°C / Night $minTempC°C",
        fontSize = 16.sp,
        color = Color.White
    )
    Text(
        text = "Feels like $feelsLikeC°C",
        fontSize = 16.sp,
        color = Color.White
    )
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