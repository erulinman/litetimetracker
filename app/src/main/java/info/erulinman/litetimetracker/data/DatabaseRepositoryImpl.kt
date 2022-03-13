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

    override fun loadCategories() = database.categoryDao().getCategoriesStream()

    override fun loadCategoryById(id: Long) = database.categoryDao().getCategoryById(id)

    override fun loadPresets(categoryId: Long) =
        database.presetDao().getPresetsStream(categoryId)

    override fun getMaxCategoryId() = database.categoryDao().getMaxCategoryId()

    override fun getMaxPresetId() = database.presetDao().getMaxPresetId()

    override suspend fun insertCategory(category: Category) =
        database.categoryDao().insert(category)

    override suspend fun insertPreset(preset: Preset) =
        database.presetDao().insert(preset)

    override suspend fun deleteCategory(category: Category) {
        val oldList = database.categoryDao().getCategoriesSync() ?: return
        val newList = mutableListOf<Category>()
        var correctPosition = 0
        oldList.forEach {
            if (it == category) return@forEach
            val newItem = Category(it.id, correctPosition, it.name)
            newList.add(newItem)
            correctPosition++
        }
        database.categoryDao().delete(category, newList)
        database.presetDao().deleteByCategoryId(category.id)
    }

    override suspend fun deletePreset(preset: Preset) {
        val oldList = database.presetDao().getPresetsSync(preset.categoryId) ?: return
        val newList = mutableListOf<Preset>()
        var correctPosition = 0
        oldList.forEach {
            if (it == preset) return@forEach
            val newItem = Preset(it.id, it.categoryId, correctPosition, it.name, it.time)
            newList.add(newItem)
            correctPosition++
        }
        database.presetDao().delete(preset, newList)
    }

    override suspend fun updatePreset(preset: Preset) =
        database.presetDao().update(preset)

    override suspend fun updateCategory(category: Category) =
        database.categoryDao().update(category)
}