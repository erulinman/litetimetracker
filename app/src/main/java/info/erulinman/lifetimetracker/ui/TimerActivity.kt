package info.erulinman.lifetimetracker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Chronometer

import androidx.appcompat.app.AppCompatActivity
import info.erulinman.lifetimetracker.R

import info.erulinman.lifetimetracker.databinding.ActivityTimerBinding
import info.erulinman.lifetimetracker.PomodoroService

class TimerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimerBinding
    private lateinit var fabOnClick: (timer: Chronometer) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fabOnClick = ::startTimer
        runPomodoroService()
    }

    private fun runPomodoroService() {
        val intent = Intent(this, PomodoroService::class.java)
        startService(intent)
    }

    private fun pauseTimer(timer: Chronometer) {
        timer.stop()
        binding.startOrPauseFab.setImageResource(R.drawable.ic_play_24)
        fabOnClick = ::startTimer
    }

    private fun startTimer(timer: Chronometer) {
        timer.start()
        binding.startOrPauseFab.setImageResource(R.drawable.ic_pause_24)
        fabOnClick = ::pauseTimer
    }
}