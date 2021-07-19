package info.erulinman.lifetimetracker

import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import info.erulinman.lifetimetracker.databinding.ActivityWayDetailBinding
import info.erulinman.lifetimetracker.utilities.WAY_ID

class WayDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWayDetailBinding
    private lateinit var fabOnClick: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWayDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fabOnClick = ::startNewTime
        binding.bottomAppBarLayout.fab.apply {
            setOnClickListener { fabOnClick() }
            setImageResource(R.drawable.ic_play_24)
        }
        binding.bottomAppBarLayout.appBarTitle.text = intent.getStringExtra(WAY_ID)
    }

    private fun startNewTime() {
        Toast.makeText(
            this,
            "Start timer!",
            Toast.LENGTH_SHORT
        ).show()
    }
}