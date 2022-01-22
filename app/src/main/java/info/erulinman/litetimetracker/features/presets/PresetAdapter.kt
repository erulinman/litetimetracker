package info.erulinman.litetimetracker.features.presets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.litetimetracker.data.entity.Preset
import info.erulinman.litetimetracker.databinding.RvItemPresetBinding
import info.erulinman.litetimetracker.utils.DiffUtilCallback
import info.erulinman.litetimetracker.utils.toListHHMMSS
import info.erulinman.litetimetracker.utils.toStringHHMMSS

class PresetAdapter(private val onClick: (Preset) -> Unit) :
    ListAdapter<Preset, PresetAdapter.PresetViewHolder>(DiffUtilCallback<Preset>()) {

    private var tracker: SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val binding = RvItemPresetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PresetViewHolder(binding)
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

    inner class PresetViewHolder(private val binding: RvItemPresetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(preset: Preset, isSelected: Boolean = false) {
            binding.apply {
                itemView.setOnClickListener { onClick(preset) }
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