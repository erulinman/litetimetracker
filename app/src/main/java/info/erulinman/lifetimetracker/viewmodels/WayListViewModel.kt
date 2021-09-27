package info.erulinman.lifetimetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import info.erulinman.lifetimetracker.data.DatabaseRepository

import info.erulinman.lifetimetracker.data.entity.Way

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WayListViewModel(private val repository: DatabaseRepository) : ViewModel() {
    val liveDataWays = repository.loadWays().asLiveData()

    fun addNewWay(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newId = repository.getMaxWayId()?.let { it + 1 } ?: 1
            repository.insertWays(Way(newId, name))
        }
    }

    fun deleteSelectedWays(idList: List<Long>) = viewModelScope.launch {
        val listForDelete = mutableListOf<Way>()
        liveDataWays.value?.forEach {
            if (idList.contains(it.id))
                listForDelete.add(it)
        }
        repository.deleteWays(listForDelete.toList())
    }
}