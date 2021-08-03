package info.erulinman.lifetimetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import info.erulinman.lifetimetracker.data.DatabaseRepository
import info.erulinman.lifetimetracker.data.entity.Preset
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch

class PresetViewModel(
    private val repository: DatabaseRepository,
    private val wayId: Long
) : ViewModel() {
    val liveDataPresets = repository.loadPresets(wayId).asLiveData()

    fun addNewPreset(name: String, time: String) = viewModelScope.launch(Dispatchers.IO) {
        val newId = repository.getMaxPresetId()?.let { it + 1 } ?: 1
        val time = time.toLong()
        val newPreset = Preset(
            id = newId,
            wayId = wayId,
            name,
            time
        )
        repository.insertPresets(newPreset)
    }

    fun deleteSelectedPresets(idList: List<Long>) = viewModelScope.launch {
        val listForDelete = mutableListOf<Preset>()
        liveDataPresets.value?.forEach {
            if (idList.contains(it.id))
                listForDelete.add(it)
        }
        repository.deletePresets(listForDelete.toList())
    }
}