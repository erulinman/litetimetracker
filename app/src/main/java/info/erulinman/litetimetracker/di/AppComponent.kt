package info.erulinman.litetimetracker.di

import dagger.Component
import info.erulinman.litetimetracker.categories.CategoryListFragment
import info.erulinman.litetimetracker.di.module.AppModule
import info.erulinman.litetimetracker.presets.PresetListFragment
import info.erulinman.litetimetracker.timer.TimerService
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(fragment: CategoryListFragment)
    fun inject(fragment: PresetListFragment)
    fun inject(timerService: TimerService)
}