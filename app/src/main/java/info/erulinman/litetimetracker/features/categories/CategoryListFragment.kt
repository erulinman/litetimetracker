package info.erulinman.litetimetracker.features.categories

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.Lazy
import info.erulinman.litetimetracker.BaseFragment
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.databinding.FragmentCategoryListBinding
import info.erulinman.litetimetracker.di.appComponent
import info.erulinman.litetimetracker.features.presets.CategoryEditorFragment
import info.erulinman.litetimetracker.features.presets.PresetListFragment
import info.erulinman.litetimetracker.utils.ItemTouchCallback
import javax.inject.Inject

class CategoryListFragment :
    BaseFragment<FragmentCategoryListBinding>(R.layout.fragment_category_list) {

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _adapter = CategoryAdapter(viewModel) { category ->
            navigator.navigateTo(
                PresetListFragment.getInstanceWithArg(category.id),
                true
            )
        }
        _binding = FragmentCategoryListBinding.bind(view).apply {
            fab.setImageResource(R.drawable.ic_plus)
            fab.setOnClickListener {
                navigator.showDialog(CategoryEditorFragment())
            }
        }
        toolbar.updateToolbar(getString(R.string.app_name), R.drawable.ic_edit) {
            // TODO("Feature not implemented yet")
        }
        toolbar.setActionVisibility(false)
        setupRecyclerView()
        observeViewModel()
        setCategoryEditorFragmentListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }

    private fun setupRecyclerView() = binding.recyclerView.let { recyclerView ->
        recyclerView.adapter = adapter
        val callback = ItemTouchCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

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

    private fun observeViewModel() = viewModel.apply {
        categories.observe(viewLifecycleOwner) { categories ->
            if (categories == null) return@observe
            binding.emptyMessage.isVisible = categories.isEmpty()
            adapter.submitList(categories)
        }
    }
}