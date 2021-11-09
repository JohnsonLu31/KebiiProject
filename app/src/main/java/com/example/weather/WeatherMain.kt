package com.example.weather

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.R
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.*
import kotlinx.android.synthetic.main.weather_activity.*
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

    //Nuwa setting-----------------------
    lateinit var mRobotAPI: NuwaRobotAPI
    lateinit var mClientId: IClientId
    //------------------------------------

    //lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)

        btnSerch = findViewById(R.id.btnSearch)
        etCityName = findViewById(R.id.etCityName)
        iconWeather = findViewById(R.id.iconWeather)
        tvTemp = findViewById(R.id.tvTemp)
        tvCity = findViewById(R.id.tvCity)
        lvDailyWeather = findViewById(R.id.lvDailyWeather)

        //Nuwa API--------------------------------------------
        mClientId = IClientId(this.packageName)
        mRobotAPI = NuwaRobotAPI(this, mClientId)

        Log.d("Test", "register EventListener")
        mRobotAPI.registerRobotEventListener(robotEventListener)
        //-----------------------------------------------------

        etCityName.setOnClickListener {
            mRobotAPI.startLocalCommand()
            Log.d("Test", "Start Local Command")
        }

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



                    /*換成中文(保留)*/val weathermain = status.get(0).asJsonObject.get("main").asString
                        var report: String = ""
                        if (weathermain.contains("Sun")) {
                            report = "好天氣"
                        }
                        if (weathermain.contains("Cloud")) {
                            report = "多雲"
                        }
                        if (weathermain.contains("Rain")) {
                            report = "下雨"
                        }
                    /**/

                    /*TTS can't use in Nuwa--------------------------------------------------
                    textToSpeech = TextToSpeech(this) { p0 ->
                        if (p0 == TextToSpeech.SUCCESS) {
                            textToSpeech.language = Locale.ENGLISH
                            textToSpeech.setSpeechRate(0.5f)
                            textToSpeech.speak("today's weather in $city city is $description in $temp degree", TextToSpeech.QUEUE_FLUSH, null)
                        }
                    }-------------------------------------------------------*/

                    //Nuwa TTS-------------------------------------------------------
                    mRobotAPI.startTTS("today's weather in $city city is $description in $temp degree")
                    //---------------------------------------------------------------

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


                        /*換成中文(保留)*/val weathermain = daily.get(i-1).asJsonObject.get("weather").asJsonArray.get(0).asJsonObject.get("main").asString
                            var report: String = ""
                            if (weathermain.contains("Sun")) {
                                report = "好天氣"
                            }
                            if (weathermain.contains("Cloud")) {
                                report = "多雲"
                            }
                            if (weathermain.contains("Rain")) {
                                report = "下雨"
                            }/**/

                        /*TTS
                        textToSpeech = TextToSpeech(this) { p0 ->
                            if (p0 == android.speech.tts.TextToSpeech.SUCCESS) {
                                textToSpeech.language = java.util.Locale.ENGLISH
                                textToSpeech.setSpeechRate(0.5f)
                                textToSpeech.speak("And tomorrow's weather is $description $temptts degree", android.speech.tts.TextToSpeech.QUEUE_FLUSH, null)
                            }
                        }*/
                        if (i == size) {
                            //Nuwa TTS-----------------------------------------------
                            mRobotAPI.startTTS("And tomorrow's weather is $description $temptts degree")
                            //-------------------------------------------------------
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

    //Nuwa---------------------------------------------------------

    override fun onDestroy() {
        super.onDestroy()
        mRobotAPI.release()
    }

    fun prepareGrammarToRobot() {
        val mGrammarData = SimpleGrammarData("example")

        val city  = resources.getStringArray(R.array.city)

        mGrammarData.addSlot(*city)

        mGrammarData.updateBody()

        mRobotAPI.createGrammar(mGrammarData.grammar, mGrammarData.body)

    }

    val robotEventListener = object: RobotEventListener {
        override fun onWikiServiceStart() {
            Log.d("Test", "onWikiServiceStart, robot ready to be control")

            mRobotAPI.registerVoiceEventListener(voiceEventListener)

            prepareGrammarToRobot()

        }

        override fun onWikiServiceStop() {

        }

        override fun onWikiServiceCrash() {

        }

        override fun onWikiServiceRecovery() {

        }

        override fun onStartOfMotionPlay(p0: String?) {

        }

        override fun onPauseOfMotionPlay(p0: String?) {

        }

        override fun onStopOfMotionPlay(p0: String?) {

        }

        override fun onCompleteOfMotionPlay(p0: String?) {

        }

        override fun onPlayBackOfMotionPlay(p0: String?) {

        }

        override fun onErrorOfMotionPlay(p0: Int) {

        }

        override fun onPrepareMotion(p0: Boolean, p1: String?, p2: Float) {

        }

        override fun onCameraOfMotionPlay(p0: String?) {

        }

        override fun onGetCameraPose(
            p0: Float,
            p1: Float,
            p2: Float,
            p3: Float,
            p4: Float,
            p5: Float,
            p6: Float,
            p7: Float,
            p8: Float,
            p9: Float,
            p10: Float,
            p11: Float
        ) {

        }

        override fun onTouchEvent(p0: Int, p1: Int) {

        }

        override fun onPIREvent(p0: Int) {

        }

        override fun onTap(p0: Int) {

        }

        override fun onLongPress(p0: Int) {

        }

        override fun onWindowSurfaceReady() {

        }

        override fun onWindowSurfaceDestroy() {

        }

        override fun onTouchEyes(p0: Int, p1: Int) {

        }

        override fun onRawTouch(p0: Int, p1: Int, p2: Int) {

        }

        override fun onFaceSpeaker(p0: Float) {

        }

        override fun onActionEvent(p0: Int, p1: Int) {

        }

        override fun onDropSensorEvent(p0: Int) {

        }

        override fun onMotorErrorEvent(p0: Int, p1: Int) {

        }
    }

    val voiceEventListener = object: VoiceEventListener {
        override fun onWakeup(p0: Boolean, p1: String?, p2: Float) {

        }

        override fun onTTSComplete(p0: Boolean) {

        }

        override fun onSpeechRecognizeComplete(
            p0: Boolean,
            p1: VoiceEventListener.ResultType?,
            p2: String?
        ) {

        }

        override fun onSpeech2TextComplete(p0: Boolean, p1: String?) {
            Log.d("Test", "onSpeech2TextComplete: $p0, json: $p1")
        }

        override fun onMixUnderstandComplete(p0: Boolean, p1: VoiceEventListener.ResultType?, p2: String) {

            val result_string = VoiceResultJsonParser.parseVoiceResult(p2)

            if (p1 == VoiceEventListener.ResultType.LOCAL_COMMAND) {
                loadWeatherByCityName(result_string)
            }


        }

        override fun onSpeechState(
            p0: VoiceEventListener.ListenType?,
            p1: VoiceEventListener.SpeechState?
        ) {

        }

        override fun onSpeakState(
            p0: VoiceEventListener.SpeakType?,
            p1: VoiceEventListener.SpeakState?
        ) {

        }

        override fun onGrammarState(p0: Boolean, p1: String) {
            mRobotAPI.startMixUnderstand()
            Log.d("Test", "GrammerState $p1")
        }

        override fun onListenVolumeChanged(p0: VoiceEventListener.ListenType?, p1: Int) {

        }

        override fun onHotwordChange(
            p0: VoiceEventListener.HotwordState?,
            p1: VoiceEventListener.HotwordType?,
            p2: String?
        ) {

        }

    }
    //-----------------------------------------------------------------------------
}
