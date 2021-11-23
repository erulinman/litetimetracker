package info.erulinman.lifetimetracker.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import info.erulinman.lifetimetracker.MainApplication
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.adapters.CategoryAdapter
import info.erulinman.lifetimetracker.databinding.FragmentCategoryListBinding
import info.erulinman.lifetimetracker.selection.CategoryItemDetailsLookup
import info.erulinman.lifetimetracker.selection.CategoryItemKeyProvider
import info.erulinman.lifetimetracker.fragments.dialogs.CategoryEditorFragment
import info.erulinman.lifetimetracker.viewmodels.CategoryListViewModel
import info.erulinman.lifetimetracker.viewmodels.CategoryListViewModelFactory
import java.lang.NullPointerException

class CategoryListFragment : Fragment(), Selection {
    private val viewModel by viewModels<CategoryListViewModel> {
        CategoryListViewModelFactory(
            (requireActivity().application as MainApplication).databaseRepository
        )
    }
    private var tracker: SelectionTracker<Long>? = null
    private lateinit var binding: FragmentCategoryListBinding

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
        binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        val categoryAdapter = CategoryAdapter { category -> categoryOnClick(category.id) }
        binding.recyclerView.adapter = categoryAdapter
        observeViewModel(categoryAdapter)

        tracker = SelectionTracker.Builder(
            SELECTION_TRACKER_ID,
            binding.recyclerView,
            CategoryItemKeyProvider(categoryAdapter),
            CategoryItemDetailsLookup(binding.recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        categoryAdapter.setTracker(tracker)
        setTrackerObserver()
        setCategoryEditorFragmentListener()

        return binding.root
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

    private fun setCategoryEditorFragmentListener() {
        parentFragmentManager.setFragmentResultListener(
            CategoryEditorFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result -> with(result) {
            if (getInt(CategoryEditorFragment.RESPONSE_KEY) == DialogInterface.BUTTON_POSITIVE) {
                val categoryName = getString(CategoryEditorFragment.CATEGORY_NAME) ?:
                    throw NullPointerException("null name's value as a result of editing")
                viewModel.addNewCategory(categoryName)
            }
        }}
    }

    private fun categoryOnClick(categoryId: Long) {
        val fragment = PresetListFragment.newInstance(categoryId)
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.mainFragmentContainer, fragment)
        }
    }

    private fun notifyViewModelAboutSelectionStatus() {
        tracker?.selection?.let {
            viewModel.hasSelection.value = it.size() > 0
        }
    }

    private fun observeViewModel(categoryAdapter: CategoryAdapter) = viewModel.apply {
        categories.observe(viewLifecycleOwner) {
            it?.let {categoryAdapter.submitList(it)}
        }
        hasSelection.observe(viewLifecycleOwner) { hasSelection ->
            if (!hasSelection) {
                navigator().updateAppBar(R.drawable.ic_plus, getString(R.string.app_name)) {
                    CategoryEditorFragment.show(parentFragmentManager)
                }
                navigator().setOnClickListenerToAppBarTitle(null)
            } else {
                tracker?.let { tracker ->
                    val counter = tracker.selection.size()
                    val title = "${getString(R.string.app_bar_title_counter)} $counter"
                    navigator().updateAppBar(R.drawable.ic_delete, title) {
                        viewModel.deleteSelectedCategories(tracker.selection.toList())
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

    companion object {
        const val SELECTION_TRACKER_ID = "CategoryListFragment.SELECTION_TRACKER_ID"
    }
}