package info.erulinman.litetimetracker.features.presets

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import info.erulinman.litetimetracker.BaseFragment
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.data.entity.Preset
import info.erulinman.litetimetracker.databinding.FragmentPresetListBinding
import info.erulinman.litetimetracker.di.appComponent
import info.erulinman.litetimetracker.features.timer.TimerFragment
import info.erulinman.litetimetracker.utils.ItemTouchCallback
import javax.inject.Inject

class PresetListFragment : BaseFragment<FragmentPresetListBinding>(R.layout.fragment_preset_list) {

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _adapter = PresetAdapter(viewModel) { preset -> editPreset(preset) }
        val addButtonAdapter = AddButtonAdapter { addPreset() }
        val concatAdapter = ConcatAdapter(adapter, addButtonAdapter)
        _binding = FragmentPresetListBinding.bind(view).apply {
            fab.setImageResource(R.drawable.ic_play)
            fab.setOnClickListener { runTimerFragment() }
        }
        toolbar.setActionVisibility(true)
        setupRecyclerView(concatAdapter)
        observeViewModel()
        setPresetEditorFragmentListener()
        setCategoryEditorFragmentListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }

    private fun setupRecyclerView(concatAdapter: ConcatAdapter) =
        binding.recyclerView.let { recyclerView ->
            recyclerView.adapter = concatAdapter
            val callback = ItemTouchCallback(adapter)
            val itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }

    private fun runTimerFragment() = parentFragmentManager.commit {
        val presets = viewModel.presets.value!!
        val fragment = TimerFragment.newInstance(presets as ArrayList)
        addToBackStack(null)
        replace(R.id.mainFragmentContainer, fragment)
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

    private fun editCategory(category: Category) =
        CategoryEditorFragment.show(parentFragmentManager, category)

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

    private fun observeViewModel() = viewModel.apply {
        presets.observe(viewLifecycleOwner) { presets ->
            if (presets == null) return@observe
            adapter.submitList(presets)
            binding.fab.isVisible = presets.isNotEmpty()
        }
        category.observe(viewLifecycleOwner) { category ->
            if (category == null) return@observe
            toolbar.updateToolbar(category.name, R.drawable.ic_edit) {
                editCategory(category)
            }
        }
    }

    companion object {
        private const val EMPTY = -1L
        const val ARG_CATEGORY_ID = "PresetListFragment.ARG_CATEGORY_ID"

        fun newInstance(categoryId: Long) = PresetListFragment().apply {
            arguments = bundleOf(ARG_CATEGORY_ID to categoryId)
        }
    }
}