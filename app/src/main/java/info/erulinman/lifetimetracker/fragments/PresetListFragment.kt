package info.erulinman.lifetimetracker.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.ConcatAdapter
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.adapters.AddButtonAdapter
import info.erulinman.lifetimetracker.adapters.PresetAdapter
import info.erulinman.lifetimetracker.data.entity.Category
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.databinding.FragmentRecyclerViewBinding
import info.erulinman.lifetimetracker.di.appComponent
import info.erulinman.lifetimetracker.fragments.dialogs.CategoryEditorFragment
import info.erulinman.lifetimetracker.selection.PresetItemDetailsLookup
import info.erulinman.lifetimetracker.selection.PresetItemKeyProvider
import info.erulinman.lifetimetracker.fragments.dialogs.PresetEditorFragment
import info.erulinman.lifetimetracker.selection.PresetSelectionPredicate
import info.erulinman.lifetimetracker.viewmodels.PresetListViewModel
import info.erulinman.lifetimetracker.viewmodels.PresetListViewModelFactory
import javax.inject.Inject

class PresetListFragment : Fragment(R.layout.fragment_recycler_view), Selection {

    private var _adapter: PresetAdapter? = null
    private val adapter: PresetAdapter
        get() {
            checkNotNull(_adapter)
            return _adapter as PresetAdapter
        }

    private var _binding: FragmentRecyclerViewBinding? = null
    private val binding: FragmentRecyclerViewBinding
        get() {
            checkNotNull(_binding)
            return _binding as FragmentRecyclerViewBinding
        }

    @Inject lateinit var viewModelFactory: PresetListViewModelFactory.Factory

    private val viewModel: PresetListViewModel by viewModels {
        viewModelFactory.create(requireArguments().getLong(ARG_CATEGORY_ID))
    }

    private var tracker: SelectionTracker<Long>? = null

    override val hasSelection: Boolean
        get() = tracker?.hasSelection() ?: false

    override fun cancelSelection() {
        tracker?.clearSelection()
    }

    override fun onAttach(context: Context) {
        appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _adapter = PresetAdapter { preset -> editPreset(preset) }
        val addButtonImplAdapter = AddButtonAdapter { addPreset() }
        val concatAdapter = ConcatAdapter(adapter, addButtonImplAdapter)

        _binding = FragmentRecyclerViewBinding.bind(view).apply {
            recyclerView.adapter = concatAdapter
        }

        observeViewModel()

        setSelectionTracker()
        setTrackerObserver()

        setPresetEditorFragmentListener()
        setCategoryEditorFragmentListener()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setSelectionTracker() {
        tracker = SelectionTracker.Builder(
            SELECTION_TRACKER_ID,
            binding.recyclerView,
            PresetItemKeyProvider(adapter),
            PresetItemDetailsLookup(binding.recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            // Used a custom predicate to exclude selection of AddButtonAdapter element
            PresetSelectionPredicate()
        ).build()

        adapter.setTracker(tracker)
    }

    private fun runTimerFragment() {
        viewModel.presets.value?.let { presets ->
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
                val presetId = getLong(PresetEditorFragment.PRESET_ID, EMPTY)
                if (presetId != EMPTY) {
                    val categoryId = getLong(PresetEditorFragment.CATEGORY_ID)
                    val updatedPreset = Preset(presetId, categoryId, presetName, presetTime)
                    viewModel.updatePreset(updatedPreset)
                    return@setFragmentResultListener
                }
                viewModel.addNewPreset(presetName, presetTime)
            }
        }}
    }

    private fun editCategory(category: Category) {
        CategoryEditorFragment.show(parentFragmentManager, category)
    }

    private fun setCategoryEditorFragmentListener() {
        parentFragmentManager.setFragmentResultListener(
            CategoryEditorFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result -> with(result) {
            if (getInt(CategoryEditorFragment.RESPONSE_KEY) == DialogInterface.BUTTON_POSITIVE) {
                val categoryId = getLong(CategoryEditorFragment.CATEGORY_ID)
                val categoryName = getString(CategoryEditorFragment.CATEGORY_NAME)!! // null check in CategoryEditorFragment
                viewModel.updateCategory(Category(categoryId, categoryName))
            }
        }}
    }

    private fun setTrackerObserver() {
        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    notifyViewModelAboutSelectionStatus()
                }

                override fun onSelectionRestored() {
                    notifyViewModelAboutSelectionStatus()
                }
            }
        )
    }

    private fun notifyViewModelAboutSelectionStatus() {
        tracker?.selection?.let {
            viewModel.hasSelection.value = it.size() > 0
        }
    }

    private fun observeViewModel() = viewModel.apply {
        presets.observe(viewLifecycleOwner) { presets ->
            presets?.let {
                adapter.submitList(it)
            }
        }
        category.observe(viewLifecycleOwner) {
            it?.let { hasSelection.refresh() }
        }
        hasSelection.observe(viewLifecycleOwner) { hasSelection ->
            if (!hasSelection) {
                category.value?.let{ category ->
                    navigator().updateAppBar(R.drawable.ic_play, category.name) {
                        runTimerFragment()
                    }
                    navigator().setOnClickListenerToAppBarTitle { editCategory(category) }
                }
            } else {
                tracker?.let { tracker ->
                    val counter = tracker.selection.size()
                    val title = "${getString(R.string.app_bar_title_counter)} $counter"
                    navigator().updateAppBar(R.drawable.ic_delete, title) {
                        viewModel.deleteSelectedPresets(tracker.selection.toList())
                    }
                    navigator().setOnClickListenerToAppBarTitle(null)
                }
            }
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

    override fun onDestroyView() {
        _adapter = null
        _binding = null
        tracker = null
        super.onDestroyView()
    }

    companion object {
        private const val EMPTY = -1L
        const val SELECTION_TRACKER_ID = "PresetListFragment.SELECTION_TRACKER_ID"
        const val ARG_CATEGORY_ID = "PresetListFragment.ARG_CATEGORY_ID"

        fun newInstance(categoryId: Long) = PresetListFragment().apply {
            arguments = bundleOf(ARG_CATEGORY_ID to categoryId)
        }
    }
}