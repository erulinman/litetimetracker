package info.erulinman.lifetimetracker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DataSource() {
    private val initialWayList = wayList()
    private val wayLiveData = MutableLiveData(initialWayList)

    fun addWay(way: Way) {
        val currentList = wayLiveData.value
        if (currentList == null) {
            wayLiveData.postValue(listOf(way))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(way)
            wayLiveData.postValue(updatedList)
        }
    }

    fun removeWay(way: Way) {
        val currentList = wayLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(way)
            wayLiveData.postValue(updatedList)
        }
    }

    fun getWayForId(id: Long): Way? {
        wayLiveData.value?.let { ways ->
            return ways.firstOrNull { it.id == id }
        }
        return null
    }

    fun getWayList(): LiveData<List<Way>> {
        return wayLiveData
    }

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}