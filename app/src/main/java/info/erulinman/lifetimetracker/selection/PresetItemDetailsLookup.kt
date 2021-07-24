package info.erulinman.lifetimetracker.selection

import android.view.MotionEvent

import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.lifetimetracker.adapters.AddPresetAdapter

import info.erulinman.lifetimetracker.adapters.PresetAdapter

class PresetItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            if (recyclerView.getChildViewHolder(view) is AddPresetAdapter.AddPresetViewHolder) {
                return (recyclerView.getChildViewHolder(view) as AddPresetAdapter.AddPresetViewHolder)
                    .getItemDetails()
            }
            return (recyclerView.getChildViewHolder(view) as PresetAdapter.PresetViewHolder)
                .getItemDetails()
        }
        return null
    }
}