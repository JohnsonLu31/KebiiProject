package com.example.weather

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.R
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import java.util.*
import kotlin.collections.ArrayList

class WeatherMain : AppCompatActivity() {

    private val API_KEY = "ca0e2a321124cebea6d8520ba7323cdf"

    lateinit var btnSerch: Button
    lateinit var etCityName: EditText
    lateinit var iconWeather: ImageView
    lateinit var tvTemp: TextView
    lateinit var tvCity: TextView
    lateinit var lvDailyWeather: ListView

    lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)


        btnSerch = findViewById(R.id.btnSearch)
        etCityName = findViewById(R.id.etCityName)
        iconWeather = findViewById(R.id.iconWeather)
        tvTemp = findViewById(R.id.tvTemp)
        tvCity = findViewById(R.id.tvCity)
        lvDailyWeather = findViewById(R.id.lvDailyWeather)

        btnSerch.setOnClickListener {
            val city = etCityName.text.toString()

            if (city.isEmpty()) {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            } else {
                loadWeatherByCityName(city)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadWeatherByCityName(city: String) {
        Ion.with(this)
            .load("http://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$API_KEY")
            .asJsonObject()
            .setCallback { e, result ->
                if (e != null) {
                    e.printStackTrace()
                    Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show()
                } else {
                    val main: JsonObject = result.get("main").asJsonObject
                    val temp: Double = main.get("temp").asDouble
                    tvTemp.text = "$temp℃"

                    val sys: JsonObject = result.get("sys").asJsonObject
                    val country: String = sys.get("country").asString
                    tvCity.text = "$city, $country"

                    val status = result.get("weather").asJsonArray
                    val description = status.get(0).asJsonObject.get("description").asString

                    //TTS
                    textToSpeech = TextToSpeech(this) { p0 ->
                        if (p0 == TextToSpeech.SUCCESS) {
                            textToSpeech.language = Locale.ENGLISH
                            textToSpeech.setSpeechRate(0.5f)
                            textToSpeech.speak("Today's weather in $city is $description, and the temperature is $temp degree", TextToSpeech.QUEUE_FLUSH, null)
                        }
                    }

                    val weather = result.get("weather").asJsonArray
                    val icon = weather.get(0).asJsonObject.get("icon").asString
                    loadIcon(icon)
                    
                    val coord: JsonObject = result.get("coord").asJsonObject
                    val lon = coord.get("lon").asDouble
                    val lat = coord.get("lat").asDouble
                    loadDailyForecast(lon, lat)
                }
            }
    }

    private fun loadDailyForecast(lon: Double, lat: Double) {
        Ion.with(this)
            .load("https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&exclude=hourly,minutely&units=metric&appid=$API_KEY")
            .asJsonObject()
            .setCallback { e, result ->
                if (e != null) {
                    e.printStackTrace()
                    Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show()
                } else {
                    val weatherList = ArrayList<Weather>()
                    val timeZone: String = result.get("timezone").asString
                    val daily: JsonArray = result.get("daily").asJsonArray
                    val size: Int = daily.size()
                    for (i in 1..size) {
                        val date = daily.get(i-1).asJsonObject.get("dt").asLong
                        val temp = daily.get(i-1).asJsonObject.get("temp").asJsonObject.get("day").asDouble
                        val temptts = daily.get(1).asJsonObject.get("temp").asJsonObject.get("day").asDouble
                        val icon = daily.get(i-1).asJsonObject.get("weather").asJsonArray.get(0).asJsonObject.get("icon").asString
                        val description = daily.get(1).asJsonObject.get("weather").asJsonArray.get(0).asJsonObject.get("description").asString
                        weatherList.add(Weather(date, timeZone, temp, icon))
                        //TTS
                        textToSpeech = TextToSpeech(this) { p0 ->
                            if (p0 == android.speech.tts.TextToSpeech.SUCCESS) {
                                textToSpeech.language = java.util.Locale.ENGLISH
                                textToSpeech.setSpeechRate(0.5f)
                                textToSpeech.speak("And tomorrow's weather will be $description in $temptts  degree", android.speech.tts.TextToSpeech.QUEUE_FLUSH, null)
                            }
                        }
                    }
                    val dailyWeatherAdapter = DailyWeatherAdapter(this, weatherList)
                    lvDailyWeather.adapter = dailyWeatherAdapter



                }
            }
    }


    private fun loadIcon(icon: String) {
        Ion.with(this)
            .load("http://openweathermap.org/img/w/$icon.png")
            .intoImageView(iconWeather)
    }

}
