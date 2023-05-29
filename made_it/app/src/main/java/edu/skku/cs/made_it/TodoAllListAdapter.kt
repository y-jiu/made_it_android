package edu.skku.cs.made_it

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class TodoAllListAdapter (private val context: Context, private val items: List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.todo_item_all, parent, false)
        val todoItem = view.findViewById<TextView>(R.id.allTodo)
        val todoDday = view.findViewById<TextView>(R.id.dday)

        val item = getItem(position) as String
        todoItem.text = item

        return view
    }
}