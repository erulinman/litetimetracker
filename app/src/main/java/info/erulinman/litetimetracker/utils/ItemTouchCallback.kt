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
        val dragFlags = UP or DOWN
        val swipeFlags = LEFT or RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemSwipe(viewHolder.bindingAdapterPosition)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        adapter.onClearView(viewHolder)
    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return target !is AddButtonAdapter.AddButtonViewHolder
    }

    interface Adapter {

        fun onItemSwipe(position: Int)

        fun onItemMove(from: Int, to: Int)

        fun onClearView(targetViewHolder: RecyclerView.ViewHolder)
    }
}