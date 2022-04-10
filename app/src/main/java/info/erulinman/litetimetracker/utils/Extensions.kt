package info.erulinman.litetimetracker.utils

import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.fragment.app.Fragment
import info.erulinman.litetimetracker.R
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

fun Fragment.setLightStatusBar() = with(requireActivity().window) {
    statusBarColor = resources.getColor(R.color.white, context?.theme)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        decorView.windowInsetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Fragment.setDarkStatusBar() = with(requireActivity().window) {
    statusBarColor = resources.getColor(R.color.black_night, context?.theme)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        decorView.windowInsetsController?.setSystemBarsAppearance(
            0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}