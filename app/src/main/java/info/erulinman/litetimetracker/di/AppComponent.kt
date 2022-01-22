package info.erulinman.litetimetracker.di

import dagger.Component
import info.erulinman.litetimetracker.di.module.AppModule
import info.erulinman.litetimetracker.features.categories.CategoryListFragment
import info.erulinman.litetimetracker.features.presets.PresetListFragment
import info.erulinman.litetimetracker.features.timer.TimerService
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(fragment: CategoryListFragment)
    fun inject(fragment: PresetListFragment)
    fun inject(timerService: TimerService)
}