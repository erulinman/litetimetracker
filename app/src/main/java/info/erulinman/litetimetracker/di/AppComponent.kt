package info.erulinman.litetimetracker.di

import dagger.Component
import info.erulinman.litetimetracker.TimerService
import info.erulinman.litetimetracker.di.module.AppModule
import info.erulinman.litetimetracker.fragments.CategoryListFragment
import info.erulinman.litetimetracker.fragments.PresetListFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(fragment: CategoryListFragment)
    fun inject(fragment: PresetListFragment)
    fun inject(timerService: TimerService)
}