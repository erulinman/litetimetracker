package info.erulinman.lifetimetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

import info.erulinman.lifetimetracker.data.PresetRepository

class PomodoroPresetViewModel(
    private val presetRepository: PresetRepository
) : ViewModel() {
    val liveDataPomodoroPresets = presetRepository.getAll().asLiveData()
}
