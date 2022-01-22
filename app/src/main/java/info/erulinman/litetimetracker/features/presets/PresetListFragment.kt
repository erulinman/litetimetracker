package info.erulinman.litetimetracker.features.presets

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.ConcatAdapter
import info.erulinman.litetimetracker.BaseFragment
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.Selection
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.data.entity.Preset
import info.erulinman.litetimetracker.databinding.FragmentPresetListBinding
import info.erulinman.litetimetracker.di.appComponent
import info.erulinman.litetimetracker.features.timer.TimerFragment
import javax.inject.Inject

class PresetListFragment :
    BaseFragment<FragmentPresetListBinding>(R.layout.fragment_preset_list), Selection {

    private var _adapter: PresetAdapter? = null
    private val adapter: PresetAdapter
        get() {
            checkNotNull(_adapter)
            return _adapter as PresetAdapter
        }

    @Inject
    lateinit var viewModelFactory: PresetListViewModelFactory.Factory

    private val viewModel: PresetListViewModel by viewModels {
        viewModelFactory.create(requireArguments().getLong(ARG_CATEGORY_ID))
    }

    private var tracker: SelectionTracker<Long>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _adapter = PresetAdapter { preset -> editPreset(preset) }
        val addButtonAdapter = AddButtonAdapter { addPreset() }
        val concatAdapter = ConcatAdapter(adapter, addButtonAdapter)

        toolbar.setToolbarActionVisibility(true)

        _binding = FragmentPresetListBinding.bind(view).apply {
            recyclerView.adapter = concatAdapter
            fab.setImageResource(R.drawable.ic_play)
            fab.setOnClickListener { runTimerFragment() }
        }

        observeViewModel()

        setSelectionTracker()
        setTrackerObserver()

        setPresetEditorFragmentListener()
        setCategoryEditorFragmentListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        tracker = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        tracker?.onRestoreInstanceState(savedInstanceState)
    }

    override fun onBackPressed() = !cancelSelection()

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

    override fun cancelSelection() = tracker?.clearSelection() ?: false

    private fun runTimerFragment() {
        viewModel.presets.value?.let { presets ->
            if (presets.isNotEmpty()) {
                val fragment = TimerFragment.newInstance(presets as ArrayList)
                parentFragmentManager.commit {
                    addToBackStack(null)
                    replace(R.id.mainFragmentContainer, fragment)
                }
            } else {
                toolbar.showToast(R.string.toast_no_presets)
            }
        }
    }

    private fun editPreset(preset: Preset) =
        PresetEditorFragment.show(parentFragmentManager, preset)

    private fun addPreset() = PresetEditorFragment.show(parentFragmentManager)

    private fun setPresetEditorFragmentListener() {
        parentFragmentManager.setFragmentResultListener(
            PresetEditorFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            with(result) {
                if (getInt(PresetEditorFragment.RESPONSE_KEY) == DialogInterface.BUTTON_POSITIVE) {
                    val presetName = getString(PresetEditorFragment.PRESET_NAME)
                        ?: throw NullPointerException("null name's value as a result of editing")
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
            }
        }
    }

    private fun editCategory(category: Category) {
        CategoryEditorFragment.show(parentFragmentManager, category)
    }

    private fun setCategoryEditorFragmentListener() {
        parentFragmentManager.setFragmentResultListener(
            CategoryEditorFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            with(result) {
                if (getInt(CategoryEditorFragment.RESPONSE_KEY) == DialogInterface.BUTTON_POSITIVE) {
                    val categoryId = getLong(CategoryEditorFragment.CATEGORY_ID)
                    val categoryName =
                        getString(CategoryEditorFragment.CATEGORY_NAME)!! // null check in CategoryEditorFragment
                    viewModel.updateCategory(Category(categoryId, categoryName))
                }
            }
        }
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
            binding.fab.isVisible = !hasSelection

            if (!hasSelection) {
                category.value?.let { category ->
                    toolbar.updateToolbar(category.name, R.drawable.ic_edit) {
                        editCategory(category)
                    }
                }
            } else {
                tracker?.let { tracker ->
                    val counter = tracker.selection.size()
                    val title = "${getString(R.string.tv_toolbar_selection)} $counter"
                    toolbar.updateToolbar(title, R.drawable.ic_delete) {
                        viewModel.deleteSelectedPresets(tracker.selection.toList())
                    }
                }
            }
        }
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