package info.erulinman.lifetimetracker.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import info.erulinman.lifetimetracker.MainApplication
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.adapters.PresetAdapter
import info.erulinman.lifetimetracker.data.entity.Category
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.databinding.FragmentPresetListBinding
import info.erulinman.lifetimetracker.selection.PresetItemDetailsLookup
import info.erulinman.lifetimetracker.selection.PresetItemKeyProvider
import info.erulinman.lifetimetracker.fragments.dialogs.PresetEditorFragment
import info.erulinman.lifetimetracker.utilities.Constants
import info.erulinman.lifetimetracker.viewmodels.PresetListViewModel
import info.erulinman.lifetimetracker.viewmodels.PresetListViewModelFactory
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

class PresetListFragment : Fragment(), Selection {
    private val presetListViewModel by viewModels<PresetListViewModel> {
        val categoryId = arguments?.getLong(ARG_CATEGORY_ID) ?:
            throw IllegalArgumentException("there is no category id to open preset list")
        PresetListViewModelFactory(
            (requireActivity().application as MainApplication).databaseRepository,
            categoryId
        )
    }
    private var tracker: SelectionTracker<Long>? = null
    private lateinit var binding: FragmentPresetListBinding

    override val hasSelection: Boolean
        get() = tracker?.hasSelection() ?: false

    override fun cancelSelection() {
        tracker?.clearSelection()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPresetListBinding.inflate(inflater, container, false)
        val presetAdapter = PresetAdapter { preset -> editPreset(preset) }
        binding.recyclerView.adapter = presetAdapter
        submitUi(presetAdapter)
        setDefaultAppBar()

        tracker = SelectionTracker.Builder(
            SELECTION_TRACKER_ID,
            binding.recyclerView,
            PresetItemKeyProvider(presetAdapter),
            PresetItemDetailsLookup(binding.recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        presetAdapter.setTracker(tracker)
        setTrackerObserver()
        setPresetEditorFragmentListener()

        return binding.root
    }

    private fun runTimerFragment() {
        presetListViewModel.liveDataPresets.value?.let { presets ->
            if (presets.isNotEmpty()) {
                val fragment = TimerFragment.newInstance(presets as ArrayList)
                parentFragmentManager.commit {
                    addToBackStack(null)
                    replace(R.id.mainFragmentContainer, fragment)
                }
            } else {
                navigator().showToast(R.string.toast_message_no_presets)
            }
        }
    }

    private fun editPreset(preset: Preset) = PresetEditorFragment.show(parentFragmentManager, preset)

    private fun addPreset() = PresetEditorFragment.show(parentFragmentManager)

    private fun setPresetEditorFragmentListener() {
        parentFragmentManager.setFragmentResultListener(
            PresetEditorFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result -> with(result) {
            if (getInt(PresetEditorFragment.RESPONSE_KEY) == DialogInterface.BUTTON_POSITIVE) {
                val presetName = getString(PresetEditorFragment.PRESET_NAME) ?: throw NullPointerException("null name's value as a result of editing")
                val presetTime = getLong(PresetEditorFragment.PRESET_TIME)
                if (getBoolean(PresetEditorFragment.UPDATE)) {
                    val presetId = getLong(PresetEditorFragment.PRESET_ID)
                    val categoryId = getLong(PresetEditorFragment.CATEGORY_ID)
                    val updatedPreset = Preset(presetId, categoryId, presetName, presetTime)
                    presetListViewModel.updatePreset(updatedPreset)
                    return@setFragmentResultListener
                }
                presetListViewModel.addNewPreset(presetName, presetTime)
            }
        }}
    }

    private fun setTrackerObserver() {
        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    updateAppBarOnSelection()
                }

                override fun onSelectionRestored() {
                    updateAppBarOnSelection()
                }
            }
        )
    }

    private fun updateAppBarOnSelection() {
        tracker?.selection?.let { selection ->
            val selected = selection.size()
            Log.d(Constants.DEBUG_TAG, "Selected: $selected")
            if (selected > 0) {
                val barTitle = "${getString(R.string.app_bar_title_counter)} $selected"
                navigator().updateAppBar(
                    R.drawable.ic_delete,
                    barTitle
                ) {
                    presetListViewModel.deleteSelectedPresets(selection.toList())
                }
                return
            }
        }
        setDefaultAppBar()
    }

    private fun setDefaultAppBar() {
        navigator().updateAppBar(
            R.drawable.ic_play,
            arguments?.getString(ARG_CATEGORY_NAME)!!
        ) {
            runTimerFragment()
        }
    }

    private fun submitUi(presetAdapter: PresetAdapter) {
        presetListViewModel.liveDataPresets.observe(viewLifecycleOwner, {
            Log.d(Constants.DEBUG_TAG, "new data: $it")
            it?.let { presetAdapter.submitList(it) }
        })
        binding.addNewPresetButton.setOnClickListener {
            addPreset()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        tracker?.onRestoreInstanceState(savedInstanceState)
    }

    companion object {
        const val SELECTION_TRACKER_ID = "PresetListFragment.SELECTION_TRACKER_ID"
        const val ARG_CATEGORY_ID = "PresetListFragment.ARG_CATEGORY_ID"
        const val ARG_CATEGORY_NAME = "PresetListFragment.ARG_CATEGORY_NAME"

        fun newInstance(category: Category) = PresetListFragment().apply {
            arguments = bundleOf(
                ARG_CATEGORY_ID to category.id,
                ARG_CATEGORY_NAME to category.name
            )
        }
    }
}