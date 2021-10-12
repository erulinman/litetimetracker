package info.erulinman.lifetimetracker.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
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
import info.erulinman.lifetimetracker.data.entity.Category
import info.erulinman.lifetimetracker.databinding.FragmentCategoryListBinding
import info.erulinman.lifetimetracker.selection.CategoryItemDetailsLookup
import info.erulinman.lifetimetracker.selection.CategoryItemKeyProvider
import info.erulinman.lifetimetracker.fragments.dialogs.AddCategoryFragment
import info.erulinman.lifetimetracker.utilities.Constants
import info.erulinman.lifetimetracker.viewmodels.CategoryListViewModel
import info.erulinman.lifetimetracker.viewmodels.CategoryListViewModelFactory
import java.lang.NullPointerException

class CategoryListFragment : Fragment(), Selection {
    private val categoryListViewModel by viewModels<CategoryListViewModel> {
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
        val categoryAdapter = CategoryAdapter { category -> categoryOnClick(category) }
        binding.recyclerView.adapter = categoryAdapter
        submitUi(categoryAdapter)
        setDefaultAppBar()

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
        setAddCategoryFragmentListener()

        return binding.root
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

    private fun setAddCategoryFragmentListener() {
        parentFragmentManager.setFragmentResultListener(
            AddCategoryFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result -> with(result) {
            if (getInt(AddCategoryFragment.RESPONSE_KEY) == DialogInterface.BUTTON_POSITIVE) {
                val categoryName = getString(AddCategoryFragment.CATEGORY_NAME) ?:
                    throw NullPointerException("null name's value as a result of editing")
                categoryListViewModel.addNewCategory(categoryName)
            }
        }}
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
                    categoryListViewModel.deleteSelectedCategories(selection.toList())
                }
                return
            }
        }
        setDefaultAppBar()
    }

    private fun setDefaultAppBar() {
        navigator().updateAppBar(
            R.drawable.ic_plus,
            getString(R.string.app_name)
        ) {
            AddCategoryFragment.show(parentFragmentManager)
        }
    }

    private fun categoryOnClick(category: Category) {
        val fragment = PresetListFragment.newInstance(category)
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.mainFragmentContainer, fragment)
        }
    }

    private fun submitUi(categoryAdapter: CategoryAdapter) {
        categoryListViewModel.liveDataCategory.observe(viewLifecycleOwner, {
            it?.let {categoryAdapter.submitList(it)}
        })
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