package info.erulinman.litetimetracker.categories

import androidx.recyclerview.selection.ItemKeyProvider

class CategoryItemKeyProvider(private val adapter: CategoryAdapter) :
    ItemKeyProvider<Long>(SCOPE_CACHED) {
    override fun getKey(position: Int): Long =
        adapter.currentList[position].id

    override fun getPosition(key: Long): Int =
        adapter.currentList.indexOfFirst { it.id == key }
}