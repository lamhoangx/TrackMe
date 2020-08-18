package com.lamhx.trackme.utilities

object TimeUtils {
    /**
     * Simply to standardize with pattern hh:mm:ss from [duration] value
     */
    fun standardizeDuration(duration: Long): String {
        if (duration <= 0) {
           return "--:--:--"
        }
        val tempSS = (duration % 60)
        val ss = if (tempSS > 9) {
            "$tempSS"
        } else {
            "0$tempSS"
        }
        val timeMinutes = duration / 60;
        val tempMm = timeMinutes % 60
        val mm = if (tempMm > 9) {
            "$tempMm"
        } else {
            "0$tempMm"
        }
        val tempHH = timeMinutes / 60
        val hh = if (tempHH > 9) {
            "$tempHH"
        } else {
            "0$tempHH"
        }
        return "$hh:$mm:$ss"
    }
}