package info.erulinman.lifetimetracker.selection

import androidx.recyclerview.selection.SelectionTracker

class PresetSelectionPredicate : SelectionTracker.SelectionPredicate<Long>() {
    override fun canSetStateForKey(key: Long, nextState: Boolean): Boolean {
        val wrongKey: Long = -1
        return key != wrongKey
    }

    override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean = true

    override fun canSelectMultiple(): Boolean = true
}