package info.erulinman.lifetimetracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import info.erulinman.lifetimetracker.data.Preset

import info.erulinman.lifetimetracker.data.Way
import info.erulinman.lifetimetracker.data.WayRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WayListViewModel(private val wayRepository: WayRepository) : ViewModel() {
    val liveDataWays = wayRepository.getAll().asLiveData()

    fun deleteSelectedWays(idList: List<Long>) = viewModelScope.launch {
        val listForDelete = mutableListOf<Way>()
        liveDataWays.value?.forEach {
            if (idList.contains(it.id))
                listForDelete.add(it)
        }
        wayRepository.delete(listForDelete.toList())
    }

    fun insertNewWay(name: String, description: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val newId = wayRepository.getMaxWayId()?.let { it + 1 } ?: 1
            wayRepository.insert(Way(newId, name, description))
        }
    }
}