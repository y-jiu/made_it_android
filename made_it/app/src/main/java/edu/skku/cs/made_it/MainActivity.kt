package edu.skku.cs.made_it

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import com.google.gson.Gson
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.DaySize
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import okhttp3.*
import org.w3c.dom.Element
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import javax.xml.parsers.DocumentBuilderFactory

data class Event(val date: LocalDate, val name: String)

data class Item(
    var dateKind: String? = null,
    var dateName: String? = null,
    var isHoliday: String? = null,
    var locdate: String? = null,
    var seq: String? = null
)


class MainActivity : AppCompatActivity() {

    private lateinit var calendarMonth: TextView
    private lateinit var selectedMonth: YearMonth
    private lateinit var selectedMonthHoliday: MutableList<Item>
    private var selectedMonthHolidayDates = mutableListOf<String>()
    var totaldays = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Get the calendar view
        val calendarView = findViewById<com.kizitonwose.calendar.view.CalendarView>(R.id.calendarView)

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        calendarMonth = findViewById(R.id.month)

        selectedMonth = currentMonth

        calendarMonth.text = selectedMonth.year.toString() + "년 " + selectedMonth.monthValue.toString() + "월"




        fetchHolidays(selectedMonth.year, selectedMonth.monthValue)
        val daysOfWeek = daysOfWeek()

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                val today = LocalDate.now()
                container.textView.text = data.date.dayOfMonth.toString()
                Log.d("pos", data.position.toString())
                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.BLACK)

                    if (data.date.dayOfWeek == DayOfWeek.SUNDAY){
                        container.textView.setTextColor(Color.parseColor("#FF747D"))
                    }
                    if (data.date.dayOfWeek == DayOfWeek.SATURDAY){
                        container.textView.setTextColor(Color.parseColor("#7887FF"))
                    }

                    if (selectedMonthHolidayDates.contains(data.date.toString())){
                        container.textView.setTextColor(Color.parseColor("#FF747D"))
                    }
                } else {
                    container.textView.setTextColor(Color.parseColor("#A5A5A5"))

                    if (data.date.dayOfWeek == DayOfWeek.SUNDAY){
                        container.textView.setTextColor(Color.parseColor("#F5B4B8"))
                    }
                    if (data.date.dayOfWeek == DayOfWeek.SATURDAY){
                        container.textView.setTextColor(Color.parseColor("#BBC0E9"))
                    }
                    if (selectedMonthHolidayDates.contains(data.date.toString())){
                        container.textView.setTextColor(Color.parseColor("#F5B4B8"))
                    }
                }

                if (data.date == today) {
                    container.textView.apply {
                        setBackgroundColor(Color.BLACK)
                        setTextColor(Color.WHITE)
                        val cornerRadius = dpToPx(28)
                        val backgroundDrawable = GradientDrawable()
                        backgroundDrawable.cornerRadius = cornerRadius
                        backgroundDrawable.setColor(Color.BLACK)
                        background = backgroundDrawable
                    }
                    totaldays++
                }

                container.view.setOnClickListener {
                    Log.d("day", data.toString())
                    // add color on click and make the color disappear on click other day
                }
            }
        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                // Remember that the header is reused so this will be called for each month.
                // However, the first day of the week will not change so no need to bind
                // the same view every time it is reused.
                if (container.titlesContainer.tag == null) {
                    container.titlesContainer.tag = data.yearMonth
                    container.titlesContainer.children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            val dayOfWeek = daysOfWeek[index]
                            var title =
                                dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            when (title) {
                                "Mon" -> title = "월"

                                "Tue" -> title = "화"

                                "Wed" -> title = "수"

                                "Thu" -> title = "목"

                                "Fri" -> title = "금"

                                "Sat" -> {
                                    title = "토"
                                    textView.setTextColor(Color.parseColor("#7887FF"))
                                }

                                "Sun" -> {
                                    title = "일"
                                    textView.setTextColor(Color.parseColor("#FF747D"))
                                }
                            }

                            textView.text = title

                    // In the code above, we use the same `daysOfWeek` list
                            // that was created when we set up the calendar.
                            // However, we can also get the `daysOfWeek` list from the month data:
                            // val daysOfWeek = data.weekDays.first().map { it.date.dayOfWeek }
                            // Alternatively, you can get the value for this specific index:
                            // val dayOfWeek = data.weekDays.first()[index].date.dayOfWeek
                        }
                }
            }
        }

       calendarView.monthScrollListener = object : MonthScrollListener {
           override fun invoke(p1: CalendarMonth) {
               calendarView.notifyMonthChanged(p1.yearMonth)
//               p1.yearMonth.
               selectedMonth = p1.yearMonth
               calendarMonth.text = selectedMonth.year.toString() + "년 " + selectedMonth.monthValue.toString() + "월"
               fetchHolidays(selectedMonth.year, selectedMonth.monthValue)
           }
       }
        val prevBtn = findViewById<ImageButton>(R.id.prevButton)
        val nextBtn = findViewById<ImageButton>(R.id.nextButton)

        prevBtn.setOnClickListener {
            calendarView.notifyMonthChanged(selectedMonth.previousMonth)
            calendarView.scrollToMonth(selectedMonth.previousMonth)
            selectedMonth = selectedMonth.previousMonth
            calendarMonth.text = selectedMonth.year.toString() + "년 " + selectedMonth.monthValue.toString() + "월"
            fetchHolidays(selectedMonth.year, selectedMonth.monthValue)
        }

        nextBtn.setOnClickListener {
            calendarView.notifyMonthChanged(selectedMonth.nextMonth)
            calendarView.scrollToMonth(selectedMonth.nextMonth)
            selectedMonth = selectedMonth.nextMonth
//            val displayYearMonth = selectedMonth.toString().split('-')
            calendarMonth.text = selectedMonth.year.toString() + "년 " + selectedMonth.monthValue.toString() + "월"
            fetchHolidays(selectedMonth.year, selectedMonth.monthValue)
        }

        val listView = findViewById<ListView>(R.id.todoDayList)
