package com.example.combindallapp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.FILL_IN_ACTION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Menu
import android.widget.*
import com.example.R
import com.example.alarmClock.AlarmClockMain
import com.example.chatBox.ui.ChatBoxActivity
import com.example.musicplayer.ListMusicActivity
import com.example.vedioview.ListVideoActivity
import com.example.weather.WeatherMain
import com.example.youtubevideo.YoututbevideoMainActivity
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<*>

    private var RECOGNIZER_RESULT = 11

    //Nuwa API
    lateinit var mRobotAPI: NuwaRobotAPI
    lateinit var mClientId: IClientId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Nuwa setting--------------------------------------------
        mClientId = IClientId(this.packageName)
        mRobotAPI = NuwaRobotAPI(this, mClientId)

        Log.d("Test", "register EventListener")
        mRobotAPI.registerRobotEventListener(robotEventListener)
        //---------------------------------------------------------

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.App)
        )
        lv_listView.adapter = adapter
        lv_listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                when (position) {
                    0 -> startActivity(Intent(this, WeatherMain::class.java))
                    1 -> startActivity(Intent(this, AlarmClockMain::class.java))
                    2 -> startActivity(Intent(this, ListMusicActivity::class.java))
                    3 -> startActivity(Intent(this, ListVideoActivity::class.java))
                    4 -> startActivity(Intent(this, ChatBoxActivity::class.java))
                    5 -> startActivity(Intent(this, YoututbevideoMainActivity::class.java))
                }
            }
        fb_floatbutton.setOnClickListener {
            mRobotAPI.startLocalCommand()
            Log.d("Test", "Start Local Command")
        }
    }

    //TTS which Nuwa can't use
    private fun speechToText() {
        val speechintent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechintent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechintent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text")
        startActivityForResult(speechintent, RECOGNIZER_RESULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches?.get(0).toString().contains("weather")) {
                startActivity(Intent(this, WeatherMain::class.java))
            }
            if (matches?.get(0).toString().contains("alarm")) {
                startActivity(Intent(this, AlarmClockMain::class.java))
            }
            if (matches?.get(0).toString().contains("music")) {
                startActivity(Intent(this, ListMusicActivity::class.java))
            }
            if (matches?.get(0).toString().contains("video")) {
                startActivity(Intent(this, ListVideoActivity::class.java))
            }
            if (matches?.get(0).toString().contains("chat")) {
                startActivity(Intent(this, ChatBoxActivity::class.java))
            }
            if (matches?.get(0).toString().contains("youtube")) {
                startActivity(Intent(this, YoututbevideoMainActivity::class.java))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        val search = menu?.findItem(R.id.nav_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search Something!"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    //Nuwa------------------------------------------------------------------------------------

    override fun onDestroy() {
        super.onDestroy()
        mRobotAPI.release()
    }

    @SuppressLint("ResourceType")
    fun prepareGrammarToRobot() {
        val mGrammarData = SimpleGrammarData("example")

        val app = resources.getStringArray(R.array.Application_name)

        mGrammarData.addSlot(*app)

        mGrammarData.updateBody()

        mRobotAPI.createCrammer(mGrammarData.grammar, mGrammarData.body)
    }

    val robotEventListener = object : RobotEventListener {
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

        override fun onMixUnderstandComplete(p0: Boolean, p1: VoiceEventListener.ResultType?, p2: String?) {

            val result_string = VoiceResultJsonParser.parseVoiceResult(p2)

            if (result_string.contains("Weather")) {
                startActivity(Intent(applicationContext, WeatherMain::class.java))
            }
            if (result_string.contains("Alarm")) {
                startActivity(Intent(applicationContext, AlarmClockMain::class.java))
            }
            if (result_string.contains("Music")) {
                startActivity(Intent(applicationContext, ListMusicActivity::class.java))
            }
            if (result_string.contains("Video")) {
                startActivity(Intent(applicationContext, ListVideoActivity::class.java))
            }
            if (result_string.contains("Chat")) {
                startActivity(Intent(applicationContext, ChatBoxActivity::class.java))
            }
            if (result_string.contains("Youtube")) {
                startActivity(Intent(applicationContext, YoututbevideoMainActivity::class.java))
            }

            Log.d("Test", "onMixUnderstandComplete")
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

        override fun onGrammarState(p0: Boolean, p1: String?) {
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
    //---------------------------------------------------------------
}

