package info.erulinman.litetimetracker

import android.app.Application
import info.erulinman.litetimetracker.di.AppComponent
import info.erulinman.litetimetracker.di.module.AppModule
import info.erulinman.litetimetracker.di.DaggerAppComponent
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