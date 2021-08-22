package info.erulinman.lifetimetracker.model

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.ui.fromLongToTimerString
import info.erulinman.lifetimetracker.utilities.Constants

class TimerService: Service() {
    private lateinit var binder: TimerServiceBinder
    private var timer: Timer? = null
    private var currentPresetDuration: Long = 0
    private var currentPresetRemaining: Long? = null
    private var currentPresetIndex: Int = 0

    private val presets = mutableListOf<Preset>()

    private val _time = MutableLiveData<Long>()
    val time get() = _time as LiveData<Long>

    private val _presetName = MutableLiveData<String>()
    val presetName get() = _presetName as LiveData<String>

    private val _state = MutableLiveData<String>()
    val state get() = _state as LiveData<String>

    companion object {
        const val INITIALIZED = "info.erulinman.lifetimetracker.TIMER.INITIALIZED"
        const val STARTED     = "info.erulinman.lifetimetracker.TIMER.STARTED"
        const val STOPPED     = "info.erulinman.lifetimetracker.TIMER.STOPPED"
        const val FINISHED    = "info.erulinman.lifetimetracker.TIMER.FINISHED"
    }

    override fun onCreate() {
        Log.d(Constants.DEBUG_TAG, "TimerService.onCreate()")
        super.onCreate()
        _state.value = INITIALIZED
        binder = TimerServiceBinder()
    }

    fun loadPresets(_presets: List<Preset>) {
        Log.d(Constants.DEBUG_TAG, "TimerService.loadPresets()")
        if (state.value == INITIALIZED && _presets.isNotEmpty()) {
            presets.apply {
                addAll(_presets)
                first().let { firstPreset ->
                    _time.value = firstPreset.time
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
        Log.d(Constants.DEBUG_TAG, "Current preset index: $currentPresetIndex")
        Log.d(Constants.DEBUG_TAG, "Current preset name: ${presetName.value}")
        Log.d(Constants.DEBUG_TAG, "Current preset duration: $currentPresetDuration")
        Log.d(Constants.DEBUG_TAG, "Current preset remaining: $currentPresetRemaining")
        if (timer == null) {
            timer = Timer(currentPresetRemaining ?: (currentPresetDuration + 1000))
            timer?.start()
            _state.value = STARTED
        } else {
            Log.d(Constants.DEBUG_TAG, "TimerService.startTimer(): there is old timer")
        }

    }

    private fun stopTimer() {
        Log.d(Constants.DEBUG_TAG, "TimerService.stopTimer()")
        if (state.value == STARTED) {
            timer?.cancel()
            timer = null
            _state.value = STOPPED
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
            _time.value = firstPreset.time
            _presetName.value = firstPreset.name
            currentPresetDuration = firstPreset.time
            startTimer()
        }
    }

    override fun onDestroy() {
        Log.d(Constants.DEBUG_TAG, "TimerService.Timer.onDestroy()")
        stopTimer()
        super.onDestroy()
    }

    private inner class Timer(millisInFuture: Long) : CountDownTimer(millisInFuture, Constants.ONE_SECOND_INTERVAL) {
        override fun onTick(millisUntilFinished: Long) {
            if (millisUntilFinished < 1000) {
                onFinish()
            } else {
                Log.d(
                    Constants.DEBUG_TAG,
                    "TimerService.Timer.onTick(): ${millisUntilFinished.fromLongToTimerString()}"
                )
                _time.value = millisUntilFinished
                currentPresetRemaining = millisUntilFinished
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
}
