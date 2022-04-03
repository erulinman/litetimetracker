package info.erulinman.litetimetracker.features.presets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.litetimetracker.data.entity.Preset
import info.erulinman.litetimetracker.databinding.RvItemPresetBinding
import info.erulinman.litetimetracker.utils.ItemTouchCallback
import info.erulinman.litetimetracker.utils.toListHHMMSS
import info.erulinman.litetimetracker.utils.toStringHHMMSS

class PresetAdapter(
    private val viewModel: PresetListViewModel,
    private val onClick: (Preset) -> Unit
) : RecyclerView.Adapter<PresetAdapter.PresetViewHolder>(),
    ItemTouchCallback.Adapter {

    private var items = listOf<Preset>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val binding = RvItemPresetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PresetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        holder.bind(items[position], onClick)
    }

    override fun onItemSwipe(position: Int) {
        val preset = items[position]
        viewModel.deletePreset(preset)
    }

    override fun onItemMove(from: Int, to: Int) {
        notifyItemMoved(from, to)
    }

    override fun onClearView(targetViewHolder: RecyclerView.ViewHolder) {
        val order = (targetViewHolder as PresetViewHolder).order!!
        val currentPosition = targetViewHolder.absoluteAdapterPosition
        if (order != currentPosition && currentPosition != RecyclerView.NO_POSITION)
            viewModel.onChangePositions(order, currentPosition)
    }

    override fun getItemCount() = items.size

    fun submitList(list: List<Preset>) {
        items = list
        notifyDataSetChanged()
    }

    class PresetViewHolder(private val binding: RvItemPresetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var order: Int? = null

        fun bind(preset: Preset, onClick: (Preset) -> Unit) {
            order = preset.position
            itemView.setOnClickListener { onClick(preset) }
            binding.presetName.text = preset.name
            binding.presetTime.text = preset.time.toListHHMMSS().toStringHHMMSS()
        }
    }
}