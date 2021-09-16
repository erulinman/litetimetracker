package info.erulinman.lifetimetracker.utilities

import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

fun Long.fromLongToTimerString(): String {
    var seconds = TimeUnit.MILLISECONDS.toSeconds(this)
    val minutes = TimeUnit.SECONDS.toMinutes(seconds)
    seconds -= minutes * 60

    val secondsString = if (seconds < 10) "0$seconds" else "$seconds"
    val minutesString = if (minutes< 10) "0$minutes" else "$minutes"
    return "$minutesString:$secondsString"
}

fun String.fromTimerStringToLong(): Long {
    val pattern = Pattern.compile("^(\\d\\d):(\\d\\d)$")
    val matcher = pattern.matcher(this)
    if (matcher.find()) {
        val minutes = matcher.group(1)?.toLong() ?: throw IllegalStateException("Empty regexp group")
        val seconds = matcher.group(2)?.toLong() ?: throw IllegalStateException("Empty regexp group")
        return (minutes * 60000) + (seconds * 1000)
    } else throw IllegalStateException("No matches found")
}

fun Long.toListHHMMSS(): List<String> {
    var seconds = TimeUnit.MILLISECONDS.toSeconds(this)
    var minutes = TimeUnit.SECONDS.toMinutes(seconds)
    val hours = TimeUnit.MINUTES.toHours(minutes)
    seconds -= minutes * 60
    minutes -= hours * 60

    val secondsToString = if (seconds < 10) "0$seconds" else "$seconds"
    val minutesToString = if (minutes < 10) "0$minutes" else "$minutes"
    val hoursToString = if (hours < 10) "0$hours" else "$minutes"
    return listOf(hoursToString, minutesToString, secondsToString)
}

fun List<String>.toStringHHMMSS(): String = "${this[0]}:${this[1]}:${this[2]}"