package info.erulinman.litetimetracker.data

import info.erulinman.litetimetracker.data.database.AppDatabase
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.data.entity.Preset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : DatabaseRepository {

    override fun loadCategories() = database.categoryDao().getCategoryList()

    override fun loadCategoryById(id: Long) = database.categoryDao().getCategoryById(id)

    override fun loadPresets(categoryId: Long) =
        database.presetDao().getPresetForCategoryId(categoryId)

    override fun getMaxCategoryId() = database.categoryDao().getMaxCategoryId()

    override fun getMaxPresetId() = database.presetDao().getMaxPresetId()

    override suspend fun insertCategory(category: Category) =
        database.categoryDao().insert(category)

    override suspend fun insertPreset(preset: Preset) =
        database.presetDao().insert(preset)

    override suspend fun deleteCategory(category: Category) {
        database.categoryDao().deleteCategory(category)
        database.presetDao().deleteByCategoryId(category.id)
    }

    override suspend fun deletePreset(preset: Preset) =
        database.presetDao().delete(preset)

    override suspend fun updatePreset(preset: Preset) =
        database.presetDao().update(preset)

    override suspend fun updateCategory(category: Category) =
        database.categoryDao().update(category)
}