package com.example.chatBox.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.chatBox.data.Message
import com.example.chatBox.util.BotResponse
import com.example.chatBox.util.Constants.OPEN_GOOGLE
import com.example.chatBox.util.Constants.OPEN_SEARCH
import com.example.chatBox.util.Constants.RECEIVE_ID
import com.example.chatBox.util.Constants.SEND_ID
import com.example.chatBox.util.Time
import com.example.chatox.ui.MessagingAdapter
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.NuwaRobotAPI
import com.nuwarobotics.service.agent.RobotEventListener
import com.nuwarobotics.service.agent.VoiceEventListener
import com.nuwarobotics.service.agent.VoiceResultJsonParser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.chatbox_main.*
import kotlinx.coroutines.*
import java.util.*

class ChatBoxActivity : AppCompatActivity() {

    //NUWA API
    lateinit var mRobotAPI: NuwaRobotAPI
    lateinit var mClientId: IClientId

    private lateinit var messagingadapter: MessagingAdapter
    private val botlist = listOf("Peter", "Francesca", "Luigi", "Igor")
    lateinit var textToSpeech: TextToSpeech

    val random = (0..3).random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatbox_main)

        //NUWA SETTING
        /*mClientId = IClientId(this.packageName)
        mRobotAPI = NuwaRobotAPI(this, mClientId)

        Log.d("Test", "register EventListener")
        mRobotAPI.registerRobotEventListener(robotEventListener)*/


        recycleView()

        clickEvents()

        customMessage("Hello! Today you're speaking with ${botlist[random]}, how may I help?")



        //TTS can't use in NUWA system
        textToSpeech = TextToSpeech(applicationContext) { p0 ->
            if (p0 == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.ENGLISH
                textToSpeech.setSpeechRate(0.5f)
                textToSpeech.speak("Hello! Today you're speaking with ${botlist[random]}, how may I help?", TextToSpeech.QUEUE_FLUSH, null)
            }
        }

    }

    private fun clickEvents() {
        btn_send.setOnClickListener {
            sendMessage()
        }

        et_message.setOnClickListener {

            GlobalScope.launch {
                delay(1000)
                withContext(Dispatchers.Main) {
                    rv_messages.scrollToPosition(messagingadapter.itemCount - 1)
                }
            }
        }
    }

    private fun recycleView() {
        messagingadapter = MessagingAdapter()
        rv_messages.adapter = messagingadapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun sendMessage() {
        val message = et_message.text.toString()
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            et_message.setText("")

            messagingadapter.insertMessage(Message(message, SEND_ID, timeStamp))
            rv_messages.scrollToPosition(messagingadapter.itemCount - 1)

            botResponse(message)
        }
    }

    private fun botResponse(message: String) {
        val timeStamp = Time.timeStamp()

        GlobalScope.launch {
            delay(1000)

            withContext(Dispatchers.Main) {
                val response = BotResponse.basicResponse(message)

                messagingadapter.insertMessage(Message(response, RECEIVE_ID, timeStamp))
                rv_messages.scrollToPosition(messagingadapter.itemCount - 1)

                when (response) {
                    OPEN_GOOGLE -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("https://www.google.com/")
                        startActivity(site)
                    }
                    OPEN_SEARCH -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        val searchTerm: String? = message.substringAfter("search")
                        site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                        startActivity(site)
                    }
                    else -> {
                        //NUWA TTS
                        mRobotAPI.startTTS(response)
                    }
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(messagingadapter.itemCount - 1)
            }
        }
    }

    private fun customMessage(messsage: String) {
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val timeStamp = Time.timeStamp()
                messagingadapter.insertMessage(Message(messsage, RECEIVE_ID, timeStamp))

                rv_messages.scrollToPosition(messagingadapter.itemCount - 1)
            }
        }
    }


    //NUWA
    /*override fun onDestroy() {
        super.onDestroy()
        mRobotAPI.release()
    }

    val robotEventListener = object: RobotEventListener {
        override fun onWikiServiceStart() {
            Log.d("Test", "onWikiServiceStart, robot ready to be control")

            mRobotAPI.registerVoiceEventListener(voiceEventListener)

            mRobotAPI.startTTS("Hello! Today you're speaking with ${botlist[random]}, how may I help?")
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

        }

        override fun onListenVolumeChanged(p0: VoiceEventListener.ListenType?, p1: Int) {

        }

        override fun onHotwordChange(
            p0: VoiceEventListener.HotwordState?,
            p1: VoiceEventListener.HotwordType?,
            p2: String?
        ) {

        }

    }*/


}