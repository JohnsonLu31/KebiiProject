package com.example.youtubevideo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.example.R

class YoutubeActivity : AppCompatActivity() {

    lateinit var youtubePlayer: YouTubePlayerView



    lateinit var id: String
    private lateinit var title: String
    lateinit var author: String
    lateinit var youtubeID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtubevideo_youtube)

        youtubePlayer = findViewById(R.id.youtube_player)

        //Get Data form Intent
        getIntentData()
        //Set Title
        supportActionBar?.title = title

        lifecycle.addObserver(youtubePlayer)

        if (intent.hasExtra("pages")) {
            youtubeID = intent.getStringExtra("pages").toString()

        } else {
            Toast.makeText(this@YoutubeActivity, "Error Youtube ID", Toast.LENGTH_SHORT).show()
        }

        youtubePlayer.addYouTubePlayerListener(object: AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {

                youTubePlayer.loadVideo(youtubeID, 0f)
            }
        })
    }

    private fun getIntentData() {
        id = intent.getStringExtra("id").toString()
        title = intent.getStringExtra("title").toString()
        author = intent.getStringExtra("author").toString()
        youtubeID = intent.getStringExtra("pages").toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        youtubePlayer.release()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.data_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.video_data) {
            val intent = Intent(this, UpdateActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("title", title)
            intent.putExtra("author", author)
            intent.putExtra("pages", youtubeID)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}