package edu.skku.cs.made_it

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.text.SimpleDateFormat
import java.util.*

class TodoAllListAdapter (private val context: Context, private val items: List<Todo>) : BaseAdapter() {

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
        val todoBg = view.findViewById<ConstraintLayout>(R.id.allTodoBg)

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var date = dateFormat.format(currentDate)

        val item = items[position]
        val startDateObj = dateFormat.parse(date)
        val endDateObj = dateFormat.parse(item.date)

        val diffInMillis = endDateObj.time - startDateObj.time
        val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
        todoItem.text = item.title

        if (diffInDays <= 3) {
            todoBg.setBackgroundResource(R.drawable.gradient_bg_due)
        }
        if (diffInDays < 0) {
            todoDday.text = "D+" + diffInDays.toString().split('-')[1]
        } else if (diffInDays > 0) {
            todoDday.text = "D-" + diffInDays.toString()
        } else if (diffInDays.toInt() == 0) {
            todoDday.text = "D-DAY"
        }

        return view
    }
}