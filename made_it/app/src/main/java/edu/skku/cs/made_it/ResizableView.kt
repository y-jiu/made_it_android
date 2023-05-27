package edu.skku.cs.made_it

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

class ResizableView (context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private var lastY = 0f

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        // Layout the child views within the container
        for (i in 0 until childCount) {
            val child = getChildAt(i)
//            Log.d("c", child.measuredHeight.toString()+ child.measuredWidth.toString())
            child.layout(0, 0, child.measuredWidth, child.measuredHeight)

        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val y = event.rawY

        val layoutParams = layoutParams as ViewGroup.LayoutParams

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = y - lastY

                // Resize the container height
                layoutParams.height -= deltaY.toInt()

                // Set the new layout parameters
                this.layoutParams = layoutParams

                // Request a layout update to reflect the changes
                requestLayout()

                lastY = y
            }
        }

        return true
    }
}