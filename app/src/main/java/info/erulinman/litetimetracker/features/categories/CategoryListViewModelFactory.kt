package info.erulinman.litetimetracker.features.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.erulinman.litetimetracker.data.DatabaseRepository
import javax.inject.Inject

class CategoryListViewModelFactory @Inject constructor(
    private val repository: DatabaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        require(modelClass == CategoryListViewModel::class.java)
        return CategoryListViewModel(repository) as T
    }
}