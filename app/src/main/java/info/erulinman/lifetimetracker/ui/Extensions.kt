package info.erulinman.lifetimetracker.ui

import java.util.concurrent.TimeUnit

fun Long.fromLongToTimerString(): String {
    var seconds = TimeUnit.MILLISECONDS.toSeconds(this)
    val minutes = TimeUnit.SECONDS.toMinutes(seconds)
    seconds -= minutes * 60
    val secondsString = if (seconds < 10) "0$seconds" else "$seconds"
    val minutesString = if (minutes< 10) "0$minutes" else "$minutes"
    return "$minutesString:$secondsString"
}

fun String.fromTimerStringToLong(): Long {
    return 0
}