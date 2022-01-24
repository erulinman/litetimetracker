package info.erulinman.litetimetracker.features.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.erulinman.litetimetracker.data.DatabaseRepository
import info.erulinman.litetimetracker.data.entity.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryListViewModel(private val repository: DatabaseRepository) : ViewModel() {

    val categories = repository.loadCategories()

    fun addNewCategory(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val newId = repository.getMaxCategoryId()?.let { it + 1 } ?: 1
        repository.insertCategory(Category(newId, name))
    }

    fun deleteCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteCategory(category)
    }
}