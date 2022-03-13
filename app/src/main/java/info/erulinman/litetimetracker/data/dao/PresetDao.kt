package info.erulinman.litetimetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import info.erulinman.litetimetracker.data.entity.Preset


@Dao
interface PresetDao {
    @Query("SELECT * FROM presets WHERE categoryId = :categoryId ORDER BY id")
    fun getPresetsStream(categoryId: Long): LiveData<List<Preset>>

    @Query("SELECT * FROM presets WHERE categoryId = :categoryId ORDER BY id")
    suspend fun getPresetsSync(categoryId: Long): List<Preset>?

    @Query("SELECT MAX(id) FROM presets")
    fun getMaxPresetId(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: Preset)

    @Delete
    suspend fun delete(preset: Preset)

    @Update
    suspend fun update(preset: Preset)

    @Update
    suspend fun update(presets: List<Preset>)

    // for prepopulate on create db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(presets: List<Preset>)

    @Query("DELETE FROM presets WHERE categoryId = :id")
    suspend fun deleteByCategoryId(id: Long)

    @Transaction
    suspend fun delete(preset: Preset, presets: List<Preset>) {
        delete(preset)
        update(presets)
    }
}