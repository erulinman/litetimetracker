package info.erulinman.lifetimetracker.ui

import android.os.Bundle
import android.os.CountDownTimer

import androidx.appcompat.app.AppCompatActivity
import info.erulinman.lifetimetracker.R

import info.erulinman.lifetimetracker.databinding.ActivityTimerBinding

class TimerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimerBinding
    private lateinit var fabOnClick: (timer: CountDownTimer) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var counter = 0

        val timer = object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                binding.timerTextView.setText("${ millisUntilFinished / 1000 }")
            }

            override fun onFinish() {
                binding.timerTextView.setText("done!")
            }
        }
        start(timer)

        fabOnClick = ::pause
        binding.startOrPauseFab.setOnClickListener {
            fabOnClick(timer)
        }
    }

    private fun pause(timer: CountDownTimer) {
        timer.cancel()
        binding.startOrPauseFab.setImageResource(R.drawable.ic_play_24)
    }

    private fun start(timer: CountDownTimer) {
        timer.start()
        binding.startOrPauseFab.setImageResource(R.drawable.ic_pause_24)
    }
}