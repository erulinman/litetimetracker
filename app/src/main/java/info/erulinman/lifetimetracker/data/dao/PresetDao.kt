package info.erulinman.lifetimetracker.data.dao

import androidx.room.*

import info.erulinman.lifetimetracker.data.entity.Preset

import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("SELECT * FROM presets WHERE categoryId = :categoryId ORDER BY id")
    fun getPresetForCategoryId(categoryId: Long): Flow<List<Preset>>

    @Query("SELECT MAX(id) FROM presets")
    fun getMaxPresetId(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: Preset)

    @Delete
    suspend fun delete(presets: List<Preset>)

    @Update
    suspend fun update(preset: Preset)

    // for prepopulate on create db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(presets: List<Preset>)
}