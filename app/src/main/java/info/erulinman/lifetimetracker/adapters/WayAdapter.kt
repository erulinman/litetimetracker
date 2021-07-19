package info.erulinman.lifetimetracker.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import info.erulinman.lifetimetracker.data.Way
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.utilities.DEBUG_TAG


class WayAdapter(private val onClick: (Way) -> Unit) :
    ListAdapter<Way, WayAdapter.WayViewHolder>(WayDiffCallback) {
    private var tracker: SelectionTracker<Long>? = null

    inner class WayViewHolder(itemView: View, val onClick: (Way) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val wayTextView: TextView = itemView.findViewById(R.id.way_detail_text)
        private val tickPointImage: ImageView = itemView.findViewById(R.id.tickPointImage)
        private var currentWay: Way? = null

        init {
            itemView.setOnClickListener {
                currentWay?.let {
                    onClick(it)
                }
            }
        }

        fun bind(way: Way, isSelected: Boolean = false) {
            Log.d("CHECKING", "WayAdapter.WayViewHolder.bind()")
            currentWay = way
            wayTextView.text = way.name
            tickPointImage.isVisible = isSelected
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> {
            return object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = bindingAdapterPosition
                override fun getSelectionKey(): Long? =
                    (getItem(position) as Way).wayId
            }
        }
    }

    /*init {
        setHasStableIds(true)
    }*/

    fun setTracker(tracker: SelectionTracker<Long>?) {
        this.tracker = tracker
    }

    //override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WayViewHolder {
        Log.d(DEBUG_TAG, "WayAdapter.onCreateViewHolder()")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.way_item, parent, false)
        return WayViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: WayViewHolder, position: Int) {
        tracker?.let {
            val item = getItem(position)
            holder.bind(item, it.isSelected(item.wayId))
        }
    }
}

object WayDiffCallback: DiffUtil.ItemCallback<Way>() {
    override fun areItemsTheSame(oldItem: Way, newItem: Way): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Way, newItem: Way): Boolean {
        return oldItem.wayId == newItem.wayId
    }
}