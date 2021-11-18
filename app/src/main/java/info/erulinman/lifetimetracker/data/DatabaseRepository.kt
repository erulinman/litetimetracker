package info.erulinman.lifetimetracker.data

import info.erulinman.lifetimetracker.data.database.AppDatabase
import info.erulinman.lifetimetracker.data.entity.Category
import info.erulinman.lifetimetracker.data.entity.Preset

class DatabaseRepository(private val database: AppDatabase) {
    fun loadCategories() = database.categoryDao().getCategoryList()

    fun loadCategoryById(id: Long) = database.categoryDao().getCategoryById(id)

    fun loadPresets(categoryId: Long) =
        database.presetDao().getPresetForCategoryId(categoryId)

    fun getMaxCategoryId() = database.categoryDao().getMaxCategoryId()

    fun getMaxPresetId() = database.presetDao().getMaxPresetId()

    suspend fun insertCategory(category: Category) =
        database.categoryDao().insert(category)

    suspend fun insertPreset(preset: Preset) =
        database.presetDao().insert(preset)

    suspend fun deleteCategories(categoriesId: List<Long>, categories: List<Category>) {
        database.categoryDao().delete(categories)
        database.presetDao().deleteByCategoriesId(categoriesId)
    }


    suspend fun deletePresets(presets: List<Preset>) =
        database.presetDao().delete(presets)

    suspend fun updatePreset(preset: Preset) =
        database.presetDao().update(preset)

    suspend fun updateCategory(category: Category) =
        database.categoryDao().update(category)
}