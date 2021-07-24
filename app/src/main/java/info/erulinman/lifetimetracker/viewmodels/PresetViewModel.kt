package info.erulinman.lifetimetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import info.erulinman.lifetimetracker.data.Preset

import info.erulinman.lifetimetracker.data.PresetRepository
import info.erulinman.lifetimetracker.data.Way
import kotlinx.coroutines.launch

class PresetViewModel(
    private val presetRepository: PresetRepository
) : ViewModel() {
    val liveDataPomodoroPresets = presetRepository.getAll().asLiveData()

    fun deleteSelectedPresets(idList: List<Long>) = viewModelScope.launch {
        val listForDelete = mutableListOf<Preset>()
        liveDataPomodoroPresets.value?.forEach {
            if (idList.contains(it.id))
                listForDelete.add(it)
        }
        presetRepository.delete(listForDelete.toList())
    }
}
