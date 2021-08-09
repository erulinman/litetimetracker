package info.erulinman.lifetimetracker.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.data.entity.Preset

import info.erulinman.lifetimetracker.databinding.ActivityTimerBinding
import info.erulinman.lifetimetracker.model.TimerService
import info.erulinman.lifetimetracker.utilities.Constants

class TimerActivity : AppCompatActivity() {
    private lateinit var timerService: TimerService
    private lateinit var binding: ActivityTimerBinding
    private lateinit var fabOnClick: () -> Unit
    private var bound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, TimerService::class.java)
        val connect = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d(Constants.DEBUG_TAG, "service connected")
                timerService = (service as TimerService.MyBinder).getService()
                bound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                bound = false
                Log.d(Constants.DEBUG_TAG, "service disconnected")
            }

            override fun onBindingDied(name: ComponentName?) {
                Log.d(Constants.DEBUG_TAG, "onBindingDied()")
                super.onBindingDied(name)

            }

        }
        bindService(intent, connect, BIND_AUTO_CREATE)

        fabOnClick = ::startTimer
        binding.startOrPauseFab.setOnClickListener {
            fabOnClick()
        }

        timerService.time.observe(this, { time ->
            time?.let {
                binding.timer.text = time.toString()
            }
        })
    }

    private fun startTimer() {
        Log.d(Constants.DEBUG_TAG, "start timer")
        sendIntentToService(TimerService.ACTION.START)
        fabOnClick = ::stopTimer
        binding.startOrPauseFab.setImageResource(R.drawable.ic_pause_24)
    }

    private fun stopTimer() {
        sendIntentToService(TimerService.ACTION.STOP)
        fabOnClick = ::startTimer
        binding.startOrPauseFab.setImageResource(R.drawable.ic_play_24)
    }

    private fun sendIntentToService(_action: String) {
        val intent = Intent(this, TimerService::class.java).apply {
            action = _action
        }
        startService(intent)
    }

    override fun onBackPressed() {
        stopTimer()
        super.onBackPressed()
    }
}