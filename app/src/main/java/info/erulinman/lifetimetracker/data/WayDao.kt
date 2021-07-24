package info.erulinman.lifetimetracker.data

import androidx.room.*

import kotlinx.coroutines.flow.Flow

@Dao
interface WayDao {
    @Query("SELECT * FROM ways ORDER BY name")
    fun getAll(): Flow<List<Way>>

    @Query("SELECT MAX(id) FROM ways")
    fun getMaxWayId(): Long?

    @Query("SELECT * FROM ways WHERE id = :id")
    fun getWay(id: Long): Way

    @Delete
    suspend fun delete(ways: List<Way>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(way: Way)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ways: List<Way>)
}