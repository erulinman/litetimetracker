package info.erulinman.lifetimetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.erulinman.lifetimetracker.data.DatabaseRepository

class PresetListViewModelFactory(
    private val repository: DatabaseRepository,
    private val categoryId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (categoryId == WRONG_ID) throw IllegalArgumentException("Wrong category id")
        if (modelClass.isAssignableFrom(PresetListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PresetListViewModel(repository, categoryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        const val WRONG_ID: Long = -1
    }
}