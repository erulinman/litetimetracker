package info.erulinman.lifetimetracker.wayList

import androidx.recyclerview.selection.ItemKeyProvider


class WayItemKeyProvider(private val adapter: WayAdapter) :
    ItemKeyProvider<Long>(SCOPE_CACHED) {
    override fun getKey(position: Int): Long? =
        adapter.currentList[position].id

    override fun getPosition(key: Long): Int =
        adapter.currentList.indexOfFirst { it.id == key }
}