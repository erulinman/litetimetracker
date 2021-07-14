package info.erulinman.lifetimetracker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Database() {
    private val initialWayList = wayList()
    private val wayLiveData = MutableLiveData(initialWayList)

    fun add(way: Way) {
        val currentList = wayLiveData.value
        if (currentList == null) {
            wayLiveData.postValue(listOf(way))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(way)
            wayLiveData.postValue(updatedList)
        }
    }

    fun delete(ways: List<Way>) {
        val currentList = wayLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.removeAll(ways)
            wayLiveData.postValue(updatedList)
        }
    }

    fun delete(way: Way) {
        TODO()
    }

    fun getWayForId(id: Long): Way? {
        wayLiveData.value?.let { ways ->
            return ways.firstOrNull { it.id == id }
        }
        return null
    }

    fun getWayList(): LiveData<List<Way>> = wayLiveData

    companion object {
        private var INSTANCE: Database? = null

        fun getDataSource(): Database =
            synchronized(Database::class) {
                val newInstance = INSTANCE ?: Database()
                INSTANCE = newInstance
                newInstance
            }
    }
}