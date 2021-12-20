package info.erulinman.lifetimetracker.selection

import androidx.recyclerview.selection.SelectionTracker

class PresetSelectionPredicate : SelectionTracker.SelectionPredicate<Long>() {
    override fun canSetStateForKey(key: Long, nextState: Boolean) =
        key != UNSELECTED

    override fun canSetStateAtPosition(position: Int, nextState: Boolean) = true

    override fun canSelectMultiple() = true

    companion object {
        const val UNSELECTED = -1L
    }
}