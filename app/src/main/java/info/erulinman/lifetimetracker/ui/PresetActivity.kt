package info.erulinman.lifetimetracker.ui

import android.os.Bundle
import android.widget.Toast

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.ConcatAdapter

import info.erulinman.lifetimetracker.adapters.PresetAdapter
import info.erulinman.lifetimetracker.databinding.ActivityPresetBinding
import info.erulinman.lifetimetracker.MainApplication
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.adapters.AddPresetAdapter
import info.erulinman.lifetimetracker.selection.PresetItemDetailsLookup
import info.erulinman.lifetimetracker.selection.PresetItemKeyProvider
import info.erulinman.lifetimetracker.selection.PresetSelectionPredicate
import info.erulinman.lifetimetracker.utilities.WAY_ID
import info.erulinman.lifetimetracker.utilities.WAY_NAME
import info.erulinman.lifetimetracker.viewmodels.PresetViewModel
import info.erulinman.lifetimetracker.viewmodels.PresetViewModelFactory

class PresetActivity : AppCompatActivity() {
    private val presetViewModel by viewModels<PresetViewModel> {
        PresetViewModelFactory(
            (application as MainApplication).databaseRepository,
            intent.getLongExtra(WAY_ID, -1)
        )
    }
    private var tracker: SelectionTracker<Long>? = null
    private lateinit var binding: ActivityPresetBinding
    private lateinit var fabOnClick: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val presetAdapter = PresetAdapter { adapterOnClick() }
        val addPresetAdapter = AddPresetAdapter { addNewPreset() }
        val concatAdapter = ConcatAdapter(presetAdapter, addPresetAdapter)

        binding.recyclerView.adapter = concatAdapter
        submitUi(presetAdapter)

        fabOnClick = ::startNewTime
        binding.fab.apply {
            setOnClickListener { fabOnClick() }
            setImageResource(R.drawable.ic_play_24)
        }
        binding.appBarTitle.text = intent.getStringExtra(WAY_NAME)

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

    private fun submitUi(adapter: PresetAdapter) {
        presetViewModel.liveDataPresets.observe(this, {
            it?.let { adapter.submitList(it) }
        })
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
            fabOnClick = ::startNewTime
        }
    }

    private fun addNewPreset() {
        val presetFragment = PresetFragment()
        presetFragment.show(supportFragmentManager, PresetFragment.TAG)
    }

    private fun startNewTime() {
        Toast.makeText(
            this,
            "Start timer!",
            Toast.LENGTH_SHORT
        ).show()
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

    private fun adapterOnClick() {
        Toast.makeText(
            this,
            "Show preset settings",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        tracker?.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        tracker?.onSaveInstanceState(outState)
    }
}