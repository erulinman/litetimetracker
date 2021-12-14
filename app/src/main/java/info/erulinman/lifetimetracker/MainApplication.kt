package info.erulinman.lifetimetracker

import android.app.Application
import info.erulinman.lifetimetracker.di.AppComponent
import info.erulinman.lifetimetracker.di.module.AppModule
import info.erulinman.lifetimetracker.di.DaggerAppComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainApplication: Application() {

    private lateinit var _appComponent: AppComponent

    val appComponent: AppComponent
        get() = _appComponent

    override fun onCreate() {
        super.onCreate()
        _appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this, CoroutineScope(SupervisorJob())))
            .build()
    }
}