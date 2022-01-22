package info.erulinman.litetimetracker.presets

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.erulinman.litetimetracker.data.DatabaseRepository
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.data.entity.Preset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PresetListViewModel(
    private val repository: DatabaseRepository,
    private val categoryId: Long
) : ViewModel() {

    val presets = repository.loadPresets(categoryId)

    val category = repository.loadCategoryById(categoryId)

    val hasSelection = MutableLiveData(false)

    fun MutableLiveData<Boolean>.refresh() {
        this.value = this.value
    }

    fun addNewPreset(presetName: String, presetTime: Long) = viewModelScope.launch(Dispatchers.IO) {
        val newId = repository.getMaxPresetId()?.let { it + 1 } ?: 1
        val newPreset = Preset(
            id = newId,
            categoryId = categoryId,
            name = presetName,
            time = presetTime
        )
        repository.insertPreset(newPreset)
    }

    fun deleteSelectedPresets(idList: List<Long>) = viewModelScope.launch(Dispatchers.IO) {
        val listForDelete = mutableListOf<Preset>()
        presets.value?.forEach {
            if (idList.contains(it.id))
                listForDelete.add(it)
        }
        repository.deletePresets(listForDelete.toList())
    }

    fun updatePreset(preset: Preset) = viewModelScope.launch(Dispatchers.IO) {
        repository.updatePreset(preset)
    }

    fun updateCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateCategory(category)
    }
}
