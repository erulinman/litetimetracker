package info.erulinman.lifetimetracker.wayList

import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class ItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            Log.d(TAG, "WayItemDetailsLookup.getItemDetails() = !null")
            return (recyclerView.getChildViewHolder(view) as WayAdapter.WayViewHolder)
                .getItemDetails()
        }
        Log.d(TAG, "WayItemDetailsLookup.getItemDetails() = null")
        return null
    }
}