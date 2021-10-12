package com.example.combindallapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.*
import com.example.R
import com.example.alarmManager.AlarmMain
import com.example.musicplayer.ListMusicActivity
import com.example.vedioview.ListVideoActivity
import com.example.weather.WeatherMain
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter : ArrayAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.App))
        lv_listView.adapter = adapter
        lv_listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            when(position){
                0 -> startActivity(Intent(this, WeatherMain::class.java))
                1 -> startActivity(Intent(this, AlarmMain::class.java))
                2 -> startActivity(Intent(this, ListMusicActivity::class.java))
                3 -> startActivity(Intent(this, ListVideoActivity::class.java))
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        val search = menu?.findItem(R.id.nav_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search Something!"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
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