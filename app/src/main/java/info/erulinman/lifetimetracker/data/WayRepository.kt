package info.erulinman.lifetimetracker.data;

class WayRepository(private val wayDao: WayDao) {
    fun getAll() = wayDao.getAll()

    fun getMaxWayId() : Long? = wayDao.getMaxWayId()

    suspend fun delete(ways: List<Way>) = wayDao.delete(ways)

    suspend fun insert(way: Way) = wayDao.insert(way)
}
