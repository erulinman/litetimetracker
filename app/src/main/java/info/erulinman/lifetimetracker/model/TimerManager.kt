package info.erulinman.lifetimetracker.model

import android.os.CountDownTimer
import info.erulinman.lifetimetracker.data.entity.Preset

class TimerManager(private val presets: List<Preset>) {

    private var currentSessionId: Preset? = null
    private var currentSessionDuration: Long? = null

    fun startSession() {

    }

    fun stopSession() {
        
    }

    private class Timer(private val millisInFuture: Long) : CountDownTimer(millisInFuture, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            TODO("Not yet implemented")
        }

        override fun onFinish() {
            TODO("Not yet implemented")
        }

    }
}