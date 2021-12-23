package info.erulinman.litetimetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import info.erulinman.litetimetracker.data.entity.Preset


@Dao
interface PresetDao {
    @Query("SELECT * FROM presets WHERE categoryId = :categoryId ORDER BY id")
    fun getPresetForCategoryId(categoryId: Long): LiveData<List<Preset>>

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

    @Query("DELETE FROM presets WHERE categoryId in (:categoriesId)")
    suspend fun deleteByCategoriesId(categoriesId: List<Long>)
}