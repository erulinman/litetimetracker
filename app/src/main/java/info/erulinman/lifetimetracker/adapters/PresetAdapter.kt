package info.erulinman.lifetimetracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.databinding.RvItemPresetBinding
import info.erulinman.lifetimetracker.utilities.toListHHMMSS
import info.erulinman.lifetimetracker.utilities.toStringHHMMSS

class PresetAdapter(private val onClick: (Preset) -> Unit) :
    ListAdapter<Preset, PresetAdapter.PresetViewHolder>(PresetDiffCallback) {
    private var tracker: SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val view = RvItemPresetBinding.inflate(
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
        private val binding: RvItemPresetBinding,
        val onClick: (Preset) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var thisPreset: Preset? = null

        init {
            itemView.setOnClickListener {
                thisPreset?.let {
                    onClick(it)
                }
            }
        }

        fun bind(preset: Preset, isSelected: Boolean = false) {
            thisPreset = preset
            binding.apply {
                presetName.text = preset.name
                presetTime.text = preset.time.toListHHMMSS().toStringHHMMSS()
                tickPointImage.isVisible = isSelected
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {

                override fun getPosition(): Int {
                    return bindingAdapterPosition
                }

                override fun getSelectionKey(): Long =
                    (getItem(position) as Preset).id

            }
    }
}

object PresetDiffCallback: DiffUtil.ItemCallback<Preset>() {
    override fun areItemsTheSame(oldItem: Preset, newItem: Preset): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Preset, newItem: Preset): Boolean =
        oldItem == newItem
}