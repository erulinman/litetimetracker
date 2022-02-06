package info.erulinman.litetimetracker.features.presets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.litetimetracker.databinding.RvItemAddButtonBinding

class AddButtonAdapter(
    private val onClick: () -> Unit
) : RecyclerView.Adapter<AddButtonAdapter.AddButtonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddButtonViewHolder {
        val binding = RvItemAddButtonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddButtonViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: AddButtonViewHolder, position: Int) {
        // nothing to do because this view has button`s function
    }

    override fun getItemCount(): Int = 1

    class AddButtonViewHolder(
        binding: RvItemAddButtonBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener { onClick() }
        }
    }
}