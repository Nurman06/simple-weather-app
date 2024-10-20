package com.example.weatherapp.data.remote.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WeatherResponse(

	@field:SerializedName("current")
	val current: Current,

	@field:SerializedName("location")
	val location: Location,

	@field:SerializedName("forecast")
	val forecast: Forecast
)

data class HourItem(
	@field:SerializedName("feelslike_c")
	val feelslikeC: Double,

	@field:SerializedName("feelslike_f")
	val feelslikeF: Double,

	@field:SerializedName("wind_degree")
	val windDegree: Int,

	@field:SerializedName("windchill_f")
	val windchillF: Double?, // Ubah ke Double? jika bisa null

	@field:SerializedName("windchill_c")
	val windchillC: Double?, // Ubah ke Double? jika bisa null

	@field:SerializedName("temp_c")
	val tempC: Double?, // Ubah ke Double? jika bisa null

	@field:SerializedName("temp_f")
	val tempF: Double?, // Ubah ke Double? jika bisa null

	@field:SerializedName("cloud")
	val cloud: Int,

	@field:SerializedName("wind_kph")
	val windKph: Double,

	@field:SerializedName("wind_mph")
	val windMph: Double,

	@field:SerializedName("snow_cm")
	val snowCm: Int,

	@field:SerializedName("humidity")
	val humidity: Int,

	@field:SerializedName("dewpoint_f")
	val dewpointF: Double?, // Ubah ke Double? jika bisa null

	@field:SerializedName("will_it_rain")
	val willItRain: Int,

	@field:SerializedName("uv")
	val uv: Double,

	@field:SerializedName("heatindex_f")
	val heatindexF: Double?, // Ubah ke Double? jika bisa null

	@field:SerializedName("dewpoint_c")
	val dewpointC: Double?, // Ubah ke Double? jika bisa null

	@field:SerializedName("is_day")
	val isDay: Int,

	@field:SerializedName("precip_in")
	val precipIn: Double,

	@field:SerializedName("heatindex_c")
	val heatindexC: Double?, // Ubah ke Double? jika bisa null

	@field:SerializedName("wind_dir")
	val windDir: String,

	@field:SerializedName("gust_mph")
	val gustMph: Double,

	@field:SerializedName("pressure_in")
	val pressureIn: Double?, // Ubah ke Double? jika bisa null

	@field:SerializedName("chance_of_rain")
	val chanceOfRain: Int,

	@field:SerializedName("gust_kph")
	val gustKph: Double,

	@field:SerializedName("precip_mm")
	val precipMm: Double,

	@field:SerializedName("condition")
	val condition: Condition,

	@field:SerializedName("will_it_snow")
	val willItSnow: Int,

	@field:SerializedName("vis_km")
	val visKm: Double,

	@field:SerializedName("time_epoch")
	val timeEpoch: Int,

	@field:SerializedName("time")
	val time: String,

	@field:SerializedName("chance_of_snow")
	val chanceOfSnow: Int,

	@field:SerializedName("pressure_mb")
	val pressureMb: Int,

	@field:SerializedName("vis_miles")
	val visMiles: Double
) : Serializable

data class Condition(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("icon")
	val icon: String,

	@field:SerializedName("text")
	val text: String
) : Serializable

