package info.erulinman.lifetimetracker.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.lifetimetracker.adapters.CategoryAdapter

class CategoryItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        view?.let {
            return (recyclerView.getChildViewHolder(it) as CategoryAdapter.CategoryViewHolder)
                .getItemDetails()
        }
        return null
    }
}