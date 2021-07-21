package info.erulinman.lifetimetracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import info.erulinman.lifetimetracker.data.Preset
import info.erulinman.lifetimetracker.databinding.ListItemPresetBinding

class PomodoroPresetAdapter(private val onClick: () -> Unit) :
    ListAdapter<Preset, PomodoroPresetAdapter.PresetViewHolder>(PresetDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val view = ListItemPresetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PresetViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        val view = getItem(position)
        return holder.bind(view)
    }
        

    class PresetViewHolder(
        private val binding: ListItemPresetBinding,
        val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onClick()
            }
        }

        fun bind(preset: Preset) {
            binding.presetName.text = preset.name
            binding.presetTime.text = preset.time
        }

    }
}

object PresetDiffCallback: DiffUtil.ItemCallback<Preset>() {
    override fun areItemsTheSame(oldItem: Preset, newItem: Preset): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Preset, newItem: Preset): Boolean =
        oldItem.id == newItem.id

}