package info.erulinman.lifetimetracker

import android.app.Application
import info.erulinman.lifetimetracker.data.DatabaseRepository

import info.erulinman.lifetimetracker.data.database.AppDatabase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainApplication: Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { AppDatabase.getInstance(this, applicationScope) }

    val databaseRepository by lazy { DatabaseRepository(database) }
}