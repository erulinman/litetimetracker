package info.erulinman.litetimetracker.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import dagger.Lazy
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.adapters.CategoryAdapter
import info.erulinman.litetimetracker.databinding.FragmentRecyclerViewBinding
import info.erulinman.litetimetracker.di.appComponent
import info.erulinman.litetimetracker.selection.CategoryItemDetailsLookup
import info.erulinman.litetimetracker.selection.CategoryItemKeyProvider
import info.erulinman.litetimetracker.fragments.dialogs.CategoryEditorFragment
import info.erulinman.litetimetracker.viewmodels.CategoryListViewModel
import info.erulinman.litetimetracker.viewmodels.CategoryListViewModelFactory
import java.lang.NullPointerException
import javax.inject.Inject

class CategoryListFragment : Fragment(R.layout.fragment_recycler_view), Selection {

    private var _adapter: CategoryAdapter? = null
    private val adapter: CategoryAdapter
        get() {
            checkNotNull(_adapter)
            return _adapter as CategoryAdapter
        }

    private var _binding: FragmentRecyclerViewBinding? = null
    private val binding: FragmentRecyclerViewBinding
        get() {
            checkNotNull(_binding)
            return _binding as FragmentRecyclerViewBinding
        }

    @Inject lateinit var viewModelFactory: Lazy<CategoryListViewModelFactory>

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory.get())
            .get(CategoryListViewModel::class.java)
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
        _adapter = CategoryAdapter { category -> categoryOnClick(category.id) }

        _binding = FragmentRecyclerViewBinding.bind(view).apply {
            recyclerView.adapter = adapter
            fab.setImageResource(R.drawable.ic_plus)
            fab.setOnClickListener { CategoryEditorFragment.show(parentFragmentManager) }
        }

        observeViewModel()

        setSelectionTracker()
        setTrackerObserver()

        setCategoryEditorFragmentListener()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setSelectionTracker() {
        tracker = SelectionTracker.Builder(
            SELECTION_TRACKER_ID,
            binding.recyclerView,
            CategoryItemKeyProvider(adapter),
            CategoryItemDetailsLookup(binding.recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
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

    private fun observeViewModel() = viewModel.apply {
        categories.observe(viewLifecycleOwner) {
            it?.let {adapter.submitList(it)}
        }
        hasSelection.observe(viewLifecycleOwner) { hasSelection ->
            binding.fab.isVisible = !hasSelection
            navigator().setToolbarActionVisibility(hasSelection)

            if (!hasSelection) {
                navigator().updateToolbar(getString(R.string.app_name), R.drawable.ic_edit) {
                    // TODO("Create fragment to show some HELP information)
                }
            } else {
                tracker?.let { tracker ->
                    val counter = tracker.selection.size()
                    val title = "${getString(R.string.tv_toolbar_selection)} $counter"
                    navigator().updateToolbar(title, R.drawable.ic_delete) {
                        viewModel.deleteSelectedCategories(tracker.selection.toList())
                    }
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
        const val SELECTION_TRACKER_ID = "CategoryListFragment.SELECTION_TRACKER_ID"
    }
}