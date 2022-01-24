package info.erulinman.litetimetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import info.erulinman.litetimetracker.data.entity.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getCategoryList(): LiveData<List<Category>>

    @Query("SELECT * FROM categories where id = :id")
    fun getCategoryById(id: Long): LiveData<Category>

    @Query("SELECT MAX(id) FROM categories")
    fun getMaxCategoryId(): Long?

    @Delete
    suspend fun deleteCategory(category: Category)

    @Update
    suspend fun update(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    // Prepopulate database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)
}