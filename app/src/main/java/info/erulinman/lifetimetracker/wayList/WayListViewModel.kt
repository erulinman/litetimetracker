package info.erulinman.lifetimetracker.wayList

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.erulinman.lifetimetracker.data.DataSource
import info.erulinman.lifetimetracker.data.Way
import kotlin.random.Random

class WayListViewModel(private val dataSource: DataSource) : ViewModel() {
    val wayLiveData = dataSource.getWayList()

    fun insertWay(name: String?) {
        if (name == null) return
        val newWay = Way(Random.nextLong(), name)
        dataSource.addWay(newWay)
    }
}

class WayListViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WayListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WayListViewModel(DataSource.getDataSource()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}