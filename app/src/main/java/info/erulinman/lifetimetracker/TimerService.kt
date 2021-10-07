package info.erulinman.lifetimetracker

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.utilities.fromLongToTimerString
import info.erulinman.lifetimetracker.utilities.Constants

class TimerService: Service() {
    private val presets = mutableListOf<Preset>()

    private val _time = MutableLiveData<String>()
    private val _presetName = MutableLiveData<String>()
    private val _state = MutableLiveData<String>()
    private val _canSkip = MutableLiveData(false)

    private var timer: Timer? = null
    private var currentPresetDuration: Long = ZERO_PRESET_DURATION
    private var currentPresetRemaining: Long? = null
    private var currentPresetIndex: Int = ZERO_PRESET_INDEX

    private lateinit var notificationHelper: NotificationHelper
    private lateinit var binder: LocalBinder

    val time: LiveData<String> get() = _time
    val presetName: LiveData<String> get() = _presetName
    val state: LiveData<String> get() = _state
    val canSkip: LiveData<Boolean> get() = _canSkip

    override fun onCreate() {
        Log.d(Constants.DEBUG_TAG, "TimerService.onCreate()")
        super.onCreate()
        _state.value = INITIALIZED
        binder = LocalBinder()
        notificationHelper = NotificationHelper(this)
        startForeground(
            NotificationHelper.NOTIFICATION_ID,
            notificationHelper.getStartedNotificationBuilder().build()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(Constants.DEBUG_TAG, "TimerService.onStartCommand()")
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            START -> startTimer()
            STOP -> stopTimer()
            RESTART -> restartPresets()
            SKIP -> skipPreset()
            CLOSE -> closeService()
        }

        return START_NOT_STICKY
    }

    fun loadPresets(_presets: List<Preset>) {
        Log.d(Constants.DEBUG_TAG, "TimerService.loadPresets()")
        if (state.value == INITIALIZED && _presets.isNotEmpty()) {
            Log.d(Constants.DEBUG_TAG, "TimerService.loadPresets()")
            presets.apply {
                addAll(_presets)
                first().let { firstPreset ->
                    _time.value = firstPreset.time.fromLongToTimerString()
                    _presetName.value = firstPreset.name
                    currentPresetDuration = firstPreset.time
                    _canSkip.value = true
                }
            }
        }
    }

    fun startTimer() {
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

    fun stopTimer() {
        if (state.value == STARTED) {
            Log.d(Constants.DEBUG_TAG, "TimerService.stopTimer()")
            timer?.cancel()
            timer = null
            _state.value = STOPPED
            time.value?.let { notificationHelper.notifyWhenStopped(it) }
        }
    }

    fun skipPreset() {
        Log.d(Constants.DEBUG_TAG, "TimerService.skipPreset()")
        stopTimer()
        currentPresetRemaining = null
        runNextPreset()
    }

    private fun runNextPreset() {
        Log.d(Constants.DEBUG_TAG, "TimerService.runNextPreset()")
        currentPresetIndex++
        val nextRun = presets.getOrNull(currentPresetIndex)?.let { nextPreset ->
            Log.d(
                Constants.DEBUG_TAG,
                "TimerService.runNextPreset(): there is new preset: $nextPreset"
            )
            currentPresetDuration = nextPreset.time
            _presetName.value = nextPreset.name
            startTimer()
            true
        } ?: false
        if (!nextRun) {
            _state.value = FINISHED
            _canSkip.value = false
            _time.value = getString(R.string.text_view_timer_finished)
            notificationHelper.notifyWhenFinished()
            Log.d(Constants.DEBUG_TAG, "TimerService.runNextPreset(): there is no new preset")
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(Constants.DEBUG_TAG, "TimerService.onBind()")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(Constants.DEBUG_TAG, "TimerService.onUnbind()")
        return super.onUnbind(intent)
    }

    fun restartPresets() {
        presets.first().let { firstPreset ->
            currentPresetIndex = 0
            _canSkip.value = true
            _time.value = firstPreset.time.fromLongToTimerString()
            _presetName.value = firstPreset.name
            currentPresetDuration = firstPreset.time
            startTimer()
        }
    }

    fun closeService() {
        Log.d(Constants.DEBUG_TAG, "TimerService.closeService()")
        stopTimer()
        _state.value = CLOSED
        stopForeground(true)
        stopSelf()
        Intent(CLOSE).also { intent ->
            Log.d(Constants.DEBUG_TAG, "TimerService.closeService.sendBroadcast()")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
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
                notificationHelper.updateStartedNotification(timeInString)
            }
        }

        override fun onFinish() {
            Log.d(Constants.DEBUG_TAG, "TimerService.Timer.onFinish()")
            cancel()
            timer = null
            currentPresetRemaining = null
            notificationHelper.playSound()
            runNextPreset()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(Constants.DEBUG_TAG, "TimerService.onDestroy()")
    }

    inner class LocalBinder: Binder() {
        fun getService(): TimerService = this@TimerService
    }

    companion object {
        const val ZERO_PRESET_DURATION = 0L
        const val ZERO_PRESET_INDEX = 0

        const val INITIALIZED = "info.erulinman.lifetimetracker.TIMER.INITIALIZED"
        const val STARTED     = "info.erulinman.lifetimetracker.TIMER.STARTED"
        const val STOPPED     = "info.erulinman.lifetimetracker.TIMER.STOPPED"
        const val FINISHED    = "info.erulinman.lifetimetracker.TIMER.FINISHED"
        const val CLOSED      = "info.erulinman.lifetimetracker.TIMER.CLOSED"

        const val START   = "info.erulinman.lifetimetracker.TIMER.START"
        const val STOP    = "info.erulinman.lifetimetracker.TIMER.STOP"
        const val RESTART = "info.erulinman.lifetimetracker.TIMER.RESTART"
        const val SKIP    = "info.erulinman.lifetimetracker.TIMER.SKIP"
        const val CLOSE   = "info.erulinman.lifetimetracker.TIMER.CLOSE"

        private const val ONE_SECOND_INTERVAL: Long = 1000
        private const val TIME_COMPENSATION: Long = 999
    }
}