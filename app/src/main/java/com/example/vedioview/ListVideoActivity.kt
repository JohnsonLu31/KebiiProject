package com.example.vedioview

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.example.R
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_list_video.*

class ListVideoActivity : AppCompatActivity() {

    lateinit var videoArrayList: ArrayList<Video>
    lateinit var videoAdapter: VideoAdapter

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_video)


        videoArrayList = ArrayList()

        videoAdapter = VideoAdapter(this, videoArrayList)

        lv_Videos.adapter = videoAdapter

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 99)
            return
        } else {
            getVideos()
        }

        lv_Videos.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val video = videoArrayList.get(position)
                val openVideoPlayer =
                    Intent(this@ListVideoActivity, VideoPlayerActivity::class.java)
                openVideoPlayer.putExtra("video", video)
                startActivity(openVideoPlayer)
            }
    }

        @RequiresApi(Build.VERSION_CODES.R)
        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            if (requestCode == 99) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getVideos()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.R)
        @SuppressLint("Recycle")
        private fun getVideos() {
            //read videos from system
            val contentResolver = contentResolver
            val videoUi = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

            val videoCursor = contentResolver.query(videoUi, null, null, null, null)
            if (videoCursor != null && videoCursor.moveToFirst()) {
                val indexTitle = videoCursor.getColumnIndex(MediaStore.Video.Media.TITLE)
                val indexArtist = videoCursor.getColumnIndex(MediaStore.Video.Media.ARTIST)
                val indexData = videoCursor.getColumnIndex(MediaStore.Video.Media.DATA)

                do {
                    val title = videoCursor.getString(indexTitle)
                    val artist = videoCursor.getString(indexArtist)
                    val path = videoCursor.getString(indexData)
                    videoArrayList.add(Video(title, artist, path))
                } while (videoCursor.moveToNext())
            }
            videoAdapter.notifyDataSetChanged()
        }
}