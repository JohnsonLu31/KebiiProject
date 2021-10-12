package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_list_music.*
import com.example.R

class ListMusicActivity : AppCompatActivity() {

    lateinit var songArrayList : ArrayList<Song>
    lateinit var songAdapter : SongAdapter
    lateinit var musicPlayer : MusicPlayerActivity

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_music)


        songArrayList = ArrayList()

        songAdapter = SongAdapter(this, songArrayList)

        lv_Songs.adapter = songAdapter

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 99)
            return
        } else {
            getSongs()
        }

        lv_Songs.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val song = songArrayList[position]
                val openMusicPlayer = Intent(this@ListMusicActivity, MusicPlayerActivity::class.java)
                openMusicPlayer.putExtra("song", song)
                startActivity(openMusicPlayer)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 99) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongs()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("Recycle")
    private fun getSongs() {
        //read songs from phone
        val contentResolver = contentResolver
        val songUi = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val songCursor = contentResolver.query(songUi, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {

            val indexTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val indexArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val indexData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            do {
                val title = songCursor.getString(indexTitle)
                val artist = songCursor.getString(indexArtist)
                val path = songCursor.getString(indexData)
                songArrayList.add(Song(title, artist, path))
            } while (songCursor.moveToNext())
        }
        songAdapter.notifyDataSetChanged()
    }

}