package info.erulinman.lifetimetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.erulinman.lifetimetracker.data.PresetRepository

class PresetViewModelFactory(
    private val repository: PresetRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PresetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PresetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}