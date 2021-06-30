package info.erulinman.lifetimetracker.wayList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.erulinman.lifetimetracker.R

class AddAdapter(private val onClick: () -> Unit) :
    RecyclerView.Adapter<AddAdapter.AddViewHolder>() {
    private val textForAddItem = "Add (+)"
    class AddViewHolder(view: View, val onClick: () -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val addWayTextView: TextView = itemView.findViewById(R.id.add_item_text)

        init {
            itemView.setOnClickListener {
                onClick()
            }
        }

        fun bind(text: String) {
            addWayTextView.text = text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_item, parent, false)
        return AddViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: AddViewHolder, position: Int) {
        holder.bind(textForAddItem)
    }

    override fun getItemCount(): Int {
        return 1
    }

}