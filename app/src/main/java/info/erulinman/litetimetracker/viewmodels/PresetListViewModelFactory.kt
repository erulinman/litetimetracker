package info.erulinman.litetimetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import info.erulinman.litetimetracker.data.DatabaseRepository

class PresetListViewModelFactory @AssistedInject constructor(
    private val repository: DatabaseRepository,
    @Assisted("categoryId") private val categoryId: Long
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (categoryId == WRONG_ID) throw IllegalArgumentException("Wrong category id")
        require(modelClass == PresetListViewModel::class.java)
        return PresetListViewModel(repository, categoryId) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("categoryId") categoryId: Long): PresetListViewModelFactory
    }

    companion object {
        const val WRONG_ID: Long = -1
    }
}