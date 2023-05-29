package edu.skku.cs.made_it

import android.app.ActionBar.LayoutParams
import android.app.Activity
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val todoInput = findViewById<EditText>(R.id.todoInput)
        val dateInput = findViewById<TextView>(R.id.selectDate)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val note = findViewById<EditText>(R.id.noteInput)
        val saveButton = findViewById<ImageButton>(R.id.saveButton)
        val layout = findViewById<ConstraintLayout>(R.id.addLayout)

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var date = dateFormat.format(currentDate)

        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Map the day of the week integer to the corresponding day name
        val dayOfWeekName = when (dayOfWeek) {
            Calendar.SUNDAY -> "일"
            Calendar.MONDAY -> "월"
            Calendar.TUESDAY -> "화"
            Calendar.WEDNESDAY -> "수"
            Calendar.THURSDAY -> "목"
            Calendar.FRIDAY -> "금"
            Calendar.SATURDAY -> "토"
            else -> "Unknown"
        }

        dateInput.text = SimpleDateFormat(
            "yyyy.MM.dd",
            Locale.getDefault()
        ).format(currentDate) + " " + dayOfWeekName

        layout.setOnClickListener {
            datePicker.visibility = View.GONE
        }

        datePicker.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, monthOfYear)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Get the day of the week as an integer (Sunday = 1, Monday = 2, etc.)
            val dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK)

            // Map the day of the week integer to the corresponding day name
            val dayOfWeekName = when (dayOfWeek) {
                Calendar.SUNDAY -> "일"
                Calendar.MONDAY -> "월"
                Calendar.TUESDAY -> "화"
                Calendar.WEDNESDAY -> "수"
                Calendar.THURSDAY -> "목"
                Calendar.FRIDAY -> "금"
                Calendar.SATURDAY -> "토"
                else -> "Unknown"
            }

            // Do something with the selected date and day of the week
            val formattedDate = SimpleDateFormat(
                "yyyy.MM.dd",
                Locale.getDefault()
            ).format(selectedDate.time) + " " + dayOfWeekName
            dateInput.text = formattedDate

            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
        }

        dateInput.setOnClickListener {
            datePicker.visibility = View.VISIBLE
        }
        saveButton.setOnClickListener {
            val dbHelper = TodoDbHelper(this)
            val latestId = dbHelper.getLatestId()
            val newId = latestId + 1

            val newTodo = Todo(
                newId,
                todoInput.text.toString(),
                note.text.toString(),
                date,
                0
            )
            dbHelper.createTodo(newTodo)
            val retrievedTodo = dbHelper.getTodoById(newId)
            println("SAVE:" + retrievedTodo)
            finish()
        }
    }
}