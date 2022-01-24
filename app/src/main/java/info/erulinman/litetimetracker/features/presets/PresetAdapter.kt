package info.erulinman.litetimetracker.features.presets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.litetimetracker.data.entity.Preset
import info.erulinman.litetimetracker.databinding.RvItemPresetBinding
import info.erulinman.litetimetracker.utils.DiffUtilCallback
import info.erulinman.litetimetracker.utils.ItemTouchCallback
import info.erulinman.litetimetracker.utils.toListHHMMSS
import info.erulinman.litetimetracker.utils.toStringHHMMSS

class PresetAdapter(
    private val viewModel: PresetListViewModel,
    private val onClick: (Preset) -> Unit
) : ListAdapter<Preset, PresetAdapter.PresetViewHolder>(DiffUtilCallback<Preset>()),
    ItemTouchCallback.Adapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val binding = RvItemPresetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PresetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        holder.bind(currentList[position], onClick)
    }

    class PresetViewHolder(private val binding: RvItemPresetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(preset: Preset, onClick: (Preset) -> Unit) {
            itemView.setOnClickListener { onClick(preset) }
            binding.presetName.text = preset.name
            binding.presetTime.text = preset.time.toListHHMMSS().toStringHHMMSS()
        }
    }

    override fun onItemSwipe(position: Int) {
        val preset = currentList[position]
        viewModel.deletePreset(preset)
    }
}