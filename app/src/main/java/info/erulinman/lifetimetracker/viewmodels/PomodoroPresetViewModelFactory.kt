package info.erulinman.lifetimetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.erulinman.lifetimetracker.data.PresetRepository

class PomodoroPresetViewModelFactory(
    private val repository: PresetRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PomodoroPresetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PomodoroPresetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}