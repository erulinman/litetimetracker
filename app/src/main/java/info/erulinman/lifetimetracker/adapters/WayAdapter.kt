package info.erulinman.lifetimetracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import info.erulinman.lifetimetracker.databinding.ListItemWayBinding
import info.erulinman.lifetimetracker.data.entity.Way

class WayAdapter(private val onClick: (Way) -> Unit) :
    ListAdapter<Way, WayAdapter.WayViewHolder>(WayDiffCallback) {
    private var tracker: SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WayViewHolder {
        val view = ListItemWayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WayViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: WayViewHolder, position: Int) {
        tracker?.let {
            val item = getItem(position)
            holder.bind(item, it.isSelected(item.id))
        }
    }

    fun setTracker(tracker: SelectionTracker<Long>?) {
        this.tracker = tracker
    }

    inner class WayViewHolder(
        private val binding: ListItemWayBinding,
        val onClick: (Way) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var thisWay: Way? = null
        init {
            itemView.setOnClickListener {
                thisWay?.let {
                    onClick(it)
                }
            }
        }

        fun bind(way: Way, isSelected: Boolean = false) {
            thisWay = way
            binding.wayDetailText.text = way.name
            binding.tickPointImage.isVisible = isSelected
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = bindingAdapterPosition

                override fun getSelectionKey(): Long? =
                    (getItem(position) as Way).id
            }
    }
}

object WayDiffCallback: DiffUtil.ItemCallback<Way>() {
    override fun areItemsTheSame(oldItem: Way, newItem: Way): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Way, newItem: Way): Boolean {
        return oldItem.id == newItem.id
    }
}