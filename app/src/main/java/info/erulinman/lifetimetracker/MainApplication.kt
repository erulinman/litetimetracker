package info.erulinman.lifetimetracker

import android.app.Application

import info.erulinman.lifetimetracker.data.AppDatabase
import info.erulinman.lifetimetracker.data.PresetRepository
import info.erulinman.lifetimetracker.data.WayRepository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainApplication: Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { AppDatabase.getInstance(this, applicationScope) }
    val wayListRepository by lazy { WayRepository(database.wayDao()) }
    val pomodoroPresetRepository by lazy { PresetRepository(database.pomodoroPresetDao()) }
}