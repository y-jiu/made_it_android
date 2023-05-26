package edu.skku.cs.made_it

import android.view.View
import android.widget.TextView
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.calendarDayText)

    // With ViewBinding
//     textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}