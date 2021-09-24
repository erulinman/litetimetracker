package info.erulinman.lifetimetracker.ui

import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
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
    private lateinit var wayName: String

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
        binding.bottomAppBarLayout.fab.apply {
            setOnClickListener { fabOnClick() }
            setImageResource(R.drawable.ic_play)
        }
        wayName = intent.getStringExtra(Constants.WAY_NAME) ?: getString(R.string.default_preset_name)
        binding.bottomAppBarLayout.appBarTitle.text = wayName

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

        setPresetEditorFragmentListener()
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
            binding.bottomAppBarLayout.appBarTitle.text = counterText + nItems
            binding.bottomAppBarLayout.fab.setImageResource(R.drawable.ic_delete)
            fabOnClick = ::deleteSelectedPresets
        } else {
            binding.bottomAppBarLayout.appBarTitle.setText(wayName)
            binding.bottomAppBarLayout.fab.setImageResource(R.drawable.ic_play)
            fabOnClick = ::runTimerActivity
        }
    }

    private fun addNewPreset() {
        val newPresetEditorFragment = PresetEditorFragment()
        newPresetEditorFragment.show(supportFragmentManager, PresetEditorFragment.TAG)
    }

    private fun setPresetEditorFragmentListener() {
        supportFragmentManager.setFragmentResultListener(
            PresetEditorFragment.REQUEST_KEY,
            this
        ) { _, result ->
            val response = result.getInt(PresetEditorFragment.RESPONSE_KEY)
            if (response == DialogInterface.BUTTON_POSITIVE) {
                val update = result.getBoolean(PresetEditorFragment.UPDATE, false)
                val presetId = result.getLong(PresetEditorFragment.PRESET_ID)
                val wayId = result.getLong(PresetEditorFragment.WAY_ID)
                val presetName = result.getString(PresetEditorFragment.PRESET_NAME, Preset.DEFAULT_NAME)
                val presetTime = result.getLong(PresetEditorFragment.PRESET_TIME, Preset.DEFAULT_TIME)
                if (update) {
                    val updatedPreset = Preset(
                        id = presetId,
                        wayId = wayId,
                        name = presetName,
                        time = presetTime
                    )
                    presetViewModel.updatePreset(updatedPreset)
                } else {
                    presetViewModel.addNewPreset(presetName, presetTime)
                }
            }
        }
    }

    private fun runTimerActivity() {
        val intent = Intent(this, TimerActivity::class.java)
        intent.putExtra(EXTRA_PRESETS_IN_JSON, Json.encodeToString(presetViewModel.liveDataPresets.value))
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
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
        Log.d(Constants.DEBUG_TAG, "PresetActivity.presetOnClick()")
        val changePresetEditorFragment = PresetEditorFragment(preset)
        changePresetEditorFragment.show(supportFragmentManager, PresetEditorFragment.TAG)
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