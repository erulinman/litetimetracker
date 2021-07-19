package info.erulinman.lifetimetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import info.erulinman.lifetimetracker.data.WayRepository

class WayListViewModelFactory(private val repository: WayRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WayListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WayListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}