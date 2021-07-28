package info.erulinman.lifetimetracker.data.dao

import androidx.room.*
import info.erulinman.lifetimetracker.data.entity.Way

import kotlinx.coroutines.flow.Flow

@Dao
interface WayDao {
    @Query("SELECT * FROM ways")
    fun getWayList(): Flow<List<Way>>

    @Query("SELECT MAX(id) FROM ways")
    fun getMaxWayId(): Long?

    /*@Query("SELECT * FROM ways WHERE id = :id")
    fun getWay(id: Long): Way*/

    @Delete
    suspend fun delete(ways: List<Way>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(way: Way)

    // Prepopulate database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ways: List<Way>)
}