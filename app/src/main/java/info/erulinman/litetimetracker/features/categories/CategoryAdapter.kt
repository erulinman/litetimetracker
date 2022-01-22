package info.erulinman.litetimetracker.features.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.databinding.RvItemCategoryBinding
import info.erulinman.litetimetracker.utils.DiffUtilCallback

class CategoryAdapter(private val onClick: (Category) -> Unit) :
    ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(DiffUtilCallback<Category>()) {

    private var tracker: SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = RvItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        tracker?.let {
            val item = getItem(position)
            holder.bind(item, it.isSelected(item.id))
        }
    }

    fun setTracker(tracker: SelectionTracker<Long>?) {
        this.tracker = tracker
    }

    inner class CategoryViewHolder(private val binding: RvItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, isSelected: Boolean = false) {
            itemView.setOnClickListener { onClick(category) }
            binding.categoryName.text = category.name
            binding.tickPointImage.isVisible = isSelected
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {

                override fun getPosition(): Int = bindingAdapterPosition

                override fun getSelectionKey(): Long =
                    (getItem(position) as Category).id
            }
    }
}