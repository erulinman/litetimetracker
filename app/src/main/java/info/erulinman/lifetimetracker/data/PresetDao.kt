package info.erulinman.lifetimetracker.data

import androidx.room.*

import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("SELECT * FROM presets ORDER BY id")
    fun getAll(): Flow<List<Preset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: Preset)

    @Delete
    suspend fun delete(presets: List<Preset>)

    // for prepopulate on create db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(presets: List<Preset>)
}