package info.erulinman.lifetimetracker.wayList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import info.erulinman.lifetimetracker.data.AppDatabase
import info.erulinman.lifetimetracker.data.Way

import kotlin.random.Random

class WayListViewModel(private val appDatabase: AppDatabase) : ViewModel() {
    val wayLiveData = appDatabase.getWayList()

    fun addNewWay(name: String?) {
        name?.let {
            val newWay = Way(Random.nextLong(), it)
            appDatabase.add(newWay)
        }
    }

    fun deleteSelectedWays(idList: List<Long>) {
        val listForDelete = mutableListOf<Way>()
        wayLiveData.value?.forEach {
            if (idList.contains(it.id))
                listForDelete.add(it)
        }
        appDatabase.delete(listForDelete)
    }
}

class WayListViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WayListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WayListViewModel(AppDatabase.getDataSource()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}