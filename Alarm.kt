package com.querycubix.alarmclock

import java.io.Serializable
import java.util.Locale

data class Alarm(
    val id: Int,
    var hour: Int,
    var minute: Int,
    var isEnabled: Boolean = true,
    var days: String = "Daily",
    var toneUri: String? = null
) : Serializable {
    val timeFormatted: String
        get() {
            val h = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
            val amPm = if (hour >= 12) "PM" else "AM"
            return String.format(Locale.getDefault(), "%02d:%02d %s", h, minute, amPm)
        }
}
