package info.erulinman.litetimetracker.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.litetimetracker.adapters.AddButtonAdapter
import info.erulinman.litetimetracker.adapters.PresetAdapter

class PresetItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        view?.let {
            val viewHolder = recyclerView.getChildViewHolder(it)
            if (viewHolder is AddButtonAdapter.AddButtonViewHolder) {
                return viewHolder.getItemDetails()
            }
            return (viewHolder as PresetAdapter.PresetViewHolder)
                .getItemDetails()
        }
        return null
    }
}