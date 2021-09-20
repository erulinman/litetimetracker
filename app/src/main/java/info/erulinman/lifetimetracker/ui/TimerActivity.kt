package info.erulinman.lifetimetracker.ui

import android.content.*
import android.os.Bundle
import android.os.IBinder

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.data.entity.Preset

import info.erulinman.lifetimetracker.databinding.ActivityTimerBinding
import info.erulinman.lifetimetracker.model.TimerService
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
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
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
        bindTimerService()
        super.onStart()
    }

    override fun onStop() {
        unbindService(serviceConnection)
        super.onStop()
    }

    override fun onBackPressed() {
        timerService.closeService()
        super.onBackPressed()
    }
    private fun bindTimerService() {
        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_ABOVE_CLIENT
        )
    }

    private fun setServiceConnection() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
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
                //TODO: rebind to service?
            }
        }
    }

    private fun setOnFabClickListener() {
        binding.bottomAppBarLayout.fab.setOnClickListener {
            fabOnClick()
        }
        binding.nextButton.setOnClickListener {
            timerService.skipPreset()
        }
    }

    private fun setObservers() {
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
                        fabOnClick = { timerService.startTimer() }
                        binding.apply {
                            bottomAppBarLayout.fab.setImageResource(R.drawable.ic_play)
                            bottomAppBarLayout.appBarTitle.isVisible = true
                        }
                    }
                    TimerService.STARTED -> {
                        fabOnClick = { timerService.stopTimer() }
                        binding.apply {
                            bottomAppBarLayout.fab.setImageResource(R.drawable.ic_pause)
                            bottomAppBarLayout.appBarTitle.isVisible = true
                        }
                    }
                    TimerService.FINISHED -> {
                        fabOnClick = { timerService.restartPresets() }
                        binding.apply {
                            bottomAppBarLayout.fab.setImageResource(R.drawable.ic_restart)
                            bottomAppBarLayout.appBarTitle.isVisible = false
                        }
                    }
                }
            }
            canSkip.observe(this@TimerActivity) { canSkip ->
                binding.nextButton.isVisible = canSkip
            }
        }
    }
}