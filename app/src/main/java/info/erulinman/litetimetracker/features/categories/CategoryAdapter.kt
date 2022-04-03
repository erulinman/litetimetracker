package info.erulinman.litetimetracker.features.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.data.entity.Preset
import info.erulinman.litetimetracker.databinding.RvItemCategoryBinding
import info.erulinman.litetimetracker.utils.ItemTouchCallback

class CategoryAdapter(
    private val viewModel: CategoryListViewModel,
    private val onClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>(),
    ItemTouchCallback.Adapter {

    private var items = listOf<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = RvItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(items[position], onClick)
    }

    override fun onItemSwipe(position: Int) {
        val item = items[position]
        viewModel.deleteCategory(item)
    }

    override fun onItemMove(from: Int, to: Int) {
        notifyItemMoved(from, to)
    }

    override fun onClearView(targetViewHolder: RecyclerView.ViewHolder) {
        val order = (targetViewHolder as CategoryViewHolder).order!!
        val currentPosition = targetViewHolder.absoluteAdapterPosition
        if (order != currentPosition && currentPosition != RecyclerView.NO_POSITION)
            viewModel.onChangePositions(order, currentPosition)
    }

    override fun getItemCount() = items.size

    fun submitList(list: List<Category>) {
        items = list
        notifyDataSetChanged()
    }

    class CategoryViewHolder(private val binding: RvItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var order: Int? = null

        fun bind(category: Category, onClick: (Category) -> Unit) {
            order = category.position
            itemView.setOnClickListener { onClick(category) }
            binding.categoryName.text = category.name
        }
    }
}