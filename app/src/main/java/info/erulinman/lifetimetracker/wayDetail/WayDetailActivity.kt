package info.erulinman.lifetimetracker.wayDetail

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import info.erulinman.lifetimetracker.databinding.ActivityWayDetailBinding
import info.erulinman.lifetimetracker.wayList.WAY_ID


class WayDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWayDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWayDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.textView.text = intent.getStringExtra(WAY_ID)
    }
}