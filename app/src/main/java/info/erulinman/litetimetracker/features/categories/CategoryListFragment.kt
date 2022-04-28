package info.erulinman.litetimetracker.features.categories

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.Lazy
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.base.BaseFragment
import info.erulinman.litetimetracker.databinding.FragmentCategoryListBinding
import info.erulinman.litetimetracker.di.appComponent
import info.erulinman.litetimetracker.features.presets.PresetListFragment
import info.erulinman.litetimetracker.utils.ItemTouchCallback
import info.erulinman.litetimetracker.utils.setDarkStatusBar
import javax.inject.Inject

class CategoryListFragment : BaseFragment<FragmentCategoryListBinding>() {

    @Inject
    lateinit var viewModelFactory: Lazy<CategoryListViewModelFactory>

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory.get())
            .get(CategoryListViewModel::class.java)
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCategoryListBinding.inflate(inflater, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setDarkStatusBar()

        with(binding.toolbar) {
            setActionVisibility(false)
            setTitle(R.string.app_name)
        }

        val adapter = CategoryAdapter(viewModel) { category ->
            navigator.navigateTo(
                PresetListFragment.getInstanceWithArg(category.id),
                true
            )
        }

        setupRecyclerView(adapter)
        observeViewModel(adapter)

        binding.fab.setImageResource(R.drawable.ic_plus)
        binding.fab.setOnClickListener {
            navigator.showDialog(CategoryEditorFragment(), CategoryEditorFragment.TAG)
        }

        setCategoryEditorFragmentListener()
    }

    private fun setupRecyclerView(adapter: CategoryAdapter) {
        binding.rvCategories.adapter = adapter
        val callback = ItemTouchCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvCategories)
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

    private fun observeViewModel(adapter: CategoryAdapter) = viewModel.apply {
        categories.observe(viewLifecycleOwner) { categories ->
            if (categories == null) return@observe
            binding.emptyMessage.isVisible = categories.isEmpty()
            adapter.submitList(categories)
        }
    }
}