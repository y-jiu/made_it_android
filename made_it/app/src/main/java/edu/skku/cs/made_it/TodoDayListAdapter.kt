package edu.skku.cs.made_it

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class TodoDayListAdapter(private val context: Context, private val items: List<Todo>) : BaseAdapter() {

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
        val view: View = LayoutInflater.from(context).inflate(R.layout.todo_item_day, parent, false)
        val todoItem = view.findViewById<TextView>(R.id.dayTodo)
        val checkBox = view.findViewById<CheckBox>(R.id.checkbox)
        val bg = view.findViewById<ConstraintLayout>(R.id.dayTodoBg)
        val item = items[position]

        todoItem.text = item.title
        checkBox.isChecked = if (item.checked == 1) true else false

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            val dbHelper = TodoDbHelper(context)
            val retrievedTodo = dbHelper.getTodoById(item.id)
            println(retrievedTodo)

            val updatedTodo = retrievedTodo?.copy(checked = if (isChecked) 1 else 0)
            if (updatedTodo != null) {
                dbHelper.updateTodo(updatedTodo)
                val retrievedUpdatedSchedule = dbHelper.getTodoById(item.id)
                println(retrievedUpdatedSchedule)
            }
        }

        bg.setOnClickListener {
            val intent = Intent(context, AddActivity::class.java)
            intent.putExtra("ADD_NEW_TODO", false)
            intent.putExtra("TODO_ID", item.id)
            context.startActivity(intent)
        }

        return view
    }
}
