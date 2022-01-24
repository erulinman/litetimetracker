package info.erulinman.litetimetracker.data

import androidx.lifecycle.LiveData
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.data.entity.Preset

interface DatabaseRepository {

    fun loadCategories(): LiveData<List<Category>>

    fun loadCategoryById(id: Long): LiveData<Category>

    fun loadPresets(categoryId: Long): LiveData<List<Preset>>

    fun getMaxCategoryId(): Long?

    fun getMaxPresetId(): Long?

    suspend fun insertCategory(category: Category)

    suspend fun insertPreset(preset: Preset)

    suspend fun deleteCategory(category: Category)

    suspend fun deletePreset(preset: Preset)

    suspend fun updatePreset(preset: Preset)

    suspend fun updateCategory(category: Category)
}