package com.example.youtubevideo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import com.example.R

class FavoriteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    lateinit var id: String
    private lateinit var title: String
    lateinit var author: String
    lateinit var youtubeID: String

    private val myDB = FavoriteMyDatabaseHelper(this)
    private var book_id = ArrayList<String>()
    private var book_title = ArrayList<String>()
    private var book_author = ArrayList<String>()
    private var book_pages = ArrayList<String>()
    private val customAdapter = FavoriteCustomAdapter(this, this, book_id, book_title, book_author, book_pages)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtubevideo_favorite)

        recyclerView = findViewById(R.id.favorite_recycleView)

        storeDataInArrays()

        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getIntentData() {
        id = intent.getStringExtra("id").toString()
        title = intent.getStringExtra("title").toString()
        author = intent.getStringExtra("author").toString()
        youtubeID = intent.getStringExtra("pages").toString()
    }

    private fun storeDataInArrays() {

        val cursor = myDB.readAllData()
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    book_id.add(cursor.getString(0))
                    book_title.add(cursor.getString(1))
                    book_author.add(cursor.getString(2))
                    book_pages.add(cursor.getString(3))
            }
        }
    }
}