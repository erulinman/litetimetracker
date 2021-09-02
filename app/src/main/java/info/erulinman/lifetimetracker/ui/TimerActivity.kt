package info.erulinman.lifetimetracker.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(Constants.DEBUG_TAG, "TimerActivity.onCreate()")
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setServiceConnection()
        bindTimerService()
    }

    private fun bindTimerService() {
        Log.d(Constants.DEBUG_TAG, "TimerActivity.bindTimerService()")
        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    private fun setServiceConnection() {
        Log.d(Constants.DEBUG_TAG, "TimerActivity.setServiceConnection()")
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d(Constants.DEBUG_TAG, "TimerActivity.serviceConnection.onServiceConnected()")
                timerService = (service as TimerService.TimerServiceBinder).getService().apply {
                    val presets = intent.getStringExtra(
                        PresetActivity.EXTRA_PRESETS_IN_JSON
                    )?.let { presets_in_json ->
                        Json.decodeFromString<List<Preset>>(presets_in_json)
                    }
                    Log.d(Constants.DEBUG_TAG, "$presets")
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
                    TimerService.INITIALIZED -> runPresets()
                    TimerService.STOPPED  -> {
                        fabOnClick = { timerService.runPresets() }
                        binding.apply {
                            bottomAppBarLayout.fab.setImageResource(R.drawable.ic_play_24)
                            bottomAppBarLayout.appBarTitle.isVisible = true
                        }

                    }
                    TimerService.STARTED -> {
                        fabOnClick = { timerService.stopPresets() }
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