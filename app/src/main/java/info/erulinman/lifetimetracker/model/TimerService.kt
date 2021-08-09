package info.erulinman.lifetimetracker.model

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.utilities.Constants
import kotlin.properties.Delegates

//class TimerService(private val presets: List<Preset>): Service() {
class TimerService: Service() {
    private var duration by Delegates.notNull<Long>()
    private var timer: Timer? = null
    private val binder = MyBinder()

    private val _time = MutableLiveData<Long>()
    private val _presetName = MutableLiveData<String>()

    private var state = STATE.INACTIVE

    val time get() = _time as LiveData<Long>
    val presetName get() = _presetName as LiveData<String>

    override fun onCreate() {
        Log.d(Constants.DEBUG_TAG, "onCreate service")
        super.onCreate()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        duration = 150000
        _time.value = duration
        _presetName.value = "work"

        Log.d(Constants.DEBUG_TAG, "on start command")
        val result = START_STICKY
        val action = intent?.action

        Log.d(Constants.DEBUG_TAG, "action: $action")
        when(action) {
            ACTION.START -> startTimer()
            ACTION.STOP -> stopTimer()
        }

        return result
    }

    private fun stopTimer() {
        timer?.cancel()
        state = STATE.STOPPED
    }

    private fun startTimer() {
        timer = Timer(duration!!)
        timer!!.start()
        state = STATE.RUNNING
    }

    private inner class Timer(millisInFuture: Long) : CountDownTimer(millisInFuture, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _time.value = millisUntilFinished
            Log.d(Constants.DEBUG_TAG, "on tick work")
        }

        override fun onFinish() {
            TODO("Not yet implemented")
        }

    }

    enum class STATE {
        RUNNING, STOPPED, INACTIVE
    }

    object ACTION {
        const val START = "application.timer_manager.start"
        const val STOP  = "application.timer_manager.stop"
        //const val SKIP  = "application.timer_manager.task.skip"
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(Constants.DEBUG_TAG, "onBind()")
        return binder
    }
    inner class MyBinder: Binder() {
        fun getService(): TimerService = this@TimerService
    }
}
