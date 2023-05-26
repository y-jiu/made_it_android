package edu.skku.cs.made_it

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

data class Event(val date: LocalDate, val name: String)

class MainActivity : AppCompatActivity() {

    private lateinit var calendarTitle: TextView
    private lateinit var calendarGrid: GridView
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private var year: Int = 2023
    private var month: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarTitle = findViewById(R.id.calendarTitle)
        calendarGrid = findViewById(R.id.calendarGrid)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)

        // Create a list of events
        val events = listOf(
            Event(LocalDate.of(2023, 5, 5), "Event 1"),
            Event(LocalDate.of(2023, 5, 10), "Event 2"),
            Event(LocalDate.of(2023, 5, 15), "Event 3")
            // Add more events here...
        )

        // Set the initial month and year for the calendar to the current month
        val currentMonth = YearMonth.now()
        year = currentMonth.year
        month = currentMonth.monthValue

        // Create a date formatter to display dates
        val dateFormatter = DateTimeFormatter.ofPattern("dd")

        // Get the first day of the month
        val firstDayOfMonth = LocalDate.of(year, month, 1)

        // Get the number of days in the month
        val numDaysInMonth = firstDayOfMonth.lengthOfMonth()

        // Create a list to store the calendar days
        val calendarDays = mutableListOf<String>()

        // Add empty cells for the days before the first day of the month
        for (i in 1 until firstDayOfMonth.dayOfWeek.value) {
            calendarDays.add("")
        }

        // Add the days of the month to the calendar
        for (dayOfMonth in 1..numDaysInMonth) {
            calendarDays.add(dayOfMonth.toString())
        }
        updateCalendarTitle()

        // Create a list to store the calendar header (weekdays)
        val calendarHeader = mutableListOf<String>()

        // Get the localized weekday names
        val locale = Locale.getDefault()
        val dayOfWeekFormatter = TextStyle.SHORT
        val weekdays = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        // Add the weekdays to the calendar header
        calendarHeader.addAll(weekdays)

        // Create an array adapter for the calendar header
        val headerAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, calendarHeader)

        // Set the adapter for the calendar header
        val headerGridView = findViewById<GridView>(R.id.headerGridView)
        headerGridView.adapter = headerAdapter

        // Create a custom adapter for the calendar grid
        val adapter = MainCalendarAdapter(this, year, month, events)

        // Set the adapter for the calendar grid
        calendarGrid.adapter = adapter


        // Populate the events for each day
        for (event in events) {
            if (event.date.year == year && event.date.monthValue == month) {
                val dayOfMonth = event.date.dayOfMonth
                val position = dayOfMonth - 1 + firstDayOfMonth.dayOfWeek.value
                val view = calendarGrid.getChildAt(position)
                view?.let {
                    it.setBackgroundResource(R.color.black)
                    it.tag = event.name
                }
            }
        }

        // Set button click listeners
        prevButton.setOnClickListener {
            changeMonth(-1)
            // Create a custom adapter for the calendar grid
            val adapter = MainCalendarAdapter(this, year, month, events)

            // Set the adapter for the calendar grid
            calendarGrid.adapter = adapter

        }
        nextButton.setOnClickListener {
            changeMonth(1)
            // Create a custom adapter for the calendar grid
            val adapter = MainCalendarAdapter(this, year, month, events)

            // Set the adapter for the calendar grid
            calendarGrid.adapter = adapter
        }

        calendarGrid.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            var date = LocalDate.of(year, month, 1)
            var dayOfMonth = position - (date.dayOfWeek.value - 1)

            Log.d("tag", position.toString() + " " + date.dayOfWeek.value.toString() + " " + date.lengthOfMonth() + " " + dayOfMonth)

//            val dayOfMonth = position - (firstDayOfMonth.dayOfWeek.value - 1)
//            val date = LocalDate.of(year, month, dayOfMonth)

//            val selectedEvents = events.filter { it.date == date }.map { it.name }
//            // Do something with selected events
//            Log.d("tag", dayOfMonth.toString()+ date.toString()+ selectedEvents.toString())

            // Check if the clicked position is within the range of valid days for the current month
            if (position >= 0 &&
                position < date.lengthOfMonth()
            ) {
                date = LocalDate.of(year, month, dayOfMonth)
                val selectedEvents = events.filter { it.date == date }.map { it.name }
                Log.d("tag", dayOfMonth.toString()+ date.toString()+ selectedEvents.toString())
            }
        }

//        // Set the calendar title
//        calendarTitle.text = "${firstDayOfMonth.month} $year"
    }
    private fun updateCalendarTitle() {
        calendarTitle.text = "${LocalDate.of(year, month, 1).month} $year"
    }

    private fun changeMonth(delta: Int) {
        month += delta
        if (month > 12) {
            year++
            month = 1
        } else if (month < 1) {
            year--
            month = 12
        }
        updateCalendarTitle()
        // Refresh the calendar with the updated month
        // Add your logic here to update the calendar grid and events
    }
}
