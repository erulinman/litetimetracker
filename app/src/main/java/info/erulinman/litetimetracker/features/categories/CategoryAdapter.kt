package info.erulinman.litetimetracker.features.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.databinding.RvItemCategoryBinding
import info.erulinman.litetimetracker.utils.DiffUtilCallback
import info.erulinman.litetimetracker.utils.ItemTouchCallback

class CategoryAdapter(
    private val viewModel: CategoryListViewModel,
    private val onClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(DiffUtilCallback<Category>()),
    ItemTouchCallback.Adapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = RvItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(currentList[position], onClick)
    }

    class CategoryViewHolder(private val binding: RvItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, onClick: (Category) -> Unit) {
            itemView.setOnClickListener { onClick(category) }
            binding.categoryName.text = category.name
        }
    }

    override fun onItemSwipe(position: Int) {
        val item = currentList[position]
        viewModel.deleteCategory(item)
    }
}