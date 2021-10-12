package com.example.vedioview

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.widget.MediaController
import androidx.appcompat.widget.Toolbar
import com.example.R
import kotlinx.android.synthetic.main.video_activity.*

class VideoPlayerActivity : AppCompatActivity() {

    lateinit var videoPlayer : MediaPlayer
    lateinit var video : Video

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_activity)

        video = intent.getSerializableExtra("video") as Video


        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)


        videoView.setMediaController(mediaController)
        videoView.setVideoPath(video.getPath())
        videoView.requestFocus()
        videoView.start()
    }

    /*private fun videoLoad() {
        videoPlayer = MediaPlayer()
        try {
            videoPlayer.setDataSource(video.getPath())
            videoPlayer.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }*/
}