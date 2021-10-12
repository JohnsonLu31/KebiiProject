package com.example.musicplayer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.widget.Toolbar
import java.io.IOException
import kotlin.concurrent.thread
import com.example.R
import kotlinx.android.synthetic.main.music_activity.*


class MusicPlayerActivity : AppCompatActivity() {

    lateinit var musicPlayer: MediaPlayer
    lateinit var song : Song

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_activity)


        song = intent.getSerializableExtra("song") as Song

        tv_Title.text = song.getTitle()
        tv_Artist.text = song.getArtist()

        musicload()

        playmusic()

        volumecontrol()

        progresscontrol()

        thread {
            while (true) {
                if (musicPlayer.isPlaying) {
                    try {
                        runOnUiThread {
                            seekBar_Time.progress = musicPlayer.currentPosition
                            tv_Time.text = millisecondsToString(musicPlayer.currentPosition)
                        }
                        Thread.sleep(1000)
                        Log.d("The progress", Thread.currentThread().name)
                    } catch (e: Exception) {
                        seekBar_Time.progress = 0
                    }
                }
            }
        }

    }

    private fun progresscontrol() {

        val duration = millisecondsToString(musicPlayer.duration)
        tv_Duration.text = duration

        seekBar_Time.max = musicPlayer.duration
        seekBar_Time.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicPlayer.seekTo(progress)
                    seekBar?.progress = progress
                    Log.d("Progress", Thread.currentThread().name)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

        })

    }

    private fun millisecondsToString(time : Int): String {
        var elapsedTime = ""
        val mins = time / 1000 / 60
        val seconds = time / 1000 % 60

        elapsedTime = "$mins:"

        if (seconds < 10) {
            elapsedTime += "0"
        }
        elapsedTime += seconds

        return elapsedTime
    }


    private fun volumecontrol() {
        seekBar_Volume.progress = 50
        seekBar_Volume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                musicPlayer.setVolume(volume, volume)
                Log.d("Volume", Thread.currentThread().name)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }
        })
    }


    private fun playmusic() {
        btn_Play.setOnClickListener {
            if(musicPlayer.isPlaying) {
                musicPlayer.pause()
                btn_Play.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                Log.d("Stop", Thread.currentThread().name)
            } else {
                musicPlayer.start()
                btn_Play.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                Log.d("Start", Thread.currentThread().name)

            }

        }
    }

    private fun musicload() {
        musicPlayer = MediaPlayer()
        try {
            musicPlayer.setDataSource(song.getPath())
            musicPlayer.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        musicPlayer.isLooping = true
        musicPlayer.seekTo(0)
        musicPlayer.setVolume(0.5f, 0.5f)
        Log.d("Main_Activity", Thread.currentThread().name)
    }
}