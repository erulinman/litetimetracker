package info.erulinman.lifetimetracker.wayDetail

import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import info.erulinman.lifetimetracker.R

import info.erulinman.lifetimetracker.databinding.ActivityWayDetailBinding
import info.erulinman.lifetimetracker.wayList.WAY_ID
import info.erulinman.lifetimetracker.wayList.TAG


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
        Log.d(TAG, "ActivityWayDetail.fab was clicked")
    }
}