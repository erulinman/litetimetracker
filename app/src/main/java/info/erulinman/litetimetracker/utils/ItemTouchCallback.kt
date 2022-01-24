package info.erulinman.litetimetracker.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.litetimetracker.features.presets.AddButtonAdapter

class ItemTouchCallback<T : ItemTouchCallback.Adapter>(private val adapter: T) :
    ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (viewHolder is AddButtonAdapter.AddButtonViewHolder) {
            return makeMovementFlags(ACTION_STATE_IDLE, ACTION_STATE_IDLE)
        }
        val dragFlags = ACTION_STATE_IDLE
        val swipeFlags = LEFT or RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // TODO("Not yet implemented")
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemSwipe(viewHolder.bindingAdapterPosition)
    }

    interface Adapter {

        fun onItemSwipe(position: Int)
    }
}