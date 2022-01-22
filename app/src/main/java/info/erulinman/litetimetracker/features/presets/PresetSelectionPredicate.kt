package info.erulinman.litetimetracker.features.presets

import androidx.recyclerview.selection.SelectionTracker
import info.erulinman.litetimetracker.utils.UNSELECTED

class PresetSelectionPredicate : SelectionTracker.SelectionPredicate<Long>() {

    override fun canSetStateForKey(key: Long, nextState: Boolean) =
        key != UNSELECTED

    override fun canSetStateAtPosition(position: Int, nextState: Boolean) = true

    override fun canSelectMultiple() = true
}