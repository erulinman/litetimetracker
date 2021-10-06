package info.erulinman.lifetimetracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.lifetimetracker.databinding.RvItemAddPresetBinding

class AddPresetAdapter(
    private val onClick: () -> Unit
) : RecyclerView.Adapter<AddPresetAdapter.AddPresetViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddPresetViewHolder {
        val binding = RvItemAddPresetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddPresetViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: AddPresetViewHolder, position: Int) {
        // nothing to do cause this view has button`s function
    }

    override fun getItemCount(): Int = 1

    class AddPresetViewHolder(
        binding: RvItemAddPresetBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.addNewPresetButton.setOnClickListener {
                onClick()
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int =
                    bindingAdapterPosition

                override fun getSelectionKey(): Long = -1
            }
    }
}