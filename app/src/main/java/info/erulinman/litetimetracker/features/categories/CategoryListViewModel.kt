package info.erulinman.litetimetracker.features.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.erulinman.litetimetracker.data.DatabaseRepository
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.data.entity.Preset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryListViewModel(private val repository: DatabaseRepository) : ViewModel() {

    val categories = repository.loadCategories()

    fun addNewCategory(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val newId = repository.getMaxCategoryId()?.let { it + 1 } ?: 1
        val newPosition = categories.value?.size ?: 0
        repository.insertCategory(Category(newId, newPosition, name))
    }

    fun deleteCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteCategory(category)
    }

    fun onChangePositions(from: Int, to: Int) = viewModelScope.launch(Dispatchers.IO) {
        if (from == to) return@launch
        val currentCategories = categories.value!!
        val updatedCategories = mutableListOf<Category>().apply {
            add(currentCategories[from].copy(position = to))
        }
        if (from < to) {
            for (i in (from + 1)..to) {
                val preset = currentCategories[i]
                updatedCategories.add(preset.copy(position = preset.position - 1))
            }
        } else {
            for (i in (to until from)) {
                val preset = currentCategories[i]
                updatedCategories.add(preset.copy(position = preset.position + 1))
            }
        }
        repository.updateCategories(updatedCategories)
    }
}