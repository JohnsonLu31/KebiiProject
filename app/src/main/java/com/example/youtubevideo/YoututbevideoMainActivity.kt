package com.example.youtubevideo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.util.ArrayList
import com.example.R
import com.example.combindallapp.MainActivity

class YoututbevideoMainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var add_button: FloatingActionButton
    private lateinit var empty_imageview: ImageView
    private lateinit var no_data: TextView

    private val myDB = MyDatabaseHelper(this)
    private var book_id = ArrayList<String>()
    private var book_title = ArrayList<String>()
    private var book_author = ArrayList<String>()
    private var book_pages = ArrayList<String>()
    private val customAdapter = CustomAdapter(this, this, book_id, book_title, book_author, book_pages)

    //sideable character
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtubevideo_main)

        recyclerView = findViewById(R.id.recyclerView)
        add_button = findViewById(R.id.floatingActionButton)

        empty_imageview = findViewById(R.id.empty_imageview)
        no_data = findViewById(R.id.no_data)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)

        sideableMenuAction()
        
        add_button.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

        storeDataInArrays()

        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun sideableMenuAction() {
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.favorite -> startActivity(Intent(this, FavoriteActivity::class.java))
                R.id.home -> startActivity(Intent(this, MainActivity::class.java))
            }
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            recreate()
        }
    }

    private fun storeDataInArrays() {
        val cursor = myDB.readAllData()
        if (cursor?.count == 0) {
            empty_imageview.visibility = View.VISIBLE
            no_data.visibility = View.VISIBLE
        } else {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    book_id.add(cursor.getString(0))
                    book_title.add(cursor.getString(1))
                    book_author.add(cursor.getString(2))
                    book_pages.add(cursor.getString(3))
                    empty_imageview.visibility = View.GONE
                    no_data.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.youtube_menu, menu)
        inflater.inflate(R.menu.delete_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        if (item.itemId == R.id.delete_all) {
            confirmDialog()
        }
        if (item.itemId == R.id.youtube) {
            startActivity(Intent(this, WebviewAcivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }



    fun confirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete All?")
        builder.setMessage("Are you sure you want to delete all Data?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show()
            val myDB = MyDatabaseHelper(this)
            myDB.deleteAllData()
            //Refresh Activity
            val intent = Intent(this, YoututbevideoMainActivity::class.java)
            startActivity(intent)
            finish()
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->

        })
        builder.create().show()
    }
}