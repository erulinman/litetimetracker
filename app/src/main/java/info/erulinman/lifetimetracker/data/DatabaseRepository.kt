package info.erulinman.lifetimetracker.data

import info.erulinman.lifetimetracker.data.database.AppDatabase
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.data.entity.Way

class DatabaseRepository(private val database: AppDatabase) {
    fun loadWays() = database.wayDao().getWayList()

    fun loadPresets(wayId: Long) =
        database.presetDao().getPresetForWayId(wayId)

    fun getMaxWayId() = database.wayDao().getMaxWayId()

    suspend fun insertWays(way: Way) =
        database.wayDao().insert(way)

    suspend fun insertPresets(preset: Preset) =
        database.presetDao().insert(preset)

    suspend fun deleteWays(ways: List<Way>) =
        database.wayDao().delete(ways)

    suspend fun deletePresets(presets: List<Preset>) =
        database.presetDao().delete(presets)
}