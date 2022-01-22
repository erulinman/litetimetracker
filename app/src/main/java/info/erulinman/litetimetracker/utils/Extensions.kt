package info.erulinman.litetimetracker.utils

import androidx.fragment.app.Fragment
import info.erulinman.litetimetracker.Toolbar
import java.util.concurrent.TimeUnit

fun Long.toStringOfTwoChar(): String {
    if (this > 99 || this < 0) error("Can accept only long value in range 0..99")
    return if (this < 10) "0$this" else "$this"
}

fun Long.toListHHMMSS(): List<String> {
    var seconds = TimeUnit.MILLISECONDS.toSeconds(this)
    var minutes = TimeUnit.SECONDS.toMinutes(seconds)
    val hours = TimeUnit.MINUTES.toHours(minutes)
    seconds -= minutes * 60
    minutes -= hours * 60

    return listOf(
        hours.toStringOfTwoChar(),
        minutes.toStringOfTwoChar(),
        seconds.toStringOfTwoChar()
    )
}

fun List<String>.toStringHHMMSS(): String = "${this[0]}:${this[1]}:${this[2]}"