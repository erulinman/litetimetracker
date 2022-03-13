package info.erulinman.litetimetracker.features.presets

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

    fun addNewPreset(presetName: String, presetTime: Long) = viewModelScope.launch(Dispatchers.IO) {
        val newId = repository.getMaxPresetId()?.let { it + 1 } ?: 1
        val newPosition = presets.value?.size ?: 0
        val newPreset = Preset(
            id = newId,
            categoryId = categoryId,
            position = newPosition,
            name = presetName,
            time = presetTime
        )
        repository.insertPreset(newPreset)
    }

    fun deletePreset(preset: Preset) = viewModelScope.launch(Dispatchers.IO) {
        repository.deletePreset(preset)
    }

    fun updatePreset(preset: Preset) = viewModelScope.launch(Dispatchers.IO) {
        repository.updatePreset(preset)
    }

    fun updateCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateCategory(category)
    }
}
