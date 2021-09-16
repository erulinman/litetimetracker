package info.erulinman.lifetimetracker.selection

import androidx.recyclerview.selection.ItemKeyProvider

import info.erulinman.lifetimetracker.adapters.WayAdapter


class WayItemKeyProvider(private val adapter: WayAdapter) :
    ItemKeyProvider<Long>(SCOPE_CACHED) {
    override fun getKey(position: Int): Long =
        adapter.currentList[position].id

    override fun getPosition(key: Long): Int =
        adapter.currentList.indexOfFirst { it.id == key }
}