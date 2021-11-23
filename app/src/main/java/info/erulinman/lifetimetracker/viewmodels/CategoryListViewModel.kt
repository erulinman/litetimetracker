package info.erulinman.lifetimetracker.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.erulinman.lifetimetracker.data.DatabaseRepository
import info.erulinman.lifetimetracker.data.entity.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CategoryListViewModel(private val repository: DatabaseRepository) : ViewModel() {
    val categories = repository.loadCategories()
    val hasSelection = MutableLiveData(false)

    fun addNewCategory(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newId = repository.getMaxCategoryId()?.let { it + 1 } ?: 1
            repository.insertCategory(Category(newId, name))
        }
    }

    fun deleteSelectedCategories(idList: List<Long>) = viewModelScope.launch {
        val listForDelete = mutableListOf<Category>()
        categories.value?.forEach {
            if (idList.contains(it.id))
                listForDelete.add(it)
        }
        repository.deleteCategories(idList, listForDelete.toList())
    }
}