package edu.skku.cs.made_it

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.calendarDayText)
    val dayCell = view.findViewById<ConstraintLayout>(R.id.dayCell)
    // With ViewBinding
//     textView = CalendarDayLayoutBinding.bind(view).calendarDayText

}