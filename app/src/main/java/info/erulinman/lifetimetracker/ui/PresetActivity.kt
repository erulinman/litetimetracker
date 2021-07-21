package info.erulinman.lifetimetracker.ui

import android.os.Bundle
import android.widget.Toast

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import info.erulinman.lifetimetracker.adapters.PomodoroPresetAdapter
import info.erulinman.lifetimetracker.databinding.ActivityPresetBinding
import info.erulinman.lifetimetracker.MainApplication
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.utilities.WAY_ID
import info.erulinman.lifetimetracker.viewmodels.PomodoroPresetViewModel
import info.erulinman.lifetimetracker.viewmodels.PomodoroPresetViewModelFactory

class PresetActivity : AppCompatActivity() {
    private val pomodoroPresetViewModel by viewModels<PomodoroPresetViewModel> {
        PomodoroPresetViewModelFactory(
            (application as MainApplication).pomodoroPresetRepository
        )
    }
    private lateinit var binding: ActivityPresetBinding
    private lateinit var fabOnClick: () -> Unit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val presetAdapter = PomodoroPresetAdapter { adapterOnClick() }
        binding.recyclerView.adapter = presetAdapter

        pomodoroPresetViewModel.liveDataPomodoroPresets.observe(this, {
            it?.let { presetAdapter.submitList(it) }
        })
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

    private fun adapterOnClick() {
        Toast.makeText(
            this,
            "Show preset settings",
            Toast.LENGTH_SHORT
        )
    }
}