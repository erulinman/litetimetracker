package info.erulinman.lifetimetracker.data.dao

import androidx.room.*
import info.erulinman.lifetimetracker.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getCategoryList(): Flow<List<Category>>

    @Query("SELECT MAX(id) FROM categories")
    fun getMaxCategoryId(): Long?

    @Delete
    suspend fun delete(categories: List<Category>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    // Prepopulate database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)
}