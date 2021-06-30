package info.erulinman.lifetimetracker.wayList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.lifetimetracker.data.Way
import info.erulinman.lifetimetracker.R

class WayAdapter(private val onClick: (Way) -> Unit) :
    ListAdapter<Way, WayAdapter.WayViewHolder>(WayDiffCallback) {
    class WayViewHolder(itemView: View, val onClick: (Way) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val wayTextView: TextView = itemView.findViewById(R.id.way_detail_text)
        private var currentWay: Way? = null

        init {
            itemView.setOnClickListener {
                currentWay?.let {
                    Log.d("CHECKING", "check init in adapter: ${it}")
                    onClick(it)
                }
            }
        }

        fun bind(way: Way) {
            Log.d("CHECKING", "check bind in adapter")
            currentWay = way
            wayTextView.text = way.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WayViewHolder {
        Log.d(TAG, "onCreateViewHolder runing")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.way_item, parent, false)
        return WayViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: WayViewHolder, position: Int) {
        Log.d(TAG, "check onBindViewHolder fun")
        val way = getItem(position)
        holder.bind(way)
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