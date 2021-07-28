package info.erulinman.lifetimetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.erulinman.lifetimetracker.data.DatabaseRepository

class PresetViewModelFactory(
    private val repository: DatabaseRepository,
    private val wayId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (wayId == WRONG_ID) throw IllegalArgumentException("Wrong way id")
        if (modelClass.isAssignableFrom(PresetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PresetViewModel(repository, wayId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        const val WRONG_ID: Long = -1
    }
}