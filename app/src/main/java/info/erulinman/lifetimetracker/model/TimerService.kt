package info.erulinman.lifetimetracker.model

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.ui.fromLongToTimerString
import info.erulinman.lifetimetracker.utilities.Constants

class TimerService: Service() {
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var binder: TimerServiceBinder
    private var timer: Timer? = null
    private var currentPresetDuration: Long = 0
    private var currentPresetRemaining: Long? = null
    private var currentPresetIndex: Int = 0

    private val presets = mutableListOf<Preset>()

    private val _time = MutableLiveData<String>()
    val time get() = _time as LiveData<String>

    private val _presetName = MutableLiveData<String>()
    val presetName get() = _presetName as LiveData<String>

    private val _state = MutableLiveData<String>()
    val state get() = _state as LiveData<String>

    override fun onCreate() {
        Log.d(Constants.DEBUG_TAG, "TimerService.onCreate()")
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        binder = TimerServiceBinder()
        _state.value = INITIALIZED

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val result = START_STICKY

        when (intent?.action) {
            START -> startTimer()
            STOP -> stopTimer()
            RESTART -> restartPresets()
            SKIP -> skipPreset()
            CLOSE -> closeService()
        }

        return result
    }

    fun loadPresets(_presets: List<Preset>) {
        Log.d(Constants.DEBUG_TAG, "TimerService.loadPresets()")
        if (state.value == INITIALIZED && _presets.isNotEmpty()) {
            presets.apply {
                addAll(_presets)
                first().let { firstPreset ->
                    _time.value = firstPreset.time.fromLongToTimerString()
                    _presetName.value = firstPreset.name
                    currentPresetDuration = firstPreset.time
                }
            }
        }
    }

    fun runPresets() {
        Log.d(Constants.DEBUG_TAG, "TimerService.runPresets()")
        startTimer()
    }

    fun stopPresets() {
        Log.d(Constants.DEBUG_TAG, "TimerService.stopPresets()")
        stopTimer()
    }

    fun skipPreset() {
        Log.d(Constants.DEBUG_TAG, "TimerService.skipPreset()")
        stopTimer()
        currentPresetRemaining = null
        runNextPreset()
    }

    private fun startTimer() {
        Log.d(Constants.DEBUG_TAG, "TimerService.startTimer()")

        if (timer == null) {
            timer = Timer(
                currentPresetRemaining ?: (currentPresetDuration + TIME_COMPENSATION)
            ).apply { start() }
            _state.value = STARTED
        } else {
            Log.d(Constants.DEBUG_TAG, "TimerService.startTimer(): there is old timer")
            //TODO: throw exception???
        }

    }

    private fun stopTimer() {
        Log.d(Constants.DEBUG_TAG, "TimerService.stopTimer()")
        if (state.value == STARTED) {
            timer?.cancel()
            timer = null
            _state.value = STOPPED
            time.value?.let { notificationHelper.notifyWhenStopped(it) }
        }
    }

    private fun runNextPreset() {
        Log.d(Constants.DEBUG_TAG, "TimerService.runNextPreset()")
        currentPresetIndex++
        val nextRun = presets.getOrNull(currentPresetIndex)?.let { nextPreset ->
            Log.d(Constants.DEBUG_TAG, "TimerService.runNextPreset(): there is new preset: $nextPreset")
            currentPresetDuration = nextPreset.time
            _presetName.value = nextPreset.name
            startTimer()
            true
        } ?: false
        if (!nextRun) {
            _state.value = FINISHED
            getString(R.string.time_value_on_finish).let {
                _time.value = it
                notificationHelper.notifyWhenFinished(it)
            }
            Log.d(Constants.DEBUG_TAG, "TimerService.runNextPreset(): there is no new preset")
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(Constants.DEBUG_TAG, "TimerService.onBind()")
        return binder
    }

    fun restartPresets() {
        presets.first().let { firstPreset ->
            currentPresetIndex = 0
            _time.value = firstPreset.time.fromLongToTimerString()
            _presetName.value = firstPreset.name
            currentPresetDuration = firstPreset.time
            startTimer()
        }
    }

    private fun closeService() {
        this.stopSelf()
    }
    override fun onDestroy() {
        Log.d(Constants.DEBUG_TAG, "TimerService.Timer.onDestroy()")
        stopTimer()
        super.onDestroy()
    }

    private inner class Timer(millisInFuture: Long) : CountDownTimer(millisInFuture, ONE_SECOND_INTERVAL) {
        override fun onTick(millisUntilFinished: Long) {
            if (millisUntilFinished < ONE_SECOND_INTERVAL) {
                onFinish()
            } else {
                val timeInString = millisUntilFinished.fromLongToTimerString()
                Log.d(
                    Constants.DEBUG_TAG,
                    "TimerService.Timer.onTick(): on string - $timeInString, on long - $millisUntilFinished}"
                )

                _time.value = timeInString
                currentPresetRemaining = millisUntilFinished
                notificationHelper.notifyWhenStarted(timeInString)
            }
        }

        override fun onFinish() {
            Log.d(Constants.DEBUG_TAG, "TimerService.Timer.onFinish()")
            cancel()
            timer = null
            currentPresetRemaining = null
            runNextPreset()
        }
    }

    inner class TimerServiceBinder: Binder() {
        fun getService(): TimerService = this@TimerService
    }

    companion object {
        const val INITIALIZED = "info.erulinman.lifetimetracker.TIMER.INITIALIZED"
        const val STARTED     = "info.erulinman.lifetimetracker.TIMER.STARTED"
        const val STOPPED     = "info.erulinman.lifetimetracker.TIMER.STOPPED"
        const val FINISHED    = "info.erulinman.lifetimetracker.TIMER.FINISHED"

        const val START = "info.erulinman.lifetimetracker.TIMER.START"
        const val STOP = "info.erulinman.lifetimetracker.TIMER.STOP"
        const val RESTART = "info.erulinman.lifetimetracker.TIMER.RESTART"
        const val SKIP = "info.erulinman.lifetimetracker.TIMER.SKIP"
        const val CLOSE = "info.erulinman.lifetimetracker.TIMER.CLOSE"

        private const val ONE_SECOND_INTERVAL: Long = 1000
        private const val TIME_COMPENSATION: Long = 999
    }
}
