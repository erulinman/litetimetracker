package info.erulinman.lifetimetracker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import info.erulinman.lifetimetracker.model.TimerService

class PomodoroService : Service() {
    private lateinit var timerManager: TimerService

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val result = START_STICKY

        when(intent?.action) {
            "START" -> startTimer()
            "STOP" -> stopTimer()
        }
        return result
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun startTimer() {
        TODO("Not yet implemented")
    }

    private fun stopTimer() {
        TODO("Not yet implemented")
    }

}