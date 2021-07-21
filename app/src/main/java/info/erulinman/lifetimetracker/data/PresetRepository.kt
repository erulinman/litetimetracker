package info.erulinman.lifetimetracker.data

class PresetRepository(private val presetDao: PresetDao) {
    fun getAll() = presetDao.getAll()

    suspend fun insert(preset: Preset) =
        presetDao.insert(preset)

    suspend fun delete(presets: List<Preset>) =
        presetDao.delete(presets)
}