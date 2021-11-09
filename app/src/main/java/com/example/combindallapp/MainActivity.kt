package com.example.combindallapp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.FILL_IN_ACTION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.widget.*
import com.example.R
import com.example.alarmClock.AlarmClockMain
import com.example.chatBox.ui.ChatBoxActivity
import com.example.musicplayer.ListMusicActivity
import com.example.vedioview.ListVideoActivity
import com.example.weather.WeatherMain
import com.example.youtubevideo.YoututbevideoMainActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<*>

    private var RECOGNIZER_RESULT = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        speechToText()

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
            val speechintent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            speechintent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            speechintent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text")

            startActivityForResult(speechintent, RECOGNIZER_RESULT)
        }
    }

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
}

