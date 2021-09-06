package info.erulinman.lifetimetracker.ui

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.data.entity.Preset

import info.erulinman.lifetimetracker.databinding.ActivityTimerBinding
import info.erulinman.lifetimetracker.model.TimerService
import info.erulinman.lifetimetracker.utilities.Constants
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class TimerActivity : AppCompatActivity() {
    private lateinit var timerService: TimerService
    private lateinit var serviceConnection: ServiceConnection
    private lateinit var binding: ActivityTimerBinding
    private lateinit var fabOnClick: () -> Unit
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(Constants.DEBUG_TAG, "TimerActivity.onCreate()")
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d(Constants.DEBUG_TAG, "TimerActivity.broadcastReceiver.onReceive()")
                if (intent?.action == TimerService.CLOSE) finish()
            }
        }
        localBroadcastManager = LocalBroadcastManager.getInstance(this).apply {
            registerReceiver(broadcastReceiver, IntentFilter(TimerService.CLOSE))
        }
        setServiceConnection()
        Intent(this, TimerService::class.java).also { intent ->
            startForegroundService(intent)
        }
    }

    override fun onStart() {
        Log.d(Constants.DEBUG_TAG, "TimerActivity.onStart()")
        bindTimerService()
        super.onStart()
    }

    override fun onStop() {
        Log.d(Constants.DEBUG_TAG, "TimerActivity.onStop()")
        unbindService(serviceConnection)
        super.onStop()
    }

    override fun onBackPressed() {
        timerService.closeService()
        super.onBackPressed()
    }
    private fun bindTimerService() {
        Log.d(Constants.DEBUG_TAG, "TimerActivity.bindTimerService()")
        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_ABOVE_CLIENT
        )
    }

    private fun setServiceConnection() {
        Log.d(Constants.DEBUG_TAG, "TimerActivity.setServiceConnection()")
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d(Constants.DEBUG_TAG, "TimerActivity.serviceConnection.onServiceConnected()")
                timerService = (service as TimerService.LocalBinder).getService().apply {
                    val presets = intent.getStringExtra(
                        PresetActivity.EXTRA_PRESETS_IN_JSON
                    )?.let { presets_in_json ->
                        Json.decodeFromString<List<Preset>>(presets_in_json)
                    }
                    if (presets != null) {
                        loadPresets(presets)
                    }
                }
                setObservers()
                setOnFabClickListener()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d(Constants.DEBUG_TAG, "TimerActivity.serviceConnection.onServiceDisconnected()")
                //TODO: rebind to service?
            }
        }
    }

    private fun setOnFabClickListener() {
        Log.d(Constants.DEBUG_TAG, "TimerActivity.setOnFabClickListener()")
        binding.bottomAppBarLayout.fab.setOnClickListener {
            fabOnClick()
        }
    }

    private fun setObservers() {
        Log.d(Constants.DEBUG_TAG, "TimerActivity.setObservers()")
        timerService.apply {
            presetName.observe(this@TimerActivity) { presetName ->
                binding.bottomAppBarLayout.appBarTitle.text = presetName
            }
            time.observe(this@TimerActivity) { time ->
                binding.timer.text = time
            }
            state.observe(this@TimerActivity) { state ->
                when(state) {
                    TimerService.INITIALIZED -> timerService.startTimer()
                    TimerService.STOPPED  -> {
                        fabOnClick = { timerService.stopTimer() }
                        binding.apply {
                            bottomAppBarLayout.fab.setImageResource(R.drawable.ic_play_24)
                            bottomAppBarLayout.appBarTitle.isVisible = true
                        }

                    }
                    TimerService.STARTED -> {
                        fabOnClick = { timerService.stopTimer() }
                        binding.apply {
                            bottomAppBarLayout.fab.setImageResource(R.drawable.ic_pause_24)
                            bottomAppBarLayout.appBarTitle.isVisible = true
                        }

                    }
                    TimerService.FINISHED -> {
                        fabOnClick = { timerService.restartPresets() }
                        binding.apply {
                            bottomAppBarLayout.fab.setImageResource(R.drawable.ic_restart_24)
                            bottomAppBarLayout.appBarTitle.isVisible = false
                        }
                    }
                }
            }
        }
    }
}