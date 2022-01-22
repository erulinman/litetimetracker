package info.erulinman.litetimetracker.features.categories

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import dagger.Lazy
import info.erulinman.litetimetracker.BaseFragment
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.Selection
import info.erulinman.litetimetracker.databinding.FragmentCategoryListBinding
import info.erulinman.litetimetracker.di.appComponent
import info.erulinman.litetimetracker.features.presets.CategoryEditorFragment
import info.erulinman.litetimetracker.features.presets.PresetListFragment
import javax.inject.Inject

class CategoryListFragment :
    BaseFragment<FragmentCategoryListBinding>(R.layout.fragment_category_list), Selection {

    private var _adapter: CategoryAdapter? = null
    private val adapter: CategoryAdapter
        get() {
            checkNotNull(_adapter)
            return _adapter as CategoryAdapter
        }

    @Inject
    lateinit var viewModelFactory: Lazy<CategoryListViewModelFactory>

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory.get())
            .get(CategoryListViewModel::class.java)
    }

    private var tracker: SelectionTracker<Long>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _adapter = CategoryAdapter { category -> categoryOnClick(category.id) }

        _binding = FragmentCategoryListBinding.bind(view).apply {
            recyclerView.adapter = adapter
            fab.setImageResource(R.drawable.ic_plus)
            fab.setOnClickListener { CategoryEditorFragment.show(parentFragmentManager) }
        }

        observeViewModel()

        setSelectionTracker()
        setTrackerObserver()

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

    override fun cancelSelection() = tracker?.clearSelection() ?: false

    private fun setCategoryEditorFragmentListener() {
        parentFragmentManager.setFragmentResultListener(
            CategoryEditorFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            with(result) {
                if (getInt(CategoryEditorFragment.RESPONSE_KEY) == DialogInterface.BUTTON_POSITIVE) {
                    val categoryName = getString(CategoryEditorFragment.CATEGORY_NAME)
                        ?: throw NullPointerException("null name's value as a result of editing")
                    viewModel.addNewCategory(categoryName)
                }
            }
        }
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
        categories.observe(viewLifecycleOwner) { categories ->
            if (categories == null) return@observe
            binding.emptyMessage.isVisible = categories.isEmpty()
            adapter.submitList(categories)
        }
        hasSelection.observe(viewLifecycleOwner) { hasSelection ->
            binding.fab.isVisible = !hasSelection
            toolbar.setToolbarActionVisibility(hasSelection)

            if (!hasSelection) {
                toolbar.updateToolbar(getString(R.string.app_name), R.drawable.ic_edit) {
                    // TODO("Create fragment to show some HELP information)
                }
            } else {
                tracker?.let { tracker ->
                    val counter = tracker.selection.size()
                    val title = "${getString(R.string.tv_toolbar_selection)} $counter"
                    toolbar.updateToolbar(title, R.drawable.ic_delete) {
                        viewModel.deleteSelectedCategories(tracker.selection.toList())
                    }
                }
            }
        }
    }

    companion object {
        const val SELECTION_TRACKER_ID = "CategoryListFragment.SELECTION_TRACKER_ID"
    }
}