//
//        // Create a list of items
        val items = listOf("Item 1", "Item 2", "Item 3")

        // Create the custom adapter
        val adapter = TodoDayListAdapter(this, items)

        // Set the adapter for the ListView
        listView.adapter = adapter
        listView.setDivider(null)

        val addBtn = findViewById<ImageButton>(R.id.addButton)
        addBtn.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
    }
    fun dpToPx(dp: Int): Float {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f)
    }

    fun fetchHolidays(year: Int, month: Int) {
        val apiKey = "8Fb7DlOz9hAFzbthtPN8i/XDjTYqz2AjmKWXdUpj/pIziCXrxNBT6TVQDgXFJ87yT7NqtQW7qKCuqJMaEZGXfA=="
//        val country = "KR"
        val monthStr =  if (month.toString().length < 2) "0" + month.toString() else month.toString()
        val client = OkHttpClient()

        val url = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo?serviceKey=$apiKey&" +
                "solYear=$year&solMonth=$monthStr"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()
                val document = builder.parse(responseData?.byteInputStream())

                val responseNode = document.documentElement

                val headerNode = responseNode.getElementsByTagName("header").item(0) as Element
                val resultCode = headerNode.getElementsByTagName("resultCode").item(0).textContent
                val resultMsg = headerNode.getElementsByTagName("resultMsg").item(0).textContent

                val BodyNode = responseNode.getElementsByTagName("body").item(0)
                val itemsNode = responseNode.getElementsByTagName("items").item(0) as Element
                val itemNodes = itemsNode.getElementsByTagName("item")

                val items = mutableListOf<Item>()
                val dates = mutableListOf<String>()

                for (i in 0 until itemNodes.length) {
                    val itemNode = itemNodes.item(i) as Element
                    val dateKind = itemNode.getElementsByTagName("dateKind").item(0).textContent
                    val dateName = itemNode.getElementsByTagName("dateName").item(0).textContent
                    val isHoliday = itemNode.getElementsByTagName("isHoliday").item(0).textContent
                    val locdate = itemNode.getElementsByTagName("locdate").item(0).textContent
                    val formattedLocDate = "${locdate.substring(0, 4)}-${locdate.substring(4, 6)}-${locdate.substring(6, 8)}"

                    val seq = itemNode.getElementsByTagName("seq").item(0).textContent

                    val item = Item(dateKind, dateName, isHoliday, formattedLocDate, seq)

                    if (isHoliday == "Y") {
                        dates.add(formattedLocDate)
                    }
                    items.add(item)
                }

                selectedMonthHoliday = items
                selectedMonthHolidayDates = dates

                // Access the parsed data
                println("resultCode: $resultCode")
                println("resultMsg: $resultMsg")
                println("items: $items")
                println("dates: $dates")

            }
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }


}
