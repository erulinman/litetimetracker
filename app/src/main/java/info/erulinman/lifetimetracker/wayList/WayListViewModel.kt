package info.erulinman.lifetimetracker.wayList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import info.erulinman.lifetimetracker.data.Database
import info.erulinman.lifetimetracker.data.Way

import kotlin.random.Random

class WayListViewModel(private val database: Database) : ViewModel() {
    val wayLiveData = database.getWayList()

    fun addNewWay(name: String?) {
        name?.let {
            val newWay = Way(Random.nextLong(), it)
            database.add(newWay)
        }
    }

    fun deleteSelectedWays(idList: List<Long>) {
        val listForDelete = mutableListOf<Way>()
        wayLiveData.value?.forEach {
            if (idList.contains(it.id))
                listForDelete.add(it)
        }
        database.delete(listForDelete)
    }
}

class WayListViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WayListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WayListViewModel(Database.getDataSource()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}