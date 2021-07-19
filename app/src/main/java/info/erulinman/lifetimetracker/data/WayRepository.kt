package info.erulinman.lifetimetracker.data;

import kotlinx.coroutines.flow.Flow

class WayRepository(private val wayDao: WayDao) {
    fun getAll(): Flow<List<Way>> = wayDao.getAll()

    fun getMaxWayId() : Long? = wayDao.getMaxWayId()

    suspend fun delete(ways: List<Way>) = wayDao.delete(ways)

    suspend fun insert(way: Way) = wayDao.insert(way)
}
