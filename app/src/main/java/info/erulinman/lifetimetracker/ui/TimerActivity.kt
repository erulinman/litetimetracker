package info.erulinman.lifetimetracker.ui

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import info.erulinman.lifetimetracker.databinding.ActivityTimerBinding
import info.erulinman.lifetimetracker.utilities.Constants

class TimerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimerBinding
    private lateinit var fabOnClick: () -> Unit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startOrPauseFab.setOnClickListener {
            fabOnClick()
        }
    }
}