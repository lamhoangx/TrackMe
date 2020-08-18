package com.lamhx.trackme.utilities

import android.content.res.Resources
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lamhx.trackme.R

/**
 * Converters to support data binding
 */

@BindingAdapter("workoutDuration")
fun duration(view: TextView?, duration: Long) {
    view?.let {
        it.text = TimeUtils.standardizeDuration(duration)
    }
}

@BindingAdapter("workoutDistance")
fun distance(view: TextView?, distance: Float) {
    view?.let {
        it.text = String.format(
            it.context.getString(R.string.info_distance),
            LocationUtils.convertDistanceToKm(distance)
        )
    }
}

@BindingAdapter("workoutDistance", "workoutDuration")
fun avgSpeed(textView: TextView?, distance: Float, duration: Long) {
    textView?.let {
        it.text = String.format(
            it.context.getString(R.string.info_avg_speed),
            LocationUtils.calSpeed(distance, duration)
        )
    }
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

