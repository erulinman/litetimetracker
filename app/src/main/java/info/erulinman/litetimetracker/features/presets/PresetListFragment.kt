package info.erulinman.litetimetracker.features.presets

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.base.BaseFragment
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.data.entity.Preset
import info.erulinman.litetimetracker.databinding.FragmentPresetListBinding
import info.erulinman.litetimetracker.di.appComponent
import info.erulinman.litetimetracker.features.categories.CategoryEditorFragment
import info.erulinman.litetimetracker.features.timer.TimerFragment
import info.erulinman.litetimetracker.utils.ItemTouchCallback
import javax.inject.Inject

class PresetListFragment : BaseFragment<FragmentPresetListBinding>() {

    @Inject
    lateinit var viewModelFactory: PresetListViewModelFactory.Factory

    private val viewModel: PresetListViewModel by viewModels {
        viewModelFactory.create(requireArguments().getLong(ARG_CATEGORY_ID))
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPresetListBinding.inflate(inflater, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding.toolbar) {
            setActionVisibility(true)
            setActionIcon(R.drawable.ic_edit)
        }

        val presetAdapter = PresetAdapter(viewModel) { preset ->
            navigator.showDialog(
                PresetEditorFragment.getInstanceWithArg(preset),
                PresetEditorFragment.TAG
            )
        }
        val addButtonAdapter = AddButtonAdapter {
            navigator.showDialog(PresetEditorFragment(), PresetEditorFragment.TAG)
        }
        val concatAdapter = ConcatAdapter(presetAdapter, addButtonAdapter)

        setupRecyclerView(concatAdapter, presetAdapter)
        observeViewModel(presetAdapter)

        binding.fab.setImageResource(R.drawable.ic_play)
        binding.fab.setOnClickListener {
            val fragment = TimerFragment.getInstanceWithArg(viewModel.presets.value!!)
            navigator.navigateTo(fragment, true)
        }

        setPresetEditorFragmentListener()
        setCategoryEditorFragmentListener()
    }

    private fun setupRecyclerView(concatAdapter: ConcatAdapter, presetAdapter: PresetAdapter) {
        binding.recyclerView.adapter = concatAdapter
        val callback = ItemTouchCallback(presetAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

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
                        val position = getInt(PresetEditorFragment.PRESET_POSITION)
                        val updatedPreset =
                            Preset(presetId, categoryId, position, presetName, presetTime)
                        viewModel.updatePreset(updatedPreset)
                        return@setFragmentResultListener
                    }
                    viewModel.addNewPreset(presetName, presetTime)
                }
            }
        }
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
                    val position = getInt(CategoryEditorFragment.CATEGORY_POSITION)
                    viewModel.updateCategory(Category(categoryId, position, categoryName))
                }
            }
        }
    }

    private fun observeViewModel(adapter: PresetAdapter) = viewModel.apply {
        presets.observe(viewLifecycleOwner) { presets ->
            if (presets == null) return@observe
            adapter.submitList(presets)
            binding.fab.isVisible = presets.isNotEmpty()
        }
        category.observe(viewLifecycleOwner) { category ->
            if (category == null) return@observe
            binding.toolbar.setTitle(category.name)
            binding.toolbar.setOnActionClickListener {
                navigator.showDialog(
                    CategoryEditorFragment.getInstanceWithArg(category),
                    CategoryEditorFragment.TAG
                )
            }
        }
    }

    companion object {
        private const val EMPTY = -1L
        const val ARG_CATEGORY_ID = "PresetListFragment.ARG_CATEGORY_ID"

        fun getInstanceWithArg(categoryId: Long) = PresetListFragment().apply {
            arguments = bundleOf(ARG_CATEGORY_ID to categoryId)
        }
    }
}