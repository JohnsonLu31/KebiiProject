package com.example.chatBox.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.chatbox_main.*
import kotlinx.coroutines.*
import java.util.*

class ChatBoxActivity : AppCompatActivity() {

    private lateinit var messagingadapter: MessagingAdapter
    private val botlist = listOf("Peter", "Francesca", "Luigi", "Igor")
    lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatbox_main)

        recycleView()

        clickEvents()

        val random = (0..3).random()
        customMessage("Hello! Today you're speaking with ${botlist[random]}, how may I help?")

        //TTS
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
                        //TTS
                        textToSpeech = TextToSpeech(applicationContext) { p0 ->
                            if (p0 == TextToSpeech.SUCCESS) {
                                textToSpeech.language = Locale.ENGLISH
                                textToSpeech.setSpeechRate(0.5f)
                                textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null)
                            }
                        }
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
}