package info.erulinman.lifetimetracker.data.dao

import androidx.room.*

import info.erulinman.lifetimetracker.data.entity.Preset

import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("SELECT * FROM pomodoro_presets WHERE wayId = :wayId ORDER BY id")
    fun getPresetForWayId(wayId: Long): Flow<List<Preset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: Preset)

    @Delete
    suspend fun delete(presets: List<Preset>)

    // for prepopulate on create db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(presets: List<Preset>)
}