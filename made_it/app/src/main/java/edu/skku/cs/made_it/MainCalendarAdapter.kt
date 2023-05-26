package edu.skku.cs.made_it
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class MainCalendarAdapter(
    private val context: Context,
    private val year: Int,
    private val month: Int,
    private val events: List<Event>
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d")
    private val currentMonth: YearMonth = YearMonth.of(year, month)

    private val days: List<LocalDate> = generateDaysList()

    private fun generateDaysList(): List<LocalDate> {
        val daysList = mutableListOf<LocalDate>()

        // Add the days from the previous month
        val firstDayOfMonth = LocalDate.of(year, month, 1)
        val prevMonth = currentMonth.minusMonths(1)
        val prevMonthDays = prevMonth.lengthOfMonth()

        for (i in prevMonthDays - firstDayOfMonth.dayOfWeek.value + 1..prevMonthDays) {
            daysList.add(LocalDate.of(prevMonth.year, prevMonth.monthValue, i))
        }

        // Add the days of the current month
        for (dayOfMonth in 1..currentMonth.lengthOfMonth()) {
            daysList.add(LocalDate.of(year, month, dayOfMonth))
        }

        // Add the days from the next month to fill the grid
        val lastDayOfMonth = LocalDate.of(year, month, currentMonth.lengthOfMonth())
        val nextMonth = currentMonth.plusMonths(1)
        Log.d("tag", lastDayOfMonth.dayOfWeek.value.toString())
        val remainingDays = 7 - (lastDayOfMonth.dayOfWeek.value+1)
        for (dayOfMonth in 1..remainingDays) {
            daysList.add(LocalDate.of(nextMonth.year, nextMonth.monthValue, dayOfMonth))
        }
        return daysList
    }

    override fun getCount(): Int {
        return days.size
    }

    override fun getItem(position: Int): LocalDate? {
        return days[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.main_calendar_cell, parent, false)
        val dayTextView = view.findViewById<TextView>(R.id.dayTextView)
//        val eventTextView = view.findViewById<TextView>(R.id.eventTextView)

        val date = getItem(position)

        if (date != null) {
            // Display the day of the month
            dayTextView.text = dateFormatter.format(date)
            dayTextView.visibility = View.VISIBLE

            // Set the background color based on the current month
            if (date.year == year && date.monthValue == month) {
                dayTextView.setTextColor(Color.BLACK)
            } else {
                dayTextView.setTextColor(Color.LTGRAY)
            }

            // Find events for the current date
            val eventsForDate = events.filter { it.date == date }

            if (eventsForDate.isNotEmpty()) {
//                eventTextView.text = eventsForDate.joinToString(separator = "\n") { it.name }
//                eventTextView.visibility = View.VISIBLE
                Log.d("tag", eventsForDate.toString())
                val parentLayout: ConstraintLayout = view.findViewById(R.id.main_cell)

                // Create a new TextView instance
                val textView = TextView(context)
                textView.text = eventsForDate.joinToString(separator = "\n") { it.name }
                textView.setTextColor(Color.BLACK)
                textView.textSize = 16f

                // Create LayoutParams for the TextView
                val layoutParams = ConstraintLayout.LayoutParams(0,0)

                layoutParams.topToBottom = dayTextView.bottom
                layoutParams.startToStart = parentLayout.left
//                layoutParams.bottomToBottom =
                layoutParams.endToEnd = parentLayout.right
                // Set any additional properties for the LayoutParams (e.g., margins)
                layoutParams.setMargins(1, 1, 1, 1)
                textView.layoutParams = layoutParams
                // Add the TextView to the parent layout
                parentLayout.addView(textView)

                Log.d("tag", textView.toString() + parentLayout.toString())
            }

        } else {
            // Hide the cell for empty days
            dayTextView.text = ""
            dayTextView.visibility = View.INVISIBLE
//            eventTextView.text = ""
//            eventTextView.visibility = View.GONE
        }

        return view
    }
}
