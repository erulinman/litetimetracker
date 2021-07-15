package info.erulinman.lifetimetracker.data

import androidx.room.*

@Dao
interface WayDao {
    @Query("SELECT * FROM ways ORDER BY name")
    fun getAll(): List<Way>

    @Query("SELECT * FROM ways WHERE wayId = :wayId")
    fun getWay(wayId: Long): Way

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ways: List<Way>)
}