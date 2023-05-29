package edu.skku.cs.made_it

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class AllActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all)

        val listView = findViewById<ListView>(R.id.todoAllList)
//
//        // Create a list of items
        val items = listOf("Item 1", "Item 2", "Item 3")

        // Create the custom adapter
        val adapter = TodoAllListAdapter(this, items)

        // Set the adapter for the ListView
        listView.adapter = adapter
        listView.setDivider(null)
    }
}