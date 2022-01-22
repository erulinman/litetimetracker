package info.erulinman.litetimetracker.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.databinding.RvItemCategoryBinding

class CategoryAdapter(private val onClick: (Category) -> Unit) :
    ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback) {
    private var tracker: SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = RvItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(view, onClick)
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

    inner class CategoryViewHolder(
        private val binding: RvItemCategoryBinding,
        val onClick: (Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var thisCategory: Category? = null

        init {
            itemView.setOnClickListener {
                thisCategory?.let {
                    onClick(it)
                }
            }
        }

        fun bind(category: Category, isSelected: Boolean = false) {
            thisCategory = category
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

object CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
        oldItem == newItem
}