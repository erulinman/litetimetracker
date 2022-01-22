package info.erulinman.litetimetracker.utils

import androidx.recyclerview.widget.DiffUtil
import info.erulinman.litetimetracker.data.entity.Identifiable

class DiffUtilCallback<T: Identifiable> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem.equals(newItem)
}