data class Current(

	@field:SerializedName("feelslike_c")
	val feelslikeC: Any,

	@field:SerializedName("feelslike_f")
	val feelslikeF: Any,

	@field:SerializedName("wind_degree")
	val windDegree: Int,

	@field:SerializedName("windchill_f")
	val windchillF: Any,

	@field:SerializedName("windchill_c")
	val windchillC: Any,

	@field:SerializedName("last_updated_epoch")
	val lastUpdatedEpoch: Int,

	@field:SerializedName("temp_c")
	val tempC: Any,

	@field:SerializedName("temp_f")
	val tempF: Any,

	@field:SerializedName("cloud")
	val cloud: Int,

	@field:SerializedName("wind_kph")
	val windKph: Any,

	@field:SerializedName("wind_mph")
	val windMph: Double,

	@field:SerializedName("humidity")
	val humidity: Int,

	@field:SerializedName("dewpoint_f")
	val dewpointF: Any,

	@field:SerializedName("uv")
	val uv: Double,

	@field:SerializedName("last_updated")
	val lastUpdated: String,

	@field:SerializedName("heatindex_f")
	val heatindexF: Any,

	@field:SerializedName("dewpoint_c")
	val dewpointC: Any,

	@field:SerializedName("is_day")
	val isDay: Int,

	@field:SerializedName("precip_in")
	val precipIn: Double,

	@field:SerializedName("heatindex_c")
	val heatindexC: Any,

	@field:SerializedName("wind_dir")
	val windDir: String,

	@field:SerializedName("gust_mph")
	val gustMph: Any,

	@field:SerializedName("pressure_in")
	val pressureIn: Any,

	@field:SerializedName("gust_kph")
	val gustKph: Any,

	@field:SerializedName("precip_mm")
	val precipMm: Any,

	@field:SerializedName("condition")
	val condition: Condition,

	@field:SerializedName("vis_km")
	val visKm: Int,

	@field:SerializedName("pressure_mb")
	val pressureMb: Int,

	@field:SerializedName("vis_miles")
	val visMiles: Int
)

data class Astro(

	@field:SerializedName("moonset")
	val moonset: String,

	@field:SerializedName("moon_illumination")
	val moonIllumination: Int,

	@field:SerializedName("sunrise")
	val sunrise: String,

	@field:SerializedName("moon_phase")
	val moonPhase: String,

	@field:SerializedName("sunset")
	val sunset: String,

	@field:SerializedName("is_moon_up")
	val isMoonUp: Int,

	@field:SerializedName("is_sun_up")
	val isSunUp: Int,

	@field:SerializedName("moonrise")
	val moonrise: String
)

data class ForecastdayItem(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("astro")
	val astro: Astro,

	@field:SerializedName("date_epoch")
	val dateEpoch: Int,

	@field:SerializedName("hour")
	val hour: List<HourItem>,

	@field:SerializedName("day")
	val day: Day
)

data class Location(

	@field:SerializedName("localtime")
	val localtime: String,

	@field:SerializedName("country")
	val country: String,

	@field:SerializedName("localtime_epoch")
	val localtimeEpoch: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("lon")
	val lon: Any,

	@field:SerializedName("region")
	val region: String,

	@field:SerializedName("lat")
	val lat: Any,

	@field:SerializedName("tz_id")
	val tzId: String
)

data class Day(

	@field:SerializedName("avgvis_km")
	val avgvisKm: Double,

	@field:SerializedName("uv")
	val uv: Double,

	@field:SerializedName("avgtemp_f")
	val avgtempF: Any,

	@field:SerializedName("avgtemp_c")
	val avgtempC: Any,

	@field:SerializedName("daily_chance_of_snow")
	val dailyChanceOfSnow: Int,

	@field:SerializedName("maxtemp_c")
	val maxtempC: Any,

	@field:SerializedName("maxtemp_f")
	val maxtempF: Any,

	@field:SerializedName("mintemp_c")
	val mintempC: Any,

	@field:SerializedName("avgvis_miles")
	val avgvisMiles: Int,

	@field:SerializedName("daily_will_it_rain")
	val dailyWillItRain: Int,

	@field:SerializedName("mintemp_f")
	val mintempF: Any,

	@field:SerializedName("totalprecip_in")
	val totalprecipIn: Double,

	@field:SerializedName("totalsnow_cm")
	val totalsnowCm: Int,

	@field:SerializedName("avghumidity")
	val avghumidity: Int,

	@field:SerializedName("condition")
	val condition: Condition,

	@field:SerializedName("maxwind_kph")
	val maxwindKph: Any,

	@field:SerializedName("maxwind_mph")
	val maxwindMph: Any,

	@field:SerializedName("daily_chance_of_rain")
	val dailyChanceOfRain: Int,

	@field:SerializedName("totalprecip_mm")
	val totalprecipMm: Any,

	@field:SerializedName("daily_will_it_snow")
	val dailyWillItSnow: Int
)

data class Forecast(

	@field:SerializedName("forecastday")
	val forecastday: List<ForecastdayItem>
)
