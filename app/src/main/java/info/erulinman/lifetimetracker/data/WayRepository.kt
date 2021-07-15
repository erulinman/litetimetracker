package info.erulinman.lifetimetracker.data;


class WayRepository(private val wayDao: WayDao) {
    fun getAll() = wayDao.getAll()

    fun getWay(wayId: Long) = wayDao.getWay(wayId)
}
