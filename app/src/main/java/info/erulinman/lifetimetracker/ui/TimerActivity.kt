package info.erulinman.lifetimetracker.ui

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import info.erulinman.lifetimetracker.databinding.ActivityTimerBinding

class TimerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}