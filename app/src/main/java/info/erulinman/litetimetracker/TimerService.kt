package info.erulinman.litetimetracker


import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.erulinman.litetimetracker.data.entity.Preset
import info.erulinman.litetimetracker.di.appComponent
import info.erulinman.litetimetracker.utilities.DEBUG_TAG
import info.erulinman.litetimetracker.utilities.toListHHMMSS
import info.erulinman.litetimetracker.utilities.toStringHHMMSS
import javax.inject.Inject

class TimerService: Service() {
    private val binder = LocalBinder()

    private val presets = mutableListOf<Preset>()

    private var currentPresetDuration: Long = ZERO_PRESET_DURATION
    private var currentPresetRemaining: Long? = null
    private var currentPresetIndex: Int = ZERO_PRESET_INDEX

    private val _time = MutableLiveData<String>()
    val time: LiveData<String> get() = _time

    private val _presetName = MutableLiveData<String>()
    val presetName: LiveData<String> get() = _presetName

    private val _state = MutableLiveData(INITIALIZED)
    val state: LiveData<String> get() = _state

    private val _canSkip = MutableLiveData(false)
    val canSkip: LiveData<Boolean> get() = _canSkip

    private var timer: Timer? = null

    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        Log.d(DEBUG_TAG, "TimerService.onCreate()")
        binder.set(this) // manual setter because getting memory leak from inner LocalBinder
        appComponent.inject(this)
        startForeground(
            NotificationHelper.NOTIFICATION_ID,
            notificationHelper.getStartedNotificationBuilder().build()
        )
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(DEBUG_TAG, "TimerService.onStartCommand()")
        intent?.let {
            when (it.action) {
                START -> startTimer()
                STOP -> stopTimer()
                RESTART_ALL -> restartPresets()
                RESTART_CURRENT -> restartCurrentPreset()
                SKIP -> skipPreset()
                CLOSE -> closeService(true)
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(DEBUG_TAG, "TimerService.onTaskRemoved()")
        closeService()
    }

    fun loadPresets(_presets: List<Preset>) {
        if (state.value == INITIALIZED && _presets.isNotEmpty()) {
            Log.d(DEBUG_TAG, "TimerService.loadPresets()")
            presets.apply {
                addAll(_presets)
                first().let { firstPreset ->
                    _time.value = firstPreset.time.toListHHMMSS().toStringHHMMSS()
                    _presetName.value = firstPreset.name
                    currentPresetDuration = firstPreset.time
                    _canSkip.value = true
                }
            }
        }
    }

    fun startTimer() {
        Log.d(DEBUG_TAG, "TimerService.startTimer()")
        if (timer == null) {
            timer = Timer(
                currentPresetRemaining ?: (currentPresetDuration + TIME_COMPENSATION)
            ).apply { start() }
            _state.value = STARTED
        } else {
            throw IllegalStateException("There is old timer")
        }
    }

    fun stopTimer() {
        Log.d(DEBUG_TAG, "TimerService.stopTimer()")
        timer?.cancel()
        timer = null
        _state.value = STOPPED
        time.value?.let { notificationHelper.notifyWhenStopped(it) }
    }

    fun skipPreset() {
        Log.d(DEBUG_TAG, "TimerService.skipPreset()")
        stopTimer()
        currentPresetRemaining = null
        runNextPreset()
    }

    fun restartPresets() {
        if (_state.value == FINISHED) {
            presets.first().let { firstPreset ->
                currentPresetIndex = 0
                _canSkip.value = true
                _time.value = firstPreset.time.toListHHMMSS().toStringHHMMSS()
                _presetName.value = firstPreset.name
                currentPresetDuration = firstPreset.time
                startTimer()
            }
        }
    }

    fun restartCurrentPreset() {
        if (state.value == INITIALIZED || state.value == FINISHED) return
        if (state.value != STOPPED) stopTimer()
        currentPresetRemaining = null
        startTimer()
    }

    private fun runNextPreset() {
        Log.d(DEBUG_TAG, "TimerService.runNextPreset()")
        currentPresetIndex++
        presets.getOrNull(currentPresetIndex)?.let { nextPreset ->
            currentPresetDuration = nextPreset.time
            _presetName.value = nextPreset.name
            startTimer()
            return
        }
        _state.value = FINISHED
        _canSkip.value = false
        _time.value = getString(R.string.text_view_timer_finished)
        notificationHelper.notifyWhenFinished()
    }

    fun closeService(sendBroadcast: Boolean = false) {
        Log.d(DEBUG_TAG, "TimerService.closeService()")
        stopTimer()
        stopForeground(true)
        stopSelf()
        if (sendBroadcast) {
            Log.d(DEBUG_TAG, "TimerService.sendBroadcast()")
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(CLOSE))
        }
    }

    override fun onDestroy() {
        Log.d(DEBUG_TAG, "TimerService.onDestroy()")
        binder.set(null)
        super.onDestroy()
    }

    class LocalBinder: Binder() {
        private var service: TimerService? = null

        fun set(service: TimerService?) {
            this.service = service
        }

        fun getService(): TimerService {
            return service ?: throw NullPointerException("Got null value from bound service")
        }
    }

    private inner class Timer(millisInFuture: Long) : CountDownTimer(millisInFuture, ONE_SECOND_INTERVAL) {
        override fun onTick(millisUntilFinished: Long) {
            if (millisUntilFinished < ONE_SECOND_INTERVAL) {
                onFinish()
            } else {
                val timeInString = millisUntilFinished.toListHHMMSS().toStringHHMMSS()
                Log.d(
                    DEBUG_TAG,
                    "TimerService.Timer.onTick(): on string - $timeInString, on long - $millisUntilFinished}"
                )
                _time.value = timeInString
                currentPresetRemaining = millisUntilFinished
                notificationHelper.updateStartedNotification(timeInString, presetName.value!!)
            }
        }

        override fun onFinish() {
            cancel()
            timer = null
            currentPresetRemaining = null
            notificationHelper.playSound()
            runNextPreset()
        }
    }

    companion object {
        const val ZERO_PRESET_DURATION = 0L
        const val ZERO_PRESET_INDEX = 0

        const val INITIALIZED = "info.erulinman.litetimetracker.TIMER.INITIALIZED"
        const val STARTED = "info.erulinman.litetimetracker.TIMER.STARTED"
        const val STOPPED = "info.erulinman.litetimetracker.TIMER.STOPPED"
        const val FINISHED = "info.erulinman.litetimetracker.TIMER.FINISHED"

        const val START = "info.erulinman.litetimetracker.TIMER.START"
        const val STOP = "info.erulinman.litetimetracker.TIMER.STOP"
        const val RESTART_ALL = "info.erulinman.litetimetracker.TIMER.RESTART_ALL"
        const val RESTART_CURRENT = "info.erulinman.litetimetracker.TIMER.RESTART_CURRENT"
        const val SKIP = "info.erulinman.litetimetracker.TIMER.SKIP"
        const val CLOSE = "info.erulinman.litetimetracker.TIMER.CLOSE"

        private const val ONE_SECOND_INTERVAL: Long = 1000
        private const val TIME_COMPENSATION: Long = 999
    }
}
