package info.erulinman.lifetimetracker.selection

import androidx.recyclerview.selection.ItemKeyProvider
import info.erulinman.lifetimetracker.adapters.PresetAdapter
import info.erulinman.lifetimetracker.utilities.UNSELECTED

class PresetItemKeyProvider(private val adapter: PresetAdapter)
    : ItemKeyProvider<Long>(SCOPE_CACHED) {

    override fun getKey(position: Int): Long {
        // if, when you try to select several items with a
        // long swipe, touch AddPresetButton item, PresetAdapter
        // goes out of bounds in the list of presets
        return try {
            adapter.currentList[position].id
        } catch (e: IndexOutOfBoundsException) {
            UNSELECTED
        }
    }

    override fun getPosition(key: Long): Int =
        adapter.currentList.indexOfFirst { it.id == key }
}