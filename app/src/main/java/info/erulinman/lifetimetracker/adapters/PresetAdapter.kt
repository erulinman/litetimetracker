package info.erulinman.lifetimetracker.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.databinding.ListItemPresetBinding
import info.erulinman.lifetimetracker.ui.fromLongToTimerString
import info.erulinman.lifetimetracker.utilities.Constants.DEBUG_TAG

class PresetAdapter(private val onClick: () -> Unit) :
    ListAdapter<Preset, PresetAdapter.PresetViewHolder>(PresetDiffCallback) {
    private var tracker: SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val view = ListItemPresetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PresetViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        tracker?.let {
            val item = getItem(position)
            return holder.bind(item, it.isSelected(item.id))
        }
    }

    fun setTracker(tracker: SelectionTracker<Long>?) {
        this.tracker = tracker
    }

    inner class PresetViewHolder(
        private val binding: ListItemPresetBinding,
        val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                Log.d(DEBUG_TAG, "on click")
                onClick()
            }
        }

        fun bind(preset: Preset, isSelected: Boolean = false) {
            binding.apply {
                presetName.text = preset.name
                presetTime.text = preset.time.fromLongToTimerString()
                tickPointImage.isVisible = isSelected
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int {
                    Log.d(DEBUG_TAG, bindingAdapterPosition.toString())
                    return bindingAdapterPosition
                }

                override fun getSelectionKey(): Long? =
                    (getItem(position) as Preset).id

            }
    }
}

object PresetDiffCallback: DiffUtil.ItemCallback<Preset>() {
    override fun areItemsTheSame(oldItem: Preset, newItem: Preset): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Preset, newItem: Preset): Boolean =
        oldItem.id == newItem.id

}