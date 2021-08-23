package info.erulinman.lifetimetracker.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.ConcatAdapter

import info.erulinman.lifetimetracker.adapters.PresetAdapter
import info.erulinman.lifetimetracker.databinding.ActivityPresetBinding
import info.erulinman.lifetimetracker.MainApplication
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.adapters.AddPresetAdapter
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.selection.PresetItemDetailsLookup
import info.erulinman.lifetimetracker.selection.PresetItemKeyProvider
import info.erulinman.lifetimetracker.selection.PresetSelectionPredicate
import info.erulinman.lifetimetracker.utilities.Constants
import info.erulinman.lifetimetracker.viewmodels.PresetViewModel
import info.erulinman.lifetimetracker.viewmodels.PresetViewModelFactory
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class PresetActivity : AppCompatActivity() {
    private val presetViewModel by viewModels<PresetViewModel> {
        PresetViewModelFactory(
            (application as MainApplication).databaseRepository,
            intent.getLongExtra(Constants.WAY_ID, -1)
        )
    }
    private var tracker: SelectionTracker<Long>? = null
    private lateinit var binding: ActivityPresetBinding
    private lateinit var fabOnClick: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val presetAdapter = PresetAdapter { preset -> presetOnClick(preset) }
        val addPresetAdapter = AddPresetAdapter { addNewPreset() }
        val concatAdapter = ConcatAdapter(presetAdapter, addPresetAdapter)

        binding.recyclerView.adapter = concatAdapter
        submitUi(presetAdapter)
        fabOnClick = ::runTimerActivity
        binding.fab.apply {
            setOnClickListener { fabOnClick() }
            setImageResource(R.drawable.ic_play_24)
        }
        binding.appBarTitle.text = intent.getStringExtra(Constants.WAY_NAME)

        tracker = SelectionTracker.Builder(
            "PresetActivity selection tracker",
            binding.recyclerView,
            PresetItemKeyProvider(presetAdapter),
            PresetItemDetailsLookup(binding.recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            PresetSelectionPredicate()
        ).build()

        presetAdapter.setTracker(tracker)

        setTrackerObserver()

        setPresetFragmentListener()
    }

    private fun submitUi(adapter: PresetAdapter) {
        presetViewModel.liveDataPresets.observe(this, {
            it?.let { adapter.submitList(it) }
        })
    }

    private fun setTrackerObserver() {
        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    showTheNumberOfSelectedItems()
                }

                override fun onSelectionRestored() {
                    showTheNumberOfSelectedItems()
                }
            }
        )
    }

    private fun showTheNumberOfSelectedItems() {
        val nItems: Int? = tracker?.selection?.size()
        val counterText = "Selected: "
        if (nItems != null && nItems > 0) {
            binding.appBarTitle.text = counterText + nItems
            binding.fab.setImageResource(R.drawable.ic_delete_24)
            fabOnClick = ::deleteSelectedPresets
        } else {
            binding.appBarTitle.setText(R.string.app_name)
            binding.fab.setImageResource(R.drawable.ic_play_24)
            fabOnClick = ::runTimerActivity
        }
    }

    private fun addNewPreset() {
        val newPresetFragment = PresetFragment()
        newPresetFragment.show(supportFragmentManager, PresetFragment.TAG)
    }

    private fun setPresetFragmentListener() {
        supportFragmentManager.setFragmentResultListener(
            PresetFragment.REQUEST_KEY,
            this
        ) { _, result ->
            val response = result.getInt(PresetFragment.KEY_RESPONSE)
            if (response == DialogInterface.BUTTON_POSITIVE) {
                val presetName = result.getString(PresetFragment.PRESET_NAME) ?: Preset.DEFAULT_NAME
                val presetTime = result.getString(PresetFragment.PRESET_TIME) ?: Preset.DEFAULT_TIME
                val updatedPresetInString = result.getString(PresetFragment.UPDATED_PRESET)
                updatedPresetInString?.let {
                    Log.d(Constants.DEBUG_TAG, "PresetActivity.setPresetFragmentListener()")
                    presetViewModel.updatePreset(Json.decodeFromString(it))
                } ?: run {
                    Log.d(Constants.DEBUG_TAG, "PresetActivity.setPresetFragmentListener()")
                    presetViewModel.addNewPreset(presetName, presetTime)
                }
            }
        }
    }

    private fun runTimerActivity() {
        val intent = Intent(this, TimerActivity::class.java)
        intent.putExtra(EXTRA_PRESETS_IN_JSON, Json.encodeToString(presetViewModel.liveDataPresets.value))
        Log.d(Constants.DEBUG_TAG, "PresetActivity.runTimerActivity()")
        startActivity(intent)
    }

    private fun deleteSelectedPresets() {
        tracker?.selection?.let {
            presetViewModel.deleteSelectedPresets(it.toList())
        }
    }

    override fun onBackPressed() {
        if (tracker?.hasSelection() == true) {
            tracker?.clearSelection()
        } else {
            super.onBackPressed()
        }
    }

    private fun presetOnClick(preset: Preset) {
        val changePresetFragment = PresetFragment(preset)
        changePresetFragment.show(supportFragmentManager, PresetFragment.TAG)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        tracker?.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        tracker?.onSaveInstanceState(outState)
    }

    companion object {
        const val EXTRA_PRESETS_IN_JSON = "info.erulinman.lifetimetracker.EXTRA_PRESETS_IN_JSON"
    }
}