package info.erulinman.lifetimetracker.data

import androidx.lifecycle.LiveData
import info.erulinman.lifetimetracker.data.database.AppDatabase
import info.erulinman.lifetimetracker.data.entity.Category
import info.erulinman.lifetimetracker.data.entity.Preset
import javax.inject.Inject

interface DatabaseRepository {

    fun loadCategories(): LiveData<List<Category>>

    fun loadCategoryById(id: Long): LiveData<Category>

    fun loadPresets(categoryId: Long): LiveData<List<Preset>>

    fun getMaxCategoryId(): Long?

    fun getMaxPresetId(): Long?

    suspend fun insertCategory(category: Category)

    suspend fun insertPreset(preset: Preset)

    suspend fun deleteCategories(
        categoriesId: List<Long>,
        categories: List<Category>
    )

    suspend fun deletePresets(presets: List<Preset>)

    suspend fun updatePreset(preset: Preset)

    suspend fun updateCategory(category: Category)
}