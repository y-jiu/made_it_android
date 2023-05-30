package edu.skku.cs.made_it

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import java.text.SimpleDateFormat
import java.util.*

class AllActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all)

        val listView = findViewById<ListView>(R.id.todoAllList)

        val dbHelper = TodoDbHelper(this)

        val todoItems = dbHelper.getAllToDoItems()

        println(todoItems)

        // Create the custom adapter
        val adapter = TodoAllListAdapter(this, todoItems)

        // Set the adapter for the ListView
        listView.adapter = adapter
        listView.setDivider(null)
    }